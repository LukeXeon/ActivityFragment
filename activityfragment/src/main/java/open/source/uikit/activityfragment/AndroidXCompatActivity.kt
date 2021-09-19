package open.source.uikit.activityfragment

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore


open class AndroidXCompatActivity : AppCompatActivity() {

    @Suppress("LeakingThis")
    private val androidXCompatDelegate by lazy {
        AndroidXCompatDelegate.create(
            this,
            object : AndroidXCompatDelegate.HostProperties {
                override val viewModelStore: ViewModelStore
                    get() = super@AndroidXCompatActivity.getViewModelStore()
                override val isChangingConfigurations: Boolean
                    get() = super@AndroidXCompatActivity.isChangingConfigurations()
            }
        )
    }

    override fun isChangingConfigurations(): Boolean {
        return androidXCompatDelegate.isChangingConfigurations
    }

    override fun getViewModelStore(): ViewModelStore {
        return androidXCompatDelegate.viewModelStore
    }
}