package open.source.uikit.ability.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Vm : ViewModel() {
    val text = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
    }
}