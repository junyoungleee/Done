package com.palette.done.view.my

object LevelType {
    private val level = arrayListOf("새싹해냄이", "해린이", "프로해냄러", "갓생해린이")
    val type = mapOf(Pair("p", "즉흥러"), Pair("j", "계획러"))

    var levelName = ""
    fun getLevel(lv: Int) {
        levelName = when(lv) {
            in(1..3) -> level[0]
            in(4..6) -> level[1]
            in(7..8) -> level[2]
            in(9..10) -> level[3]
            else -> ""
        }
    }
}
