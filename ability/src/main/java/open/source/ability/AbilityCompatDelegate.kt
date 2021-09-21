package open.source.ability

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

abstract class AbilityCompatDelegate {
    abstract fun getViewModelStore(): ViewModelStore
    abstract fun isChangingConfigurations(): Boolean
    abstract fun onApplyThemeResource(theme: Resources.Theme, resId: Int, first: Boolean)

    interface Fallback {
        fun superGetViewModelStore(): ViewModelStore
        fun superIsChangingConfigurations(): Boolean
        fun superOnApplyThemeResource(theme: Resources.Theme, resId: Int, first: Boolean)
    }

    companion object {
        @JvmStatic
        fun <T> create(
            activity: T
        ): AbilityCompatDelegate where T : ComponentActivity, T : Fallback {
            return AbilityCompatDelegateImpl(activity)
        }
    }
}