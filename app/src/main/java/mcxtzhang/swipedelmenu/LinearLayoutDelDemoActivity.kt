package mcxtzhang.swipedelmenu

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mcxtzhang.listswipemenudemo.R

class LinearLayoutDelDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linear_layout_del_demo)

        findViewById<android.view.View>(R.id.llContent).setOnClickListener {
            Toast.makeText(this@LinearLayoutDelDemoActivity, "内容区域被点击", Toast.LENGTH_SHORT).show()
        }
        findViewById<android.view.View>(R.id.btnDelete).setOnClickListener {
            Toast.makeText(this@LinearLayoutDelDemoActivity, "删除按钮被点击", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.llContent2).setOnClickListener {
            Toast.makeText(this@LinearLayoutDelDemoActivity, "第二个内容区域被点击", Toast.LENGTH_SHORT).show()
        }
        findViewById<android.view.View>(R.id.btnDelete2).setOnClickListener {
            Toast.makeText(this@LinearLayoutDelDemoActivity, "第二个删除按钮被点击", Toast.LENGTH_SHORT).show()
        }
    }
}
