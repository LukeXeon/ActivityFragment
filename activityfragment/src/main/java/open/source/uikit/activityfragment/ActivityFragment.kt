@file:Suppress("DEPRECATION")

package open.source.uikit.activityfragment

import android.app.Activity
import android.app.LocalActivityManager
import android.app.callOnActivityResult
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*
import kotlin.collections.HashMap

class ActivityFragment : Fragment() {

    private val noConfig = ViewModelProvider(
        viewModelStore,
        ViewModelProvider.NewInstanceFactory()
    )[NonConfigViewModel::class.java]
    private var manager: LocalActivityManager? = null
    private var who: String? = null

    var intent: Intent?
        get() = arguments?.getParcelable(INTENT_KEY)
        set(value) {
            val args = arguments ?: Bundle()
            args.putParcelable(INTENT_KEY, value)
            arguments = args
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        manager = LocalActivityManager(requireActivity(), true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager?.dispatchCreate(savedInstanceState?.getBundle(STATE_KEY))
        who = savedInstanceState?.getString(WHO_KEY) ?: UUID.randomUUID().toString()
        val activity = requireActivity()
        val fm = activity.fragmentManager
        val old = fm.findFragmentByTag(who)
        if (old == null) {
            val dispatcher = ActivityResultDispatcher()
            dispatcher.who = who
            fm.beginTransaction()
                .add(dispatcher, who)
                .commit()
        }
        val intent = intent
        if (manager?.currentActivity?.intent
                ?.toUri(Intent.URI_INTENT_SCHEME) != intent?.toUri(Intent.URI_INTENT_SCHEME)
        ) {
            manager?.removeAllActivities()
            val instance = noConfig.instance
            var restore: Any? = null
            if (instance != null) {
                restore = mLastNonConfigurationInstancesField.get(activity)
                val temp = newNonConfigurationInstances.newInstance()
                val map = HashMap<String?, Any>()
                map[who] = instance
                childrenField.set(temp, map)
                mLastNonConfigurationInstancesField.set(activity, temp)
            }
            manager?.startActivity(
                who,
                intent
            )
            if (instance != null) {
                mLastNonConfigurationInstancesField.set(activity, restore)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return manager?.currentActivity?.window?.decorView
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
        val isFinishing = requireActivity().isFinishing
        val activity = manager?.currentActivity
        if (!isFinishing && activity != null) {
            noConfig.instance = activity.onRetainNonConfigurationInstance()
        }
        manager?.dispatchDestroy(isFinishing)
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
        val activity = manager?.currentActivity ?: return
        activity.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        manager?.currentActivity?.callOnActivityResult(requestCode, resultCode, data)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal class ActivityResultDispatcher : android.app.Fragment() {

        var who: String?
            get() = arguments?.getString(WHO_KEY)
            set(value) {
                val args = arguments ?: Bundle()
                args.putString(WHO_KEY, value)
                arguments = args
            }

        private fun findTargetFragment(
            fm: FragmentManager = (activity as FragmentActivity).supportFragmentManager,
            who: String? = this.who
        ): Fragment? {
            for (f in fm.fragments) {
                if (f is ActivityFragment && f.who == who) {
                    return f
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
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal class NonConfigViewModel : ViewModel() {
        var instance: Any? = null

        override fun onCleared() {
            instance = null
        }
    }

    companion object {

        private val mWhoField by lazy {
            android.app.Fragment::class.java
                .getDeclaredField("mWho")
                .apply {
                    isAccessible = true
                }
        }

        private val mLastNonConfigurationInstancesField by lazy {
            Activity::class.java
                .getDeclaredField("mLastNonConfigurationInstances")
                .apply {
                    isAccessible = true
                }
        }

        private val newNonConfigurationInstances by lazy {
            mLastNonConfigurationInstancesField
                .type.getDeclaredConstructor()
                .apply {
                    isAccessible = true
                }
        }

        private val childrenField by lazy {
            mLastNonConfigurationInstancesField
                .type
                .getDeclaredField("children")
                .apply {
                    isAccessible = true
                }
        }

        private const val WHO_KEY = "android:who"
        private const val INTENT_KEY = "android:intent"
        private const val STATE_KEY = "android:state"
    }
}