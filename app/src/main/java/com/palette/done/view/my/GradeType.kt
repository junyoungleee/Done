package com.palette.done.view.my

object GradeType {
    private val grade = arrayListOf("새싹해냄이", "해린이", "프로해냄러", "갓생해린이")
    val type = mapOf(Pair("p", "즉흥러"), Pair("j", "계획러"))

    var gradeName = ""
    fun getGradeName(lv: Int): String {
        gradeName = when(lv) {
            in(1..3) -> grade[0]
            in(4..6) -> grade[1]
            in(7..8) -> grade[2]
            in(9..10) -> grade[3]
            else -> ""
        }
        return gradeName
    }

    var gradeNum = 1
    fun getGrade(lv: Int): Int {
        gradeNum = when(lv) {
            in(1..3) -> 1
            in(4..6) -> 2
            in(7..8) -> 3
            in(9..10) -> 4
            else -> 0
        }
        return gradeNum
    }
}