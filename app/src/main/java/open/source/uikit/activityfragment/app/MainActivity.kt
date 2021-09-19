package open.source.uikit.activityfragment.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import open.source.uikit.activityfragment.ActivityFragment

private var isT = true

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.xxxx1, ActivityFragment().apply {
                    intent = Intent(this@MainActivity, MainActivity2::class.java)
                })
                .add(R.id.xxxx2, ActivityFragment().apply {
                    intent = Intent(this@MainActivity, MainActivity2::class.java)
                })
                .commitNow()
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

    override fun onDestroy() {
        super.onDestroy()
    }


}