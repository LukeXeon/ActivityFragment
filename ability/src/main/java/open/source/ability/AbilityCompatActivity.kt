package open.source.ability

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore

open class AbilityCompatActivity : AppCompatActivity() {

    private val abilityCompatDelegate by lazy {
        AbilityCompat.getDelegate(
            this,
            object : AbilityCompat.Delegate {
                override val viewModelStore: ViewModelStore
                    get() = super@AbilityCompatActivity.getViewModelStore()
                override val isChangingConfigurations: Boolean
                    get() = super@AbilityCompatActivity.isChangingConfigurations()

                override fun onApplyThemeResource(
                    theme: Resources.Theme?,
                    resid: Int,
                    first: Boolean
                ) {
                    super@AbilityCompatActivity.onApplyThemeResource(theme, resid, first)
                }
            }
        )
    }

    override fun isChangingConfigurations(): Boolean {
        return abilityCompatDelegate.isChangingConfigurations
    }

    override fun getViewModelStore(): ViewModelStore {
        return abilityCompatDelegate.viewModelStore
    }

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        abilityCompatDelegate.onApplyThemeResource(theme, resid, first)
    }
}