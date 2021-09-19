package open.source.ability

import android.app.Activity
import androidx.fragment.app.FragmentManager

internal fun findTargetAbility(
    fm: FragmentManager,
    predicate: (Ability) -> Boolean
): Ability? {
    for (f in fm.fragments) {
        if (f is Ability) {
            if (predicate(f)) {
                return f
            }
        } else {
            val c = findTargetAbility(f.childFragmentManager, predicate)
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
