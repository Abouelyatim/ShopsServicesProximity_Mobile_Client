package com.smartcity.client.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object{

        private val TAG: String = "AppDebug"

        // dates from server look like this: "2019-07-23T03:28:01.406944Z"
        fun convertServerStringDateToLong(sd: String): Long{
            var stringDate = sd.removeRange(sd.indexOf("T") until sd.length)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                val time = sdf.parse(stringDate).time
                return time
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertLongToStringDate(longDate: Long): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                val date = sdf.format(Date(longDate))
                return date
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertStringToStringDate(date: String): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
            try {
                val date = sdf.format(sdf.parse(date)).replace("T"," ")
                return date
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertStringToStringDateSimpleFormat(date: String): String{
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
            val test=parser.parse(date)
            val formatter  = SimpleDateFormat("d MMM", Locale.ENGLISH)
            val result= formatter.format(test)
            try {

                return result
            } catch (e: Exception) {
                throw Exception(e)
            }
        }
    }


}