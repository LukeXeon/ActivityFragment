package open.source.ability

import android.content.res.Resources
import androidx.lifecycle.ViewModelStore

interface AbilityCompatDelegate {
    val viewModelStore: ViewModelStore
    val isChangingConfigurations: Boolean
    fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean)
}