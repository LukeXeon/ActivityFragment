package open.source.ability

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore

open class AbilityCompatActivity : AppCompatActivity(), AbilityCompatDelegate.Fallback {

    private val abilityCompatDelegate by lazy {
        AbilityCompatDelegate.create(this)
    }

    override fun isChangingConfigurations(): Boolean {
        return abilityCompatDelegate.isChangingConfigurations()
    }

    override fun getViewModelStore(): ViewModelStore {
        return abilityCompatDelegate.getViewModelStore()
    }

    override fun onApplyThemeResource(theme: Resources.Theme, resid: Int, first: Boolean) {
        abilityCompatDelegate.onApplyThemeResource(theme, resid, first)
    }

    override fun superGetViewModelStore(): ViewModelStore {
        return super.getViewModelStore()
    }

    override fun superIsChangingConfigurations(): Boolean {
        return super.isChangingConfigurations()
    }

    override fun superOnApplyThemeResource(theme: Resources.Theme, resId: Int, first: Boolean) {
        return super.onApplyThemeResource(theme, resId, first)
    }
}