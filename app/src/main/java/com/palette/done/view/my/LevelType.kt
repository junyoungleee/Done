package com.palette.done.view.my

object LevelType {

    fun getDayCondition(level: Int): Int {
        return when (level) {
            1 -> 1
            2 -> 10
            3 -> 25
            4 -> 40
            5 -> 60
            6 -> 70
            7 -> 100
            8 -> 150
            9 -> 270
            10 -> 360
            else -> 0
        }
    }

    fun getCountCondition(level: Int): Int {
        return when (level) {
            1 -> 0
            2 -> 50
            3 -> 125
            4 -> 280
            5 -> 360
            6 -> 450
            7 -> 700
            8 -> 900
            9 -> 1800
            10 -> 2800
            else -> 0
        }
    }

    fun getProgressPercent(level: Int, cumulated: Int): Int {
        val nextLevel = level + 1
        val leftDays = getDayCondition(nextLevel)
        val leftDone = getCountCondition(nextLevel)

        val left = leftDays + leftDone
        return cumulated * 100 / left
    }
}