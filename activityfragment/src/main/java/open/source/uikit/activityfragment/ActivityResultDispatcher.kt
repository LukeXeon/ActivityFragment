package open.source.uikit.activityfragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ActivityResultDispatcher : android.app.Fragment() {

    var who: String?
        get() = arguments?.getString(WHO_KEY)
        set(value) {
            val args = arguments ?: Bundle()
            args.putString(WHO_KEY, value)
            arguments = args
        }

    init {
        retainInstance = true
    }

    private fun findTargetFragment(
        fm: FragmentManager = (activity as FragmentActivity)
            .supportFragmentManager,
        who: String? = this.who
    ): Fragment? {
        for (f in fm.fragments) {
            if (f is ActivityFragment) {
                if (f.who == who) {
                    return f
                }
            } else {
                val c = findTargetFragment(f.childFragmentManager, who)
                if (c != null) {
                    return c
                }
            }
        }
        return null
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mWhoField.set(this, who)
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
        private const val WHO_KEY = "android:who"

        private val mWhoField by lazy {
            android.app.Fragment::class.java
                .getDeclaredField("mWho")
                .apply {
                    isAccessible = true
                }
        }
    }
}