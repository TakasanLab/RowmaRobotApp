package com.rowma.rowmarobotapp

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.rowma.rowma_kotlin.Rowma
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Math.abs
import java.util.*
import android.os.Bundle as Bundle1


class RobotMapActivity : AppCompatActivity() {
    //var poseData: Any = TODO();
    val rowma = Rowma(
        "https://rowma.moriokalab.com" +
                "",
        "app1"
    )

    private val robotPositions: MutableMap<String, JSONObject> = mutableMapOf()
    private var mapView: LinearLayout? = null

    private val wifiManager: WifiManager get() = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val PERMISSIONS_REQUEST_CODE = 1

    private var robots: JSONArray = JSONArray() // 追加
    val robotUuids: ArrayList<String> = ArrayList() // 追加

    private var rowma_robot: JSONObject = JSONObject()

    private var circle_x = 0
    private var circle_y = 0
    /*
    val data_circle_x: ArrayList<Int> = ArrayList();
    val data_circle_y: ArrayList<Int> = ArrayList();
    */
    private var json_x = 0
    private var json_y = 0
    private var json_x2 = 0
    private var json_y2 = 0
    private var count = 0;
    private var flag: Boolean = false;

    private var touchedRobotUuid: String = ""

    val wifiScanReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onReceive(context: Context, intent: Intent) {
            //Timer().schedule(0, 10000) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success == true) {
                scanSuccess()
                //println("scan!")
            } else {
                scanFailure()
            }
        }
    }

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

    private fun startWifiScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                    ),
                    PERMISSIONS_REQUEST_CODE
                );
            }
        }
        //  val a = wifiManager.wifiState;
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this.registerReceiver(wifiScanReceiver, intentFilter)


        val success = wifiManager.startScan()
        if (!success) {
            println("failed!")
            scanFailure()
        }
    }

    private fun startTestSubscribing() {
        GlobalScope.launch {
            repeat(1000) {
                val pose_x =20
                val pose_y = -4
                val pose_x_test2 = -20
                val pose_y_test2 = 4
                // robotPositions["test1"] = JSONObject("{ \"header\": { \"seq\": 97, \"stamp\": { \"secs\": 1614059967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": 48.0509179473, \"y\": 59.3796744676, \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                // robotPositions["test2"] = JSONObject("{ \"header\": { \"seq\": 98, \"stamp\": { \"secs\": 1614069967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": 28.0509179473, \"y\": 39.3796744676, \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                robotPositions["abc"] =
                    JSONObject("{ \"header\": { \"seq\": 97, \"stamp\": { \"secs\": 1614059967, \"nsecs\": 101351289 }, \"frame_id\": \"map\" }, \"pose\": { \"pose\": { \"position\": { \"x\": " + pose_x + ", \"y\": " + pose_y + ", \"z\": 0 }, \"orientation\": { \"x\": 0, \"y\": 0, \"z\": -0.111618530314, \"w\": 0.993751127642 } }, \"covariance\": [ 0.01962388007580529, 0.0041231821655145495, 0, 0, 0, 0, 0.004123182165510997, 0.026842641323042926, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.01822387755608491 ] } }")
                //robotPositions["def"] =
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


    private fun scanSuccess() {
        wifiManager.startScan();
        val results: MutableList<ScanResult>? = wifiManager.scanResults
        val wifi_info: ArrayList<String> = ArrayList();
        val wifi_ssid: ArrayList<String> = ArrayList();
        val wifi_rssi: ArrayList<String> = ArrayList();
        //電波強度で降順にソート
        if (results != null) {
            //println(results)
            for (i in results.indices) {
                for (j in i + 1 until results.size) {
                    if (results.get(i).BSSID == results.get(j).BSSID && results.get(i).level >= results.get(
                            j
                        ).level
                    ) {
                        results.get(j).level = -999999999 //同じAPから発する電波は除外
                    }
                    if (results.get(i).level < results.get(j).level) {
                        val tmp1: String = results.get(i).SSID
                        results.get(i).SSID = results.get(j).SSID
                        results.get(j).SSID = tmp1
                        val tmp2: Int = results.get(i).frequency
                        results.get(i).frequency = results.get(j).frequency
                        results.get(j).frequency = tmp2
                        val tmp3: Int = results.get(i).level
                        results.get(i).level = results.get(j).level
                        results.get(j).level = tmp3
                        val tmp4: String = results.get(i).BSSID
                        results.get(i).BSSID = results.get(j).BSSID
                        results.get(j).BSSID = tmp4
                    }
                }
            }
        }


        //電波強度が大きいAPを５つwifi_infoに格納
        if (results != null) {
            for (i in 0 until 5) {
                wifi_info.add(
                    "SSID: " + results.get(i).SSID.toString() + "  BSSID: " + results.get(i).BSSID.toString() +
                            "  Level: " + results.get(i).level.toString() + "(dBm)"
                )
                wifi_ssid.add(results.get(i).BSSID.toString())
                wifi_rssi.add(results.get(i).level.toString())
                ;
            }
        }

        val msg_ssid = JSONObject()
        val msg_rssi = JSONObject()
        msg_ssid.put("data", wifi_ssid)
        msg_rssi.put("data", wifi_rssi)

        count += 1
        if ((count % 80) == 0 && flag == true) {
            //Wifi情報をPublish
            rowma.publish(touchedRobotUuid, "/chatter", msg_ssid)
            rowma.publish(touchedRobotUuid, "/chatter2", msg_rssi)
        }
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val a = wifiManager
        val b = a.connectionInfo
        val f = b.ssid
        val c = a.wifiState
        val d = a.scanResults
        val results = wifiManager.scanResults
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
                paint.color = Color.RED
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

            invalidate()
            //println(i)
            //paint.color = Color.TRANSPARENT
        }

    }




    private fun startPublishingWifiInfo(uuid: String) {
        touchedRobotUuid = uuid
        startWifiScan()
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






