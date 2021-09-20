package open.source.ability

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

object AbilityCompat {

    interface Delegate {
        val viewModelStore: ViewModelStore
        val isChangingConfigurations: Boolean
        fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean)
    }

    object RequestPermissionDelegate : ActivityCompat.PermissionCompatDelegate {
        override fun requestPermissions(
            activity: Activity,
            permissions: Array<String?>,
            requestCode: Int
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val root = getRootActivity(activity) ?: return false
                val who = activity.embeddedID
                val shadow = root.fragmentManager.findFragmentByTag(who) ?: return false
                @Suppress("DEPRECATION")
                shadow.requestPermissions(permissions, requestCode)
                return true
            }
            return false
        }

        override fun onActivityResult(
            activity: Activity,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ): Boolean {
            return false
        }
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
    fun requestPermissions(
        activity: Activity,
        permissions: Array<String?>,
        @IntRange(from = 0) requestCode: Int
    ) {
        if (activity.parent == null) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        } else {
            RequestPermissionDelegate.requestPermissions(activity, permissions, requestCode)
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