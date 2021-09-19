package open.source.uikit.ability.app

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DoraemonKit.install(this)
    }
}