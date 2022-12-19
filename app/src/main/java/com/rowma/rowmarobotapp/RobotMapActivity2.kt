package com.rowma.rowmarobotapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.rowma.rowma_kotlin.Rowma
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Math.abs
import java.util.*
import android.os.Bundle as Bundle1


class RobotMapActivity2 : AppCompatActivity() {
    //var poseData: Any = TODO();
    val rowma = Rowma(
        "https://rowma.moriokalab.com" +
                "",
        "app1"
    )

    private val robotPositions: MutableMap<String, JSONObject> = mutableMapOf()
    private var mapView: LinearLayout? = null

    private var robots: JSONArray = JSONArray() // 追加
    val robotUuids: ArrayList<String> = ArrayList() // 追加

    private var circle_x = 0
    private var circle_y = 0

    private var json_x = 0
    private var json_y = 0
    private var json_x2 = 0
    private var json_y2 = 0
    private var flag: Boolean = false;
    private var draw_goal_point: Boolean = false;
    private var draw_init_point: Boolean = false;

    private var touchD_x = 0
    private var touchD_y = 0
    private var touchpose_x = 100
    private var touchpose_y = 100
    private var goal_point_x = 0
    private var goal_point_y = 0
    private var init_point_x = 0
    private var init_point_y = 0



    val msg_pose_x = JSONObject()
    val msg_pose_y = JSONObject()
    val msg_pose_x2 = JSONObject()
    val msg_pose_y2 = JSONObject()

    private var touchedRobotUuid: String = ""

    private var chosenRobot: String = ""


    override fun onCreate(savedInstanceState: Bundle1?) {
        super.onCreate(savedInstanceState)

        rowma.connect()
        rowma.subscribe("/amcl_poseToApp", { data -> robot_pose_subscribe(data) })
        //rowma.runLaunch("robot-a", "roslaunch wifi_radio_intensity_map rowma_pos_read.launch")
        mapView = LinearLayout(this)
        //startTestSubscribing()
        setContentView(mapView)

        getCurrentRobots()

    }

    private fun startTestSubscribing() {
        GlobalScope.launch {
            repeat(1000) {
                val pose_x = 9
                val pose_y = -4
                val pose_x_test2 = -24
                val pose_y_test2 = 4
                // robotPositions["test1"] = JSONObject("{ \"header\": { \"seq\": 97, \"stamp\": { \"secs\": 1614059967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": 48.0509179473, \"y\": 59.3796744676, \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                // robotPositions["test2"] = JSONObject("{ \"header\": { \"seq\": 98, \"stamp\": { \"secs\": 1614069967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": 28.0509179473, \"y\": 39.3796744676, \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                robotPositions["abc"] =
                    JSONObject("{ \"header\": { \"seq\": 97, \"stamp\": { \"secs\": 1614059967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": " + pose_x + ", \"y\": " + pose_y + ", \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
               // robotPositions["def"] =
                  //  JSONObject("{ \"header\": { \"seq\": 97, \"stamp\": { \"secs\": 1614059967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": " + pose_x_test2 + ", \"y\": " + pose_y_test2 + ", \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                /*
                println(pose_x)
                println(json_y)
                println(pose_x_test2)
                println(pose_y_test2)
                */
                //mapView!!.invalidate()
                delay(100000L)
            }
        }
    }


    private fun robot_pose_subscribe(data: Any) {
        println(data.toString())
        println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        val jObject: JSONObject = JSONObject(data.toString())
        val sourceUuid = jObject.get("sourceUuid")
        robotPositions[sourceUuid.toString()] = JSONObject(jObject.get("msg").toString())
        //mapView!!.invalidate()
    }

    private fun extractPositionFromMsg(obj: JSONObject): List<Int> {
        val json_position =
            obj.getJSONObject("pose").getJSONObject("pose").getJSONObject("position")
        json_x = json_position.getInt("y") + 6
        json_y = json_position.getInt("x") + 24
        json_y2 = json_y * 16
        if (json_x <= 0) {
            json_x2 = (json_x * 25).toInt()
        } else {
            json_x2 = json_x * 21
        }
        //return listOf<Int>(json_position.getInt("x"), json_position.getInt("y"))
        return listOf<Int>(json_x2, json_y2)

    }


    internal inner class LinearLayout(context: Context) : View(context) {
        private val paint = Paint()
        override fun onDraw(canvas: Canvas) {

            // 描画するラインの太さ
            val lineStrokeWidth = 2f
            val strokeW1 = 20f

            // ペイントする色の設定
            paint.color = Color.argb(255, 0, 0, 0)
            // ペイントストロークの太さを設定
            paint.strokeWidth = lineStrokeWidth
            // Styleのストロークを設定する
            paint.style = Paint.Style.STROKE
            // drawRectを使って線を描画する、引数に座標を設定
            // (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)

            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val disp = wm.defaultDisplay

            val size = Point()
            disp.getSize(size)

            val screenWidth = size.x
            val screenHeight = size.y

            var bmp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.nakano_11f_rotate)

            val resizedBitmap = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight-150,false)
            canvas.drawBitmap(resizedBitmap, 10f, 0f, null);

            val dp = resources.displayMetrics.density
            // Circle
            for ((k, v) in robotPositions) {
                paint.color = Color.BLUE
                paint.strokeWidth = strokeW1
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE

                //count_receiver()
                val position = extractPositionFromMsg(v)
                circle_x = (position[0] * dp).toInt() + 280
                circle_y = (position[1] * dp).toInt() + 160
                if (290 < circle_x && circle_x < 370) {
                    circle_x = 330
                }
                println("circleX = ${circle_x}")
                println("circleY = ${circle_y}")

                //circle_x = (pose_x * dp).toInt()
                //circle_y = (pose_y * dp).toInt()

                canvas.drawCircle(circle_x.toFloat(), circle_y.toFloat(), 4 * dp, paint)


            }


            if(draw_goal_point == true) {
                //canvas.drawCircle(goal_point_x.toFloat(),goal_point_y.toFloat(), 4 * dp, paint)
                paint.color = Color.BLUE
                paint.strokeWidth = strokeW1
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                canvas.drawCircle(goal_point_x * dp, goal_point_y * dp, 4 * dp, paint)
                println("goal_x = ${goal_point_x}")
                print("goal_y = ${goal_point_y}")
            }

            if(draw_init_point == true){
                paint.color = Color.RED
                paint.strokeWidth = strokeW1
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                canvas.drawCircle(init_point_x.toFloat(), init_point_y.toFloat(), 4 * dp, paint)
            }


            invalidate()
            //println(i)
            //paint.color = Color.TRANSPARENT
        }

    }




    private fun startPublishingWifiInfo(uuid: String) {
        touchedRobotUuid = uuid
    }

    private fun getCurrentRobots() = GlobalScope.launch(Dispatchers.Main) {
        async(Dispatchers.Default) { rowma.currentConnectionList() }.await().let {
            robots = JSONArray(it.toString())
            for (i in 0 until robots.length()) {
                val item = robots.getJSONObject(i)
                robotUuids.add(item.getString("uuid"))
            }
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                robotUuids
            )


        }
    }




    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cs = robotUuids.toTypedArray<CharSequence>()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchD_x = event.x.toInt()
                touchD_y = event.y.toInt()
                println("touch_downX = ${touchD_x}")
                println("touch_downY = ${touchD_y}")
            }
            MotionEvent.ACTION_UP -> {
                //println("eventX = ${event.x.toInt()}")
                //println(event.y.toInt())

                var touch_x2 = 0
                touchpose_x = event.x.toInt()
                touchpose_y = event.y.toInt()
                val touch_y = touchpose_y - 100
                println("touch_upX = ${touchpose_x}")
                println("touch_upY = ${touchpose_y}")
                val orient = Math.atan2((touchpose_y-touchD_y).toDouble(), (touchpose_x-touchD_x).toDouble())
                var deg = (orient * 180) / Math.PI
                println(orient)
                println(deg)
                val touch_x1 = touchpose_x - 280
                val touch_y1 = touch_y - 160
                println(touch_x1)
                if(touch_x1 < 100) {
                    touch_x2 = (touch_x1 / 23)
                }
                else{
                    touch_x2 = (touch_x1 / 21)
                }
                val touch_y2 = (touch_y1 / 16)
                val touch_x3 = touch_x2 - 6
                val touch_y3 = touch_y2 - 24


                //println("set X = ${touch_y3}")
                //println("set Y = ${touch_x3}")


                AlertDialog.Builder(this)
                    // FragmentではActivityを取得して生成
                    .setTitle("ロボットに対する操作（UUIDを選択）")
                    //.setMessage("ロボットに対する操作をお選びください")
                    .setSingleChoiceItems(cs, -1) { dialog, which ->
                        chosenRobot = cs[which].toString()
                        println(chosenRobot)
                    }
                    .setPositiveButton("初期位置を設定する", { dialog, which ->
                        // 初期位置設定
                        println(touchedRobotUuid)
                        msg_pose_x.put("data", touch_y3.toString())
                        msg_pose_y.put("data", touch_x3.toString())
                        rowma.publish(chosenRobot, "/initialpose_x", msg_pose_x)
                        rowma.publish(chosenRobot, "/initialpose_y", msg_pose_y)
                        println(msg_pose_x)
                        println(msg_pose_y)

                        draw_init_point = true
                        init_point_x = touchD_x
                        init_point_y = touchD_y


                    })
                    .setNegativeButton("ゴールを設定する", { dialog, which ->
                        // ゴール設定
                        msg_pose_x2.put("data", touch_y3.toString())
                        msg_pose_y2.put("data", touch_x3.toString())
                        rowma.publish(chosenRobot, "/amcl_pose_x", msg_pose_x2)
                        rowma.publish(chosenRobot, "/amcl_pose_y", msg_pose_y2)
                        println(msg_pose_x2)
                        println(msg_pose_y2)

                        draw_goal_point = true
                        goal_point_x = touchD_x
                        goal_point_y = touchD_y
                    })
                    .show()


                for ((k, v) in robotPositions) {
                    val position = extractPositionFromMsg(v)
                    val robotX = position[0]
                    val robotY = position[1]

                    if (abs((event.x.toInt()-280) - robotX) < 20 && abs((event.y.toInt() -160) - (robotY + 100)) < 20) {
                        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                            .setTitle("ロボット呼び出し")
                            .setMessage("このロボットに対する操作をお選びください。")
                            .setPositiveButton("呼び出す", { dialog, which ->
                                // TODO:Yesが押された時の挙動
                                println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                                flag = true;
                                startPublishingWifiInfo(k)
                            })
                            .setNegativeButton("呼び出しの中止", { dialog, which ->
                                // TODO:Noが押された時の挙動
                                flag = false
                            })
                            .show()


                    }


                }

                println("getSize = ${event.getSize()}")
            }
        }
        return super.onTouchEvent(event)



    }


}






