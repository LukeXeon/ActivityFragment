package open.source.uikit.activityfragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class WrapIntent : Intent {
    private constructor(parcel: Parcel) {
        readFromParcel(parcel)
    }

    constructor(o: Intent?) : super(o)

    @SuppressLint("QueryPermissionsNeeded")
    override fun resolveActivityInfo(
        pm: PackageManager,
        flags: Int
    ): ActivityInfo {
        val componentName = requireNotNull(component)
        return Intent().apply {
            component = ComponentName(
                componentName.packageName,
                ShellActivity::class.java.name
            )
        }.resolveActivityInfo(pm, flags).apply {
            name = componentName.className
        }
    }

    companion object CREATOR : Parcelable.Creator<WrapIntent> {
        override fun createFromParcel(parcel: Parcel): WrapIntent {
            return WrapIntent(parcel)
        }

        override fun newArray(size: Int): Array<WrapIntent?> {
            return arrayOfNulls(size)
        }
    }
}