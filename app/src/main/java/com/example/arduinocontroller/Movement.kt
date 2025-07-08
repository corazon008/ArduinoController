package com.example.arduinocontroller

enum class Movement {
    LEFT,
    RIGHT,
    FORWARD,
    BACKWARD,
    STOP;

    companion object {
        fun fromString(value: String): Movement? {
            return when (value.lowercase()) {
                "left" -> LEFT
                "right" -> RIGHT
                "forward" -> FORWARD
                "backward" -> BACKWARD
                "stop" -> STOP
                else -> null
            }
        }

        fun toString(movement: Movement): String {
            return when (movement) {
                LEFT -> "left"
                RIGHT -> "right"
                FORWARD -> "forward"
                BACKWARD -> "backward"
                STOP -> "stop"
            }
        }
    }
}