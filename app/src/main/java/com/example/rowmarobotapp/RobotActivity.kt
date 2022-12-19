package com.rowma.rowmarobotapp;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rowma.rowma_kotlin.Rowma
import org.json.JSONArray
import org.json.JSONObject


class RobotActivity : AppCompatActivity() {
    val rowma = Rowma(
        "https://rowma.moriokalab.com" +
                "",
        "app1"
    )
    private var robot: JSONObject = JSONObject()
    private var vel_flag: Boolean = true
    private var ActiveNodeList : Array<String> = arrayOf()




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot)


        val robotString = intent.getStringExtra("ROBOT")
        robot = JSONObject(robotString)
        var moverobot = robot.getString("uuid")
        val robotUuidTextView: TextView = findViewById(R.id.robotUuid)
        robotUuidTextView.append("  " + robot.getString("uuid" ))

        rowma.connect()
        val vel = JSONObject()
        var commandList: JSONArray = JSONArray()
        var launchcommandslist: ArrayList<String> = ArrayList();
        commandList = robot.getJSONArray("launchCommands")
        println(commandList)
        if (commandList != null) {
            for (i in 0 until commandList.length()) {
                launchcommandslist.add(commandList.getString(i))
            }
        }
        val robot_cmd: Button = findViewById(R.id.LCL)
        robot_cmd.setOnClickListener {
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                launchcommandslist
            )
            println(launchcommandslist)
            val listView = findViewById(R.id.lcl) as ListView
            listView.adapter = arrayAdapter
        }

        val listViewlist = findViewById(R.id.lcl) as ListView
        listViewlist.setOnItemClickListener { parent, view, position, id ->
            println(launchcommandslist[position])
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("ROSプログラムの実行")
                .setMessage(launchcommandslist[position])
                .setPositiveButton("上のlaunchコマンドを実行", { dialog, which ->
                    // TODO:Yesが押された時の挙動
                    rowma.runLaunch(robot.getString("uuid"), launchcommandslist[position])
                    ActiveNodeList += launchcommandslist[position]
                })
                .setNegativeButton("中止", { dialog, which ->
                    // TODO:Noが押された時の挙動
                })
                .show()
        }


        var commandList2: JSONArray = JSONArray()
        var nodecommandslist: ArrayList<String> = ArrayList();
        commandList2 = robot.getJSONArray("rosrunCommands")
        println(commandList2)
        if (commandList2 != null) {
            for (i in 0 until commandList2.length()) {
                nodecommandslist.add(commandList2.getString(i))
            }
        }
        val robot_cmd2: Button = findViewById(R.id.NCL)
        robot_cmd2.setOnClickListener {
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                nodecommandslist
            )
            println(nodecommandslist)
            val listView2 = findViewById(R.id.ncl) as ListView
            listView2.adapter = arrayAdapter
        }

        val listViewlist2 = findViewById(R.id.ncl) as ListView
        listViewlist2.setOnItemClickListener { parent, view, position, id ->
            println(nodecommandslist[position])
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("ROSプログラムの実行")
                .setMessage(nodecommandslist[position])
                .setPositiveButton("上のrosrunコマンドを実行", { dialog, which ->
                    // TODO:Yesが押された時の挙動
                    rowma.runRosrun(robot.getString("uuid"), nodecommandslist[position], "")
                    ActiveNodeList += "/talker"
                })
                .setNegativeButton("中止", { dialog, which ->
                    // TODO:Noが押された時の挙動
                })
                .show()
        }

        /*
        val robotbutton: Button = findViewById(R.id.ANL)
        robotbutton.setOnClickListener {
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("ROSプログラムの停止")
                .setMessage("現在実行中のROSノードをすべて停止します。")
                .setPositiveButton("上のrosrunコマンドを実行", { dialog, which ->
                    // TODO:Yesが押された時の挙動
                    rowma.killNodes(robot.getString("uuid"), ActiveNodeList)
                })
                .setNegativeButton("中止", { dialog, which ->
                    // TODO:Noが押された時の挙動
                })
                .show()

        }
         */

        val robotbutton: Button = findViewById(R.id.robot_go)
        robotbutton.setOnClickListener {
            var vel_x = 0.15
            vel.put("data", vel_x.toString())
            if (vel_flag == true) {
                rowma.publish(moverobot, "/vel", vel)
                Log.d(TAG, "vel_flag is true")
            }
        }

        val robotbutton2: Button = findViewById(R.id.robot_left)
        robotbutton2.setOnClickListener {
            var vel_x = 0.5
            vel.put("data", vel_x.toString())
            if (vel_flag == true) {
                rowma.publish(moverobot, "/ang", vel)
            }
        }

        val robotbutton3: Button = findViewById(R.id.robot_right)
        robotbutton3.setOnClickListener {
            var vel_x = -0.5
            vel.put("data", vel_x.toString())
            if (vel_flag == true) {
                rowma.publish(moverobot, "/ang", vel)
            }
        }

        val robotbutton4: Button = findViewById(R.id.robot_back)
        robotbutton4.setOnClickListener {
            var vel_x = -0.15

            vel.put("data", vel_x.toString())
            if (vel_flag == true) {
                rowma.publish(moverobot, "/vel", vel)
            }
        }

        val robotbutton5: Button = findViewById(R.id.robot_stop)
        robotbutton5.setOnClickListener {
            var vel_x = 0.0
            vel.put("data", vel_x.toString())
            if (vel_flag == true) {
                rowma.publish(moverobot, "/vel", vel)
                rowma.publish(moverobot, "/ang", vel)
            }
        }
    }
}
