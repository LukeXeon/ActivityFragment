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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelStore
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
        manager?.dispatchCreate(savedInstanceState?.getBundle(STATE_KEY))
        val activity = requireActivity()
        val fm = activity.fragmentManager
        if (fm.findFragmentByTag(who) == null) {
            val dispatcher = ActivityResultDispatcher()
            dispatcher.who = who
            requireActivity().fragmentManager
                .beginTransaction()
                .add(dispatcher, who)
                .hide(dispatcher)
                .commit()
        }
        val intent = intent
        if (manager?.currentActivity?.intent
                ?.toUri(Intent.URI_INTENT_SCHEME) != intent?.toUri(Intent.URI_INTENT_SCHEME)
        ) {
            manager?.removeAllActivities()
            manager?.startActivity(
                who,
                WrapIntent(intent)
            )
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
        manager?.dispatchDestroy(requireActivity().isFinishing)
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
        fun getViewModelStore(activity: ComponentActivity): ViewModelStore? {
            if (activity.parent == null) {
                return activity.viewModelStore
            }
            return runCatching {
                FragmentManager.findFragment<ActivityFragment>(
                    activity.window.decorView
                )
            }.getOrNull()?.viewModelStore
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