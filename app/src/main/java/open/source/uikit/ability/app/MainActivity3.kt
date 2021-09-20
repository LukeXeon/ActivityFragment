package open.source.uikit.ability.app

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import open.source.ability.AbilityCompatActivity

class MainActivity3 : AbilityCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.CALL_PHONE
            ),
            1000
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, "请求权限返回", Toast.LENGTH_SHORT).show()
    }
}