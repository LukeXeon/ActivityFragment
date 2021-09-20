package open.source.ability

import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

object AbilityCompat {

    interface DelegateProperties {
        val viewModelStore: ViewModelStore
        val isChangingConfigurations: Boolean
    }

    private class AbilityDelegate(
        private val ability: Ability
    ) : DelegateProperties {

        override val viewModelStore: ViewModelStore
            get() = ability.viewModelStore

        override val isChangingConfigurations: Boolean
            get() = ability.rootActivity?.isChangingConfigurations ?: false
    }

    @JvmStatic
    fun getDelegate(
        activity: ComponentActivity,
        default: DelegateProperties
    ): DelegateProperties {
        var delegate: DelegateProperties? = null
        val parent = getRootActivity(activity.parent) as? FragmentActivity
        if (parent != null) {
            val who = activity.embeddedID
            val fragment = findTargetAbility(parent.supportFragmentManager) {
                it.isInstance(who)
            }
            if (fragment != null) {
                delegate = AbilityDelegate(fragment)
            }

        }
        if (delegate == null) {
            delegate = default
        }
        return delegate
    }
}