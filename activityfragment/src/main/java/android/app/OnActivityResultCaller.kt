package android.app

import android.content.Intent

internal fun Activity.callOnActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    onActivityResult(requestCode, resultCode, data)
}