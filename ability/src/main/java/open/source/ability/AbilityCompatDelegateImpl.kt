package open.source.ability

import android.content.res.Resources
import androidx.lifecycle.ViewModelStore

internal class AbilityCompatDelegateImpl(
    private val ability: Ability
) : AbilityCompatDelegate {

    override val viewModelStore: ViewModelStore
        get() = ability.viewModelStore

    override val isChangingConfigurations: Boolean
        get() = ability.rootActivity?.isChangingConfigurations ?: false

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        theme?.applyStyle(resid, true)
    }
}