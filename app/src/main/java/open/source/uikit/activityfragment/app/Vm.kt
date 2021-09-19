package open.source.uikit.activityfragment.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Vm : ViewModel() {
    val text = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
    }
}