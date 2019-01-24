package com.game.aries.streamingshop.utilities

import java.util.*

fun generateOrderId(): String {
    val first = (Random().nextInt(26) + 65).toChar().toString()
    val second = (Random().nextInt(26) + 65).toChar().toString()
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val randomNum = (0..9999).random()

    val rand = String.format("%0" + 4 + "d", randomNum)
    val mon = String.format("%0" + 2 + "d", month)
    val days = String.format("%0" + 2 + "d", day)
    val hou = String.format("%0" + 2 + "d", hour)
    val min = String.format("%0" + 2 + "d", minute)

    val str = StringBuffer("")
    str.append(first).append(second).append(year % 100).append(mon).append(days).append(hou).append(min).append(rand)

    return str.toString()
}
