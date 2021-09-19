package open.source.uikit.activityfragment

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore


open class AndroidXCompatActivity : AppCompatActivity() {

    @Suppress("LeakingThis")
    private val androidXCompatDelegate by lazy {
        AndroidXCompatDelegate.create(
            this,
            { super.getViewModelStore() },
            { super.isChangingConfigurations() }
        )
    }

    override fun isChangingConfigurations(): Boolean {
        return androidXCompatDelegate.isChangingConfigurations
    }

    override fun getViewModelStore(): ViewModelStore {
        return androidXCompatDelegate.viewModelStore
    }
}