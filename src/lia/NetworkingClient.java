package lia;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the connection to the game engine and takes
 * care of sending and retrieving data.
 **/
public class NetworkingClient extends WebSocketClient {

    private Bot myBot;

    private static Exception illegalArgumentsException = new Exception(
            "Illegal arguments. See --help for the correct structure."
    );

    public static NetworkingClient connectNew(String[] args, Bot myBot) throws Exception {
        String botId = "";
        String port = "8887";

        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            System.out.println("HELP!!");
            return null;
        }

        // Parse arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-p") || arg.equals("--port")) {
                if (i + 1 < args.length) {
                    port = args[i + 1];
                } else {
                    throw illegalArgumentsException;
                }
            }
            else if (arg.equals("-i") || arg.equals("--id")) {
                if (i + 1 < args.length) {
                    botId = args[i + 1];
                } else {
                    throw illegalArgumentsException;
                }
            }
        }

        // Setup headers
        Map<String,String> httpHeaders = new HashMap<>();
        httpHeaders.put("Id", botId);

        NetworkingClient c = new NetworkingClient(new URI("ws://localhost:" + port), httpHeaders, myBot);
        c.connect();

        return c;
    }

    private NetworkingClient(URI serverUri, Map<String, String> httpHeaders, Bot myBot) {
        super(serverUri, httpHeaders);
        this.myBot = myBot;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Exiting...");
        System.exit(0);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        if (!isOpen()) {
            System.exit(1);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {
        try {
            Api response = new Api();

            if (message.contains(MessageType.GAME_ENVIRONMENT.toString())) {
                GameEnvironment gameEnvironment = GameEnvironment.Companion.parse(message);
                response.setUid(gameEnvironment.getUid());
                myBot.processGameEnvironment(gameEnvironment);

            } else if (message.contains(MessageType.GAME_STATE.toString())) {
                GameState gameState = GameState.Companion.parse(message);
                response.setUid(gameState.getUid());
                myBot.processGameState(gameState, response);
            }
            send(response.toJson());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
