package open.source.ability

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi

internal class AbilityShellActivity : AbilityCompatActivity(),
    ViewTreeObserver.OnPreDrawListener {

    private var ability: Ability? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = findViewById<ViewGroup>(android.R.id.content)
        content.removeAllViews()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            content.viewTreeObserver.addOnPreDrawListener(this)
        }
        if (savedInstanceState == null) {
            val f = Ability()
            f.intent = intent.getParcelableExtra(Ability.ABILITY_INTENT)
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, f)
                .commitNow()
            ability = f
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPreDraw(): Boolean {
        val f = ability ?: supportFragmentManager.findFragmentById(android.R.id.content)
        if (f is Ability) {
            val w = f.currentActivity?.window
            if (w != null) {
                if (window.statusBarColor != w.statusBarColor) {
                    window.statusBarColor = w.statusBarColor
                }
                if (window.navigationBarColor != w.navigationBarColor) {
                    window.navigationBarColor = w.navigationBarColor
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && window.navigationBarDividerColor != w.navigationBarDividerColor) {
                    window.navigationBarDividerColor = w.navigationBarDividerColor
                }
            }
            ability = f
        }
        return true
    }
}