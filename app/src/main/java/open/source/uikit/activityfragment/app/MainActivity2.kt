package open.source.uikit.activityfragment.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import open.source.uikit.activityfragment.ShellActivity

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        findViewById<View>(R.id.text).setOnClickListener {
            startActivityForResult(Intent(this, ShellActivity::class.java).apply {
                putExtra(
                    ShellActivity.START_INTENT_KEY,
                    Intent(this@MainActivity2, MainActivity3::class.java)
                )
            }, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, MainActivity2::class.java.name, Toast.LENGTH_SHORT).show()
    }
}