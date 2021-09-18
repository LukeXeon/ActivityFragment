package open.source.uikit.activityfragment

import android.app.Activity
import android.os.Bundle
import androidx.annotation.RestrictTo

@Deprecated("Used only as a stub.", level = DeprecationLevel.HIDDEN)
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class StubActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}