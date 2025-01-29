package id.ardev.keretakita.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JadwalKA(
    val no_ka: String = "",
    val name_ka: String = "",
    val kelas_ka: String = "",
    val lintas: String = "",
    val time_berangkat: String = "",
    val time_tujuan: String = "",
    val stops: List<Stop> = emptyList()
) : Parcelable {
    constructor() : this("", "", "", "", "", "", emptyList())
}

@Parcelize
data class Stop(
    val station_name: String = "",
    val station_kode: String = "",
    val arrival_time: String? = null,
    val departure_time: String? = null
) : Parcelable {
    constructor() : this("","", null, null)
}