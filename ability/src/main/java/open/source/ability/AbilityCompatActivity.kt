package open.source.ability

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore

open class AbilityCompatActivity : AppCompatActivity() {

    private val abilityCompatDelegate by lazy {
        AbilityCompat.getDelegate(
            this,
            object : AbilityCompat.DelegateProperties {
                override val viewModelStore: ViewModelStore
                    get() = super@AbilityCompatActivity.getViewModelStore()
                override val isChangingConfigurations: Boolean
                    get() = super@AbilityCompatActivity.isChangingConfigurations()
            }
        )
    }

    override fun isChangingConfigurations(): Boolean {
        return abilityCompatDelegate.isChangingConfigurations
    }

    override fun getViewModelStore(): ViewModelStore {
        return abilityCompatDelegate.viewModelStore
    }
}