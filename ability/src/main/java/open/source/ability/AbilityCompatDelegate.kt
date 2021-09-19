package open.source.ability

import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

object AbilityCompatDelegate {

    interface HostProperties {
        val viewModelStore: ViewModelStore
        val isChangingConfigurations: Boolean
    }

    private class AbilityDelegate(
        private val ability: Ability
    ) : HostProperties {

        override val viewModelStore: ViewModelStore
            get() = ability.viewModelStore

        override val isChangingConfigurations: Boolean
            get() = ability.rootActivity?.isChangingConfigurations ?: false
    }

    @JvmStatic
    fun create(
        activity: ComponentActivity,
        default: HostProperties
    ): HostProperties {
        var delegate: HostProperties? = null
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