package open.source.ability

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStore

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class AbilityShellActivity : FragmentActivity() {

    private val abilityCompatDelegate by lazy {
        AbilityCompat.getDelegate(
            this,
            object : AbilityCompat.DelegateProperties {
                override val viewModelStore: ViewModelStore
                    get() = super@AbilityShellActivity.getViewModelStore()
                override val isChangingConfigurations: Boolean
                    get() = super@AbilityShellActivity.isChangingConfigurations()
            }
        )
    }

    override fun isChangingConfigurations(): Boolean {
        return abilityCompatDelegate.isChangingConfigurations
    }

    override fun getViewModelStore(): ViewModelStore {
        return abilityCompatDelegate.viewModelStore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<ViewGroup>(android.R.id.content).removeAllViews()
        if (savedInstanceState == null) {
            val f = Ability()
            f.intent = intent.getParcelableExtra(Ability.ABILITY_INTENT)
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, f)
                .commitNow()
        }
    }
}