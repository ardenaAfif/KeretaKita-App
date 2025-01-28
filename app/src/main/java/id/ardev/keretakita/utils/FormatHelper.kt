package id.ardev.keretakita.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FormatHelper {

    fun formatDate(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set ke WIB (GMT +7)
        return dateFormat.format(Date())
    }

    fun calculateDuration(timeBerangkat: String, timeSampai: String): Pair<Int, Int> {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set ke WIB (GMT +7)

        try {
            val dateBerangkat = sdf.parse(timeBerangkat) ?: throw ParseException(timeBerangkat, 0)
            val dateSampai = sdf.parse(timeSampai) ?: throw ParseException(timeSampai, 0)

            var durationInMillis = dateSampai.time - dateBerangkat.time

            // Jika waktu sampai lebih awal dari waktu berangkat, tambahkan 24 jam
            if (durationInMillis < 0) {
                durationInMillis += 24 * 60 * 60 * 1000
            }

            val hours = (durationInMillis / (1000 * 60 * 60)).toInt()
            val minutes = (durationInMillis / (1000 * 60) % 60).toInt()
            val isNextDay = durationInMillis >= 24 * 60 * 60 * 1000

            return Pair(hours, minutes)
        } catch (e: ParseException) {
            e.printStackTrace()
            return Pair(0, 0)
        }
    }

}