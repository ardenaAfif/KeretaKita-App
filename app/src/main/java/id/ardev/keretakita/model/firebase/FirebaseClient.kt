package id.ardev.keretakita.model.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirebaseClient {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllStasiun() : QuerySnapshot {
        return firestore.collection("stasiun")
            .get().await()
    }
}