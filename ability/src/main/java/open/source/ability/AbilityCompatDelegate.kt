package open.source.ability

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

class AbilityCompatDelegate<T>(
    private val activity: T
) where T : ComponentActivity, T : AbilityCompatDelegate.Fallback {

    private val ability: Ability? by lazy {
        if (!activity.isChild) {
            return@lazy null
        }
        var ability: Ability? = null
        val root = activity.rootActivity as? FragmentActivity
        if (root != null) {
            val who = activity.embeddedID
            if (!who.isNullOrEmpty() && who.startsWith("ability:")) {
                ability = findTargetAbility(root.supportFragmentManager) {
                    it.who == who
                }
            }
        }
        return@lazy ability
    }

    fun getViewModelStore(): ViewModelStore {
        return ability?.viewModelStore ?: activity.superGetViewModelStore()
    }

    fun isChangingConfigurations(): Boolean {
        return if (activity.isChild) {
            requireNotNull(activity.rootActivity).isChangingConfigurations
        } else {
            activity.superIsChangingConfigurations()
        }
    }

    fun onApplyThemeResource(
        theme: Resources.Theme,
        resId: Int,
        first: Boolean
    ) {
        if (activity.isChild) {
            theme.applyStyle(resId, true)
        } else {
            activity.superOnApplyThemeResource(theme, resId, first)
        }
    }

    interface Fallback {
        fun superGetViewModelStore(): ViewModelStore
        fun superIsChangingConfigurations(): Boolean
        fun superOnApplyThemeResource(theme: Resources.Theme, resId: Int, first: Boolean)
    }

    companion object {
        @JvmStatic
        fun <T> create(
            activity: T
        ): AbilityCompatDelegate<T> where T : ComponentActivity, T : Fallback {
            return AbilityCompatDelegate(activity)
        }
    }
}