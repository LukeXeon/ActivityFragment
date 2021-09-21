@file:Suppress("DEPRECATION")

package open.source.ability

import android.app.Activity
import android.app.LocalActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.util.*

class Ability : Fragment() {
    private var manager: LocalActivityManager? = null
    internal var who: String? = null
        private set
    internal val currentActivity: Activity?
        get() = manager?.currentActivity

    private fun requireRootActivity(): Activity {
        return requireNotNull(activity.rootActivity)
    }

    var intent: Intent?
        get() = arguments?.getParcelable(ABILITY_INTENT)
        set(value) {
            val args = arguments ?: Bundle()
            args.putParcelable(ABILITY_INTENT, value)
            arguments = args
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        manager = LocalActivityManager(requireRootActivity(), true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        who = savedInstanceState?.getString(ABILITY_WHO) ?: "ability:" + UUID.randomUUID()
        AbilityShadowFragment.dispatchCreate(activity, who)
        manager?.dispatchCreate(savedInstanceState?.getBundle(ABILITY_STATE))
        val intent = intent
        manager?.startActivity(who, WrapIntent(intent))
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
        outState.putBundle(ABILITY_STATE, manager?.saveInstanceState())
        outState.putString(ABILITY_WHO, who)
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
        manager?.dispatchPause(requireRootActivity().isFinishing)
    }

    override fun onDestroy() {
        super.onDestroy()
        AbilityShadowFragment.dispatchDestroy(activity, who)
        manager?.dispatchDestroy(requireRootActivity().isFinishing)
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
        manager?.currentActivity?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        manager?.currentActivity?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        internal const val ABILITY_INTENT = "ability:intent"
        private const val ABILITY_WHO = "ability:who"
        private const val ABILITY_STATE = "ability:state"
    }
}