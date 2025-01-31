package id.ardev.keretakita.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stasiun(
    val nameStasiun: String = "",
    val kodeStasiun: String = "",
    val infoStasiunUrl: String = ""
) : Parcelable {
    constructor() : this("", "", "")
}