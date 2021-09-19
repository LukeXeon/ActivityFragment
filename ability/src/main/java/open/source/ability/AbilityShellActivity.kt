package open.source.ability

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

class AbilityShellActivity : AbilityCompatActivity() {

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
            f.intent = intent.getParcelableExtra(Ability.ABILITY_INTENT)
            supportFragmentManager
                .beginTransaction()
                .add(CONTENT_ID, f)
                .commitNow()
        }
    }

    companion object {
        private val CONTENT_ID = ViewCompat.generateViewId()
    }
}