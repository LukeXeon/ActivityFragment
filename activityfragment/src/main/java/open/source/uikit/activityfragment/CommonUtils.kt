package open.source.uikit.activityfragment

import android.app.Activity
import androidx.fragment.app.FragmentManager

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

internal fun getRootActivity(activity: Activity?): Activity? {
    var act: Activity? = activity
    while (act != null && act.parent != null) {
        act = act.parent
    }
    return act
}