@file:Suppress("DEPRECATION")

package open.source.uikit.activityfragment

import android.app.Activity
import android.app.LocalActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.util.*

class ActivityFragment : Fragment() {
    private var manager: LocalActivityManager? = null
    internal var who: String? = null

    var intent: Intent?
        get() = arguments?.getParcelable(INTENT_KEY)
        set(value) {
            val args = arguments ?: Bundle()
            args.putParcelable(INTENT_KEY, value)
            arguments = args
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = requireActivity()
        manager = LocalActivityManager(activity, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        who = savedInstanceState?.getString(WHO_KEY) ?: "activity_fragment:" + UUID.randomUUID()
        ActivityResultDispatcher.dispatchCreate(requireActivity(), who)
        manager?.dispatchCreate(savedInstanceState?.getBundle(STATE_KEY))
        val intent = intent
        if (manager?.currentActivity?.intent?.component != intent?.component) {
            manager?.removeAllActivities()
            manager?.startActivity(who, WrapIntent(intent))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return (manager?.currentActivity?.window?.decorView as? ViewGroup)?.apply {
            isFocusableInTouchMode = true
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(STATE_KEY, manager?.saveInstanceState())
        outState.putString(WHO_KEY, who)
    }

    override fun onResume() {
        super.onResume()
        manager?.dispatchResume()
    }

    override fun onStop() {
        super.onStop()
        manager?.dispatchStop()
    }

    override fun onPause() {
        super.onPause()
        manager?.dispatchPause(requireActivity().isFinishing)
    }

    override fun onDestroy() {
        super.onDestroy()
        val activity = requireActivity()
        ActivityResultDispatcher.dispatchDestroy(activity, who)
        manager?.dispatchDestroy(activity.isFinishing)
    }

    override fun onDetach() {
        super.onDetach()
        manager?.removeAllActivities()
        manager = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        manager?.currentActivity?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        manager?.currentActivity?.let {
            onActivityResultMethod.invoke(
                it,
                requestCode,
                resultCode,
                data
            )
        }
    }

    companion object {

        @JvmStatic
        fun getViewModelStoreOwnerDelegate(
            activity: ComponentActivity,
            original: () -> ViewModelStore
        ): ViewModelStoreOwner {
            var owner: ViewModelStoreOwner? = null
            val parent = activity.parent as? FragmentActivity
            if (parent != null) {
                val who = mEmbeddedIDField.get(activity) as String
                val fragment = findTargetFragment(parent.supportFragmentManager) {
                    it.who == who
                }
                if (fragment != null) {
                    owner = ViewModelStoreOwner {
                        if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED && parent.isChangingConfigurations) {
                            ViewModelStore()
                        } else {
                            fragment.viewModelStore
                        }
                    }
                }
            }
            if (owner == null) {
                owner = ViewModelStoreOwner { original() }
            }
            return owner
        }

        internal fun findTargetFragment(
            fm: FragmentManager,
            predicate: (ActivityFragment) -> Boolean
        ): ActivityFragment? {
            for (f in fm.fragments) {
                if (f is ActivityFragment) {
                    if (predicate(f)) {
                        return f
                    }
                } else {
                    val c = findTargetFragment(f.childFragmentManager, predicate)
                    if (c != null) {
                        return c
                    }
                }
            }
            return null
        }

        private val mEmbeddedIDField by lazy {
            Activity::class.java
                .getDeclaredField("mEmbeddedID")
                .apply {
                    isAccessible = true
                }
        }

        private val onActivityResultMethod by lazy {
            Activity::class.java
                .getDeclaredMethod(
                    "onActivityResult",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Intent::class.java
                ).apply {
                    isAccessible = true
                }
        }

        private const val WHO_KEY = "android:who"
        private const val INTENT_KEY = "android:intent"
        private const val STATE_KEY = "android:state"
    }
}