package id.ardev.keretakita.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.model.data.Stasiun
import id.ardev.keretakita.model.firebase.FirebaseClient
import id.ardev.keretakita.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val firebaseClient: FirebaseClient
) : ViewModel() {

    private val _stasiunList = MutableStateFlow<Resource<List<Stasiun>>>(Resource.Unspecified())
    val stasiunList: StateFlow<Resource<List<Stasiun>>> = _stasiunList

    private val _jadwalList = MutableStateFlow<Resource<List<JadwalKA>>>(Resource.Unspecified())
    val jadwalList: StateFlow<Resource<List<JadwalKA>>> = _jadwalList

    init {
        fetchStasiunData()
        getJadwalByNamaKa()
    }

    private fun fetchStasiunData() {
        viewModelScope.launch {
            _stasiunList.emit(Resource.Loading())

            try {
                val querySnapshot = firebaseClient.getAllStasiun()
                val stasiunList = querySnapshot.toObjects(Stasiun::class.java)
                Log.d(">>FirebaseData", "Data: $stasiunList")
                _stasiunList.emit(Resource.Success(stasiunList))
            } catch (exception: Exception) {
                _stasiunList.emit(Resource.Error(exception.message.toString()))
            }
        }
    }

    private fun getJadwalByNamaKa() {
        viewModelScope.launch {
            try {
                val snapshot = firebaseClient.getAllJadwalKa()
                val jadwalKAList = snapshot.toObjects(JadwalKA::class.java)
                Log.d(">>FirebaseData", "Data: $jadwalKAList")
                _jadwalList.value = Resource.Success(jadwalKAList)
            } catch (e: Exception) {
                _jadwalList.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}