package open.source.ability

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

object AndroidXCompatDelegate {

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

    private val mEmbeddedIDField by lazy {
        Activity::class.java
            .getDeclaredField("mEmbeddedID")
            .apply {
                isAccessible = true
            }
    }

    @JvmStatic
    fun create(
        activity: ComponentActivity,
        default: HostProperties
    ): HostProperties {
        var delegate: HostProperties? = null
        val parent = getRootActivity(activity.parent) as? FragmentActivity
        if (parent != null) {
            val who = mEmbeddedIDField.get(activity) as String
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