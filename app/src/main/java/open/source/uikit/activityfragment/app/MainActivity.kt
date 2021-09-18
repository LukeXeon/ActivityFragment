package open.source.uikit.activityfragment.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import open.source.uikit.activityfragment.ActivityFragment

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
    }
}