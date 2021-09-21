package open.source.ability

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat

object RequestPermissionDelegate : ActivityCompat.PermissionCompatDelegate {
    override fun requestPermissions(
        activity: Activity,
        permissions: Array<String?>,
        requestCode: Int
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val root = activity.rootActivity ?: return false
            val who = activity.embeddedID
            val shadow = root.fragmentManager.findFragmentByTag(who) ?: return false
            @Suppress("DEPRECATION")
            shadow.requestPermissions(permissions, requestCode)
            return true
        }
        return false
    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        return false
    }
}