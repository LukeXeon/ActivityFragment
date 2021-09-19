package open.source.uikit.activityfragment.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import open.source.uikit.activityfragment.ActivityFragment
import open.source.uikit.activityfragment.AndroidXCompatActivity
import open.source.uikit.activityfragment.ShellActivity

private var isT = true

class MainActivity2 : AndroidXCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val vm = ViewModelProvider(viewModelStore, defaultViewModelProviderFactory)[Vm::class.java]
        val tv = findViewById<TextView>(R.id.text)
        tv.setOnClickListener {
            startActivityForResult(Intent(this, ShellActivity::class.java).apply {
                putExtra(
                    ShellActivity.START_INTENT_KEY,
                    Intent(this@MainActivity2, MainActivity2::class.java)
                )
            }, 1000)
        }
        vm.text.observe(this) {
            tv.text = it
        }
        if (isT) {
            isT = false
            vm.text.value = "2222222"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, MainActivity2::class.java.name, Toast.LENGTH_SHORT).show()
    }

}