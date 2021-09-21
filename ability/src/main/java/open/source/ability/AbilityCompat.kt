@file:JvmName("AbilityCompat")

package open.source.ability

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
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

internal val Activity?.rootActivity: Activity?
    get() {
        var act: Activity? = this
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


@JvmOverloads
fun Context.startActivityAsAbility(
    intent: Intent,
    options: Bundle? = null,
    requestCode: Int = -1
) {
    val shell = Intent(this, AbilityShellActivity::class.java)
    var addFlags = false
    if (this is Application) {
        addFlags = true
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && options != null) {
            val launchTaskId = options.getInt("android.activity.launchTaskId", -1)
            if (launchTaskId == -1) {
                val am = this.getSystemService(Context.ACTIVITY_SERVICE)
                        as ActivityManager
                val task = am.getRunningTasks(1).firstOrNull()
                if (task != null) {
                    if (task.topActivity?.packageName == this.applicationInfo.packageName) {
                        options.putInt("android.activity.launchTaskId", task.id)
                        addFlags = false
                    }
                }
            }
        }
    }
    if (addFlags) {
        shell.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    shell.putExtra(Ability.ABILITY_INTENT, intent)
    ActivityCompat.setPermissionCompatDelegate(RequestPermissionDelegate)
    if (requestCode < 0 || this !is Activity) {
        this.startActivity(shell, options)
    } else {
        this.startActivityForResult(shell, requestCode, options)
    }
}


fun requestPermissions(
    activity: Activity,
    permissions: Array<String?>,
    @IntRange(from = 0) requestCode: Int
) {
    if (activity.parent == null) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    } else {
        RequestPermissionDelegate.requestPermissions(activity, permissions, requestCode)
    }
}

fun getAbilityCompatDelegate(
    activity: ComponentActivity,
    default: AbilityCompatDelegate
): AbilityCompatDelegate {
    var delegate: AbilityCompatDelegate? = null
    val root = activity.rootActivity as? FragmentActivity
    if (root != null) {
        val who = activity.embeddedID
        var ability: Ability? = null
        if (who != null) {
            ability = findTargetAbility(root.supportFragmentManager) {
                it.who == who
            }
        }
        if (ability != null) {
            delegate = AbilityCompatDelegateImpl(ability)
        }

    }
    if (delegate == null) {
        delegate = default
    }
    return delegate
}