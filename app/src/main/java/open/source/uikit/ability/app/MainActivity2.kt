package open.source.uikit.ability.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import open.source.ability.Ability
import open.source.ability.AbilityCompatActivity
import open.source.ability.startActivityAsAbility

private var isT = true

class MainActivity2 : AbilityCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val vm = ViewModelProvider(viewModelStore, defaultViewModelProviderFactory)[Vm::class.java]
        val tv = findViewById<TextView>(R.id.text)
        tv.setOnClickListener {
            startActivityAsAbility(
                Intent(this@MainActivity2, MainActivity3::class.java),
                requestCode = 1000
            )
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