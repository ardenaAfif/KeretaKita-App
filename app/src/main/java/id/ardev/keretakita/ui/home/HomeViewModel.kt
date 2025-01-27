package id.ardev.keretakita.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        fetchStasiunData()
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
}