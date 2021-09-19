package open.source.uikit.activityfragment

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

abstract class AndroidXCompatDelegate : ViewModelStoreOwner {
    abstract val isChangingConfigurations: Boolean

    private class DefaultDelegate(
        private val getViewModelStoreOp: () -> ViewModelStore,
        private val isChangingConfigurationsOp: () -> Boolean
    ) : AndroidXCompatDelegate() {
        override val isChangingConfigurations: Boolean
            get() = isChangingConfigurationsOp()

        override fun getViewModelStore(): ViewModelStore {
            return getViewModelStoreOp()
        }
    }

    private class ContainerFragmentDelegate(
        private val fragment: ActivityFragment
    ) : AndroidXCompatDelegate() {
        override fun getViewModelStore(): ViewModelStore {
            return fragment.viewModelStore
        }

        override val isChangingConfigurations: Boolean
            get() {
                return fragment.rootActivity?.isChangingConfigurations ?: false
            }
    }

    companion object {

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
            getViewModelStore: () -> ViewModelStore,
            isChangingConfigurations: () -> Boolean
        ): AndroidXCompatDelegate {
            var delegate: AndroidXCompatDelegate? = null
            val parent = activity.parent as? FragmentActivity
            if (parent != null) {
                val who = mEmbeddedIDField.get(activity) as String
                val fragment = ActivityFragment.findTargetFragment(parent.supportFragmentManager) {
                    it.who == who
                }
                if (fragment != null) {
                    delegate = ContainerFragmentDelegate(fragment)
                }

            }
            if (delegate == null) {
                delegate = DefaultDelegate(getViewModelStore, isChangingConfigurations)
            }
            return delegate
        }
    }
}