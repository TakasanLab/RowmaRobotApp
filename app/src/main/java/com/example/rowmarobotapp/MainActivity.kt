package com.rowma.rowmarobotapp


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.rowmarobotapp.Select_camera
import com.rowma.rowma_kotlin.Rowma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {
    val rowma = Rowma(
        "https://rowma.moriokalab.com" +
                "",
        "app1"
    )

    companion object {
        const val EXTRA_MESSAGE = "rowma"
    }

    private var robots: JSONArray = JSONArray() // 追加
    private val robotUuids: ArrayList<String> = ArrayList() // 追加
    private var flg = false;



    //内部ストレージ書き込み許可
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentRobots()


        val robotListView: ListView = findViewById(R.id.robot_list)
        robotListView.setOnItemClickListener { parent, view, position, id ->
            if(flg == true) {
                val intent: Intent = Intent(this, RobotActivity::class.java)
                intent.putExtra("ROBOT", robots[position].toString())
                this.startActivity(intent)
                flg = false;
            }
            //rowma.runLaunch("robot-a", "joy_twist joy_twist.launch")
        }
        val robotMapView: Button = findViewById(R.id.robot_map)
        robotMapView.setOnClickListener {
            val intent: Intent = Intent(this, RobotMapActivity::class.java)
            this.startActivity(intent)

        }

        val robotMapView2: Button = findViewById(R.id.robot_map2)
        robotMapView2.setOnClickListener {
            val intent: Intent = Intent(this, RobotMapActivity2::class.java)
            this.startActivity(intent)

        }

        val robotMapView3: Button = findViewById(R.id.robot_map3)
        robotMapView3.setOnClickListener {
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("ROSノードの遠隔実行 ")
                .setMessage("遠隔実行を行うロボットを上記のUUID一覧から選んでください")
                .setPositiveButton("OK", { dialog, which ->
                    // TODO:Yesが押された時の挙動
                    flg = true
                })
                .show()


        }

        val robotMapView4: Button = findViewById(R.id.robot_map4)
        robotMapView4.setOnClickListener {
            val intent2: Intent = Intent(this, Select_camera::class.java)
            this.startActivity(intent2)
        }


    }

    private fun getCurrentRobots() = GlobalScope.launch(Dispatchers.Main) {
        async(Dispatchers.Default) { rowma.currentConnectionList() }.await().let {
            robots = JSONArray(it.toString())
            for (i in 0 until robots.length()) {
                val item = robots.getJSONObject(i)
                robotUuids.add(item.getString("uuid"))
            }
            val robotListView: ListView = findViewById(R.id.robot_list)
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                robotUuids
            )
            robotListView.adapter = arrayAdapter
        }
    }

}

