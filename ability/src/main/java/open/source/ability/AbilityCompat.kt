@file:JvmName("AbilityCompat")

package open.source.ability

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
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

internal val Activity?.rootActivity: Activity?
    get() {
        var act: Activity? = this
        while (act != null && act.parent != null) {
            act = act.parent
        }
        return act
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
    if (requestCode < 0 || this !is Activity) {
        this.startActivity(shell, options)
    } else {
        this.startActivityForResult(shell, requestCode, options)
    }
}

fun useActivityCompatRequestPermissionsDelegate() {
    ActivityCompat.setPermissionCompatDelegate(RequestPermissionDelegate)
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