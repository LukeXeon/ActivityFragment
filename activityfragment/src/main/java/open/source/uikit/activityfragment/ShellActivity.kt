package open.source.uikit.activityfragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelStore

class ShellActivity : AppCompatActivity() {

    private val viewModelStoreOwnerDelegate by lazy {
        ActivityFragment.getViewModelStoreOwnerDelegate(this) {
            super.getViewModelStore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val content = FrameLayout(this)
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        content.id = CONTENT_ID
        setContentView(content)
        if (savedInstanceState == null) {
            val f = ActivityFragment()
            f.intent = intent.getParcelableExtra(START_INTENT_KEY)
            supportFragmentManager
                .beginTransaction()
                .add(CONTENT_ID, f)
                .commitNow()
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStoreOwnerDelegate.viewModelStore
    }

    companion object {
        private val CONTENT_ID = ViewCompat.generateViewId()
        const val START_INTENT_KEY = "android:start_intent"
    }
}