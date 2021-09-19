package open.source.ability

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import androidx.fragment.app.FragmentManager
import java.lang.reflect.InvocationTargetException

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

internal fun Activity.onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    try {
        onActivityResultMethod.invoke(this, requestCode, resultCode, data)
    } catch (e: InvocationTargetException) {
        throw e.targetException
    }
}

private val mWhoField by lazy {
    Fragment::class.java
        .getDeclaredField("mWho")
        .apply {
            isAccessible = true
        }
}

internal var Fragment.who: String?
    get() = mWhoField.get(this) as? String
    set(value) {
        mWhoField.set(this, value)
    }

private val mEmbeddedIDField by lazy {
    Activity::class.java
        .getDeclaredField("mEmbeddedID")
        .apply {
            isAccessible = true
        }
}

internal val Activity.embeddedID: String?
    get() = mEmbeddedIDField.get(this) as? String