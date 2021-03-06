package open.source.uikit.ability.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import open.source.ability.Ability
import open.source.ability.useActivityCompatRequestPermissionsDelegate

private var isT = true

class MainActivity : AppCompatActivity() {

    init {
        useActivityCompatRequestPermissionsDelegate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val start = SystemClock.uptimeMillis()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.xxxx1, Ability().apply {
                    intent = Intent(this@MainActivity, MainActivity2::class.java)
                })
                .add(R.id.xxxx2, Ability().apply {
                    intent = Intent(this@MainActivity, MainActivity2::class.java)
                })
                .commitNow()
            Log.d(TAG, "onCreate: " + (SystemClock.uptimeMillis() - start))
        }
        val tv = findViewById<TextView>(R.id.xxxxxxxx)
        val vm = ViewModelProvider(viewModelStore, defaultViewModelProviderFactory)[Vm::class.java]
        vm.text.observe(this) {
            tv.text = it
        }
        if (isT) {
            isT = false
            vm.text.value = "111111"
        }
    }

    override fun startActivityFromChild(child: Activity, intent: Intent?, requestCode: Int) {
        super.startActivityFromChild(child, intent, requestCode)
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}