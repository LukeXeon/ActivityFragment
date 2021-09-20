package open.source.ability

import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

object AbilityCompat {

    interface Delegate {
        val viewModelStore: ViewModelStore
        val isChangingConfigurations: Boolean
        fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean)
    }

    private class AbilityDelegate(
        private val ability: Ability
    ) : Delegate {

        override val viewModelStore: ViewModelStore
            get() = ability.viewModelStore

        override val isChangingConfigurations: Boolean
            get() = ability.rootActivity?.isChangingConfigurations ?: false

        override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
            theme?.applyStyle(resid, true)
        }
    }

    @JvmStatic
    fun getDelegate(
        activity: ComponentActivity,
        default: Delegate
    ): Delegate {
        var delegate: Delegate? = null
        val root = getRootActivity(activity.parent) as? FragmentActivity
        if (root != null) {
            val who = activity.embeddedID
            var ability: Ability? = null
            if (who != null) {
                ability = findTargetAbility(root.supportFragmentManager) {
                    it.isInstance(who)
                }
            }
            if (ability != null) {
                delegate = AbilityDelegate(ability)
            }

        }
        if (delegate == null) {
            delegate = default
        }
        return delegate
    }
}