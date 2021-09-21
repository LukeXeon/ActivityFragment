@file:Suppress("DEPRECATION")

package open.source.ability

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class AbilityShadowFragment : Fragment() {

    init {
        retainInstance = true
    }

    private var target: WeakReference<Ability>? = null

    private fun findTargetAbility(): Ability? {
        var t = target?.get()
        if (t == null) {
            t = findTargetAbility(
                (activity as FragmentActivity).supportFragmentManager
            ) { it.who == tag }
            if (t != null) {
                target = WeakReference(t)
            }
        }
        return t
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        who = tag
    }

    override fun onDetach() {
        super.onDetach()
        target = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        findTargetAbility()?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        findTargetAbility()?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    companion object {

        fun dispatchCreate(activity: Activity?, who: String?) {
            val root = activity.rootActivity ?: return
            val fm = root.fragmentManager
            var f = fm.findFragmentByTag(who)
            if (f == null && !fm.isDestroyed) {
                f = AbilityShadowFragment()
                val t = fm.beginTransaction()
                    .add(f, who)
                    .hide(f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t.commitNowAllowingStateLoss()
                } else {
                    t.commitAllowingStateLoss()
                    fm.executePendingTransactions()
                }
            }
        }

        fun dispatchDestroy(activity: Activity?, who: String?) {
            val root = activity.rootActivity ?: return
            val fm = root.fragmentManager
            val f = fm.findFragmentByTag(who)
            if (f != null && !fm.isDestroyed) {
                val t = fm.beginTransaction()
                    .remove(f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t.commitNowAllowingStateLoss()
                } else {
                    t.commitAllowingStateLoss()
                    fm.executePendingTransactions()
                }
            }
        }
    }
}