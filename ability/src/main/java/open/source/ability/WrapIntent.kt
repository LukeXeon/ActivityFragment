package open.source.ability

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
        var info = super.resolveActivityInfo(pm, flags)
        if (info == null) {
            val componentName = requireNotNull(component)
            info = Intent().apply {
                component = ComponentName(
                    componentName.packageName,
                    AbilityShellActivity::class.java.name
                )
            }.resolveActivityInfo(pm, flags).apply {
                name = componentName.className
            }
        }
        return info
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