package com.example.rowmarobotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.rowma.rowma_kotlin.Rowma
import com.rowma.rowmarobotapp.R
import com.rowma.rowmarobotapp.RobotActivity
import com.rowma.rowmarobotapp.TBCameraActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.ArrayList

class Select_camera : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_camera)

        val robotListView: ListView = findViewById(R.id.camera_list)

        getCurrentRobots()

        robotListView.setOnItemClickListener { parent, view, position, id ->
                val intent: Intent = Intent(this, TBCameraActivity::class.java)
                intent.putExtra("ROBOT", robots[position].toString())
                this.startActivity(intent)
        }


    }
    private fun getCurrentRobots() = GlobalScope.launch(Dispatchers.Main) {
        async(Dispatchers.Default) { rowma.currentConnectionList() }.await().let {
            robots = JSONArray(it.toString())
            for (i in 0 until robots.length()) {
                val item = robots.getJSONObject(i)
                robotUuids.add(item.getString("uuid"))
            }
            val robotListView: ListView = findViewById(R.id.camera_list)
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                robotUuids
            )
            robotListView.adapter = arrayAdapter
        }
    }
}