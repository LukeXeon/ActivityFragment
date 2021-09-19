@file:Suppress("DEPRECATION")

package open.source.uikit.activityfragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ActivityResultDispatcher : android.app.Fragment() {

    init {
        retainInstance = true
    }

    private fun findTargetFragment(): Fragment? {
        return ActivityFragment.findTargetFragment(
            (activity as FragmentActivity).supportFragmentManager
        ) { it.who == tag }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mWhoField.set(this, tag)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        findTargetFragment()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        findTargetFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        private val mWhoField by lazy {
            android.app.Fragment::class.java
                .getDeclaredField("mWho")
                .apply {
                    isAccessible = true
                }
        }

        fun dispatchCreate(activity: Activity, who: String?) {
            val fm = activity.fragmentManager
            var f = fm.findFragmentByTag(who)
            if (f == null && !fm.isDestroyed) {
                f = ActivityResultDispatcher()
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

        fun dispatchDestroy(activity: Activity, who: String?) {
            val fm = activity.fragmentManager
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