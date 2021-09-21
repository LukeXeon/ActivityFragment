package open.source.ability

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

internal class AbilityCompatDelegateImpl<T>(
    private val activity: T
) : AbilityCompatDelegate() where T : ComponentActivity, T : AbilityCompatDelegate.Fallback {

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

    override fun getViewModelStore(): ViewModelStore {
        return ability?.viewModelStore ?: activity.superGetViewModelStore()
    }

    override fun isChangingConfigurations(): Boolean {
        return if (activity.isChild) {
            requireNotNull(activity.rootActivity).isChangingConfigurations
        } else {
            activity.superIsChangingConfigurations()
        }
    }

    override fun onApplyThemeResource(
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
}