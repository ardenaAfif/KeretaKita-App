package id.ardev.keretakita.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FormatHelper {

    fun formatDate(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set ke WIB (GMT +7)
        return dateFormat.format(Date())
    }
}