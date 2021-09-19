package open.source.ability

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

class AbilityShellActivity : AndroidXCompatActivity() {

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
            val f = Ability()
            f.intent = intent.getParcelableExtra(ABILITY_INTENT)
            supportFragmentManager
                .beginTransaction()
                .add(CONTENT_ID, f)
                .commitNow()
        }
    }

    companion object {
        const val ABILITY_INTENT = "ability:intent"

        private val CONTENT_ID = ViewCompat.generateViewId()
    }
}