package id.ardev.keretakita.model.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import id.ardev.keretakita.model.data.JadwalKA
import kotlinx.coroutines.tasks.await

class FirebaseClient {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllStasiun() : QuerySnapshot {
        return firestore.collection("stasiun")
            .get().await()
    }

    suspend fun getAllJadwalKa() : QuerySnapshot {
        return firestore.collection("train")
            .get().await()
    }

    suspend fun getKaByStasiun(stBerangkat: String, stTujuan: String): List<JadwalKA> {
        val snapshot = firestore.collection("train").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val jadwal = doc.toObject(JadwalKA::class.java)
            if (jadwal != null) {
                val stops = jadwal.stops.map { it.station_name }
                if (stBerangkat in stops && stTujuan in stops &&
                    stops.indexOf(stBerangkat) < stops.indexOf(stTujuan)
                ) {
                    jadwal
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

}