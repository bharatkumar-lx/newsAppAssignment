package com.bharat.newsappassignment.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utilities {
    private val VALID_EMAIL_ADDRESS_REGEX: Pattern =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

    fun validate(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }

    fun getPublishTime(date: String , source: String?) : String{
        var c : Calendar = Calendar.getInstance()
        val time = date.split("T")[1]
        val sdf = SimpleDateFormat("HH:mm:ss")
        val publishTimeMillis: Long = sdf.parse(time)!!.time
        val currentTime: String = sdf.format(c.time)
        val millis: Long = sdf.parse(currentTime)!!.time
        val t = millis - publishTimeMillis
        val df = SimpleDateFormat("HH")
        return df.format(t) + " hours ago "+ source

    }

}