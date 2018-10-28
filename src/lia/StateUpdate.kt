package lia

import com.google.gson.GsonBuilder


data class StateUpdate(
        var uid: Long,
        var type: MessageType,
        var time: Float,
        var nOpponentsAlive: Int,
        var units: Array<UnitData>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun parse(msg: String): StateUpdate {
            return gson.fromJson(msg, StateUpdate::class.java)
        }
    }
}

data class UnitData(
        var id: Int,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientation: Float,
        var speed: Speed,
        var rotationSpeed: RotationSpeed,
        var canShoot: Boolean,
        var nBullets: Int,
        var opponentsInView: Array<OpponentInView>,
        var opponentBulletsInView: Array<BulletInView>,
        var navigationPath: Array<PointOnPath>?
)

data class PointOnPath(
        var x: Float,
        var y: Float
)

data class OpponentInView(
        var id: Int,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientation: Float
)

data class BulletInView(
        var x: Float,
        var y: Float,
        var orientation: Float,
        var velocity: Float
)

enum class Speed {
    NONE, FORWARD, BACKWARD
}

enum class RotationSpeed {
    NONE, LEFT, RIGHT, SLOW_LEFT, SLOW_RIGHT
}