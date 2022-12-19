package com.rowma.rowmarobotapp

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.rowma.rowma_kotlin.Rowma
import org.json.JSONObject
import java.sql.DriverManager.println
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import us.zoom.sdk.*



//const val YOUTUBE_VIDEO_ID = "lcrXNrKxVak"

//class TBCameraActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
class TBCameraActivity : AppCompatActivity() {
    val rowma = Rowma(
        "https://rowma.moriokalab.com" +
                "",
        "app1"
    )

    private var robot: JSONObject = JSONObject()
    //private val TAG = "YoutubeActivity"

    private var vel_flag: Boolean = true
    private var pass_flag: Boolean = true
    private var  tokeN : String = ""


    //private var ActiveNodeList : Array<String> = arrayOf()
    private var cnt = 0
    private var hnd = Handler()

    private lateinit var joinButton: ToggleButton
    private lateinit var localVideoView: ZoomVideoSDKVideoView
    private lateinit var remoteVideoView: ZoomVideoSDKVideoView


    val run = object : Runnable {
        override fun run() {
            //TODO("Not yet implemented")
            cnt ++
            println(cnt.toString())
            hnd.postDelayed(this, 1000)  // 1000ｍｓ後に自分にpost
            if(cnt == 300){
                //rowma.runLaunch("turtlebot", "turtlebot_navigation auto_docking.launch")
            }
        }

    }


    private val TAG = "MyDebug#TBActivity"


    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t_b_camera)
        rowma.connect()
        //println("aaaaaaaaaaa")
        rowma.subscribe("/dock_flag", { data -> dock_flag_subscribe(data) })
        rowma.subscribe("/token", { data -> token_subscribe(data) })

        val robotString = intent.getStringExtra("ROBOT")
        robot = JSONObject(robotString)
        //var moverobot = robot.getString("uuid")
        val robotUuidTextView: TextView = findViewById(R.id.cameraUuid)
        robotUuidTextView.append("  " + robot.getString("uuid" ))

        val vel = JSONObject()
        //val editText: EditText = findViewById(R.id.edit_text)
        //val button_pass: Button = findViewById(R.id.button_pass)
        joinButton = findViewById(R.id.joinButton)
        localVideoView = findViewById(R.id.localVideoView)
        remoteVideoView = findViewById(R.id.robotVideoView)

        if (!hasPermissions(this)) {
            requestPermissions(this)
        } else {
            initZoomSDK()
        }
        //rowma.publish("turtlebot", "/token_flag", "true")

        joinButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                if (ZoomVideoSDK.getInstance().isInSession) {
                    leave()
                } else {
                    //val userNameEditText = findViewById<EditText>(R.id.userNameEditText)
                    join("app_user", tokeN)
                    //onUser
                }
            } else {
                leave()
                // The toggle is disabled
            }
        }

        val robotbutton: Button = findViewById(R.id.robot_buttonA)
            robotbutton.setOnClickListener {
                var vel_x = 0.1
                vel.put("data", vel_x.toString())
                if (vel_flag == true && pass_flag == true) {
                    rowma.publish(robot.getString("uuid"), "/vel", vel)
                    Log.d(TAG, "vel_flag is true")
                }
            }

        val robotbutton2: Button = findViewById(R.id.robot_buttonB)
            robotbutton2.setOnClickListener {
                var vel_x = 0.5
                vel.put("data", vel_x.toString())
                if (vel_flag == true && pass_flag == true) {
                    rowma.publish(robot.getString("uuid"), "/ang", vel)
                }
            }

        val robotbutton3: Button = findViewById(R.id.robot_buttonD)
            robotbutton3.setOnClickListener {
                var vel_x = -0.5
                vel.put("data", vel_x.toString())
                if (vel_flag == true && pass_flag == true) {
                    rowma.publish(robot.getString("uuid"), "/ang", vel)
                }
            }

        val robotbutton4: Button = findViewById(R.id.robot_buttonE)
            robotbutton4.setOnClickListener {
                var vel_x = -0.1
                vel.put("data", vel_x.toString())
                if (vel_flag == true && pass_flag == true) {
                    rowma.publish(robot.getString("uuid"), "/vel", vel)
                }
            }

        val robotbutton5: Button = findViewById(R.id.robot_buttonC)
            robotbutton5.setOnClickListener {
                var vel_x = 0.0
                vel.put("data", vel_x.toString())
                if (vel_flag == true && pass_flag == true) {
                    rowma.publish(robot.getString("uuid"), "/vel", vel)
                    rowma.publish(robot.getString("uuid"), "/ang", vel)
                }
            }


            val tb2_ad: Button = findViewById(R.id.tb2_ad)
            tb2_ad.setOnClickListener {
                //rowma.runLaunch("turtlebot", "turtlebot_navigation auto_docking.launch")
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("TurtleBot2のオートドッキング")
                    .setMessage(
                        "TurtleBot2を充電スタンドにドッキングしますか？"
                    )
                    .setPositiveButton("はい", { dialog, which ->
                        // TODO:Yesが押された時の挙動

                        if(robot.getString("uuid") == "turtlebot") {
                            rowma.runLaunch("turtlebot", "turtlebot_navigation auto_docking.launch")
                            vel_flag = false
                        }
                        else{
                            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                                .setTitle("UUIDが一致しません ")
                                .setMessage("自動ドッキングを行う際は”turtlebot”を選択してください。")
                                .setPositiveButton("OK", { dialog, which ->
                                    // TODO:Yesが押された時の挙動
                                })
                                .show()
                        }
                    })
                    .setNegativeButton("いいえ", { dialog, which ->
                        // TODO:Noが押された時の挙動
                    })
                    .show()

            }



    }



    private fun hasPermissions(context: Context) =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions(activity: TBCameraActivity) {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var grantedCount = 0
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    grantedCount++
                }
            }
            if (grantResults.size == grantedCount) {
                initZoomSDK()
            } else {
                Toast.makeText(applicationContext, "アクセスを許可してください", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private fun join(name: String, Token: String) {
        val audioOptions = ZoomVideoSDKAudioOption().apply {
            connect = true // Auto connect to audio upon joining
            mute = false // Auto mute audio upon joining
        }
        val videoOptions = ZoomVideoSDKVideoOption().apply {
            //localVideoOn = true // Turn on local/self video upon joining
        }
        val params = ZoomVideoSDKSessionContext().apply {
            sessionName = "turtlebot"
            userName = "app_user"
            sessionPassword = "1104"
            token = Token
            audioOption = audioOptions
            videoOption = videoOptions
        }

        val session = ZoomVideoSDK.getInstance().joinSession(params)
        if (session != null) {
            Log.d(TAG, "joinSession")
            Log.d(TAG, "  sessionName: $session.sessionName")
            Log.d(TAG, "  sessionID: $session.sessionID")

            val videoHelper = ZoomVideoSDK.getInstance().videoHelper
            videoHelper.startVideo()
            //内カメ外カメ
            videoHelper.switchCamera()
        }
    }

    private fun leave() {
        val shouldEndSession = true
        ZoomVideoSDK.getInstance().leaveSession(shouldEndSession)
    }

    private fun initZoomSDK() {
        val sdk = ZoomVideoSDK.getInstance()
        val params = ZoomVideoSDKInitParams().apply {
            domain = "https://zoom.us" // Required
            logFilePrefix = "MyLogPrefix" // Optional for debugging
            enableLog = true // Optional for debugging
        }
        val initResult = sdk.initialize(this, params)
        if (initResult == ZoomVideoSDKErrors.Errors_Success){
            Log.d(TAG, "initialize: successfully")
        } else {
            // Something went wrong, see error code documentation
            Log.d(TAG, "initialize: failed")
        }

        val listener = object : ZoomVideoSDKDelegate {
            override fun onSessionJoin() {
                Log.d(TAG, "onSessionJoin")

                val session = ZoomVideoSDK.getInstance().session
                session.mySelf.videoCanvas.subscribe(
                    localVideoView,
                    ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
                )

                //joinButton.setText("Leave")
            }

            override fun onSessionLeave() {
                Log.d(TAG, "onSessionLeave")

                val session = ZoomVideoSDK.getInstance().session
                session.mySelf.videoCanvas.unSubscribe(localVideoView) // todo: unSubscribeしてもViewがクリアされない

                leave()
               // joinButton.setText("Join")
            }

            override fun onError(errorCode: Int) {
                when (errorCode) {
                    ZoomVideoSDKErrors.Errors_Session_Join_Failed -> { // 2003
                        // トークンの期限切れかも
                        Log.d(TAG, "onError: Errors_Session_Join_Failed($errorCode)")
                    }

                    else -> {
                        Log.d(TAG, "onError: $errorCode")
                    }
                }
            }

            @SuppressLint("WrongViewCast")
            override fun onUserJoin(
                userHelper: ZoomVideoSDKUserHelper?,
                userList: MutableList<ZoomVideoSDKUser>?
            ) {
                Log.d(TAG, "onUserJoin")
                userList?.forEach { user ->
                    Log.d(TAG, "  userName: ${user.userName}")
                    Log.d(TAG, "  userID: ${user.userID}")
                    println(user)
                    user.videoCanvas.subscribe(
                        remoteVideoView,
                        ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
                    )

                }
            }

            override fun onUserLeave(
                userHelper: ZoomVideoSDKUserHelper?,
                userList: MutableList<ZoomVideoSDKUser>?
            ) {
                Log.d(TAG, "onUserLeave")

                userList?.forEach { user ->
                    Log.d(TAG, "  id: " + user.userID)
                    Log.d(TAG, "  name: " + user.userName)

                    user.videoCanvas.unSubscribe(remoteVideoView) // todo: unSubscribeしてもViewがクリアされない
                }
            }

            override fun onUserVideoStatusChanged(
                videoHelper: ZoomVideoSDKVideoHelper?,
                userList: MutableList<ZoomVideoSDKUser>?
            ) {
                Log.d(TAG, "onUserVideoStatusChanged")
            }

            override fun onUserAudioStatusChanged(
                audioHelper: ZoomVideoSDKAudioHelper?,
                userList: MutableList<ZoomVideoSDKUser>?
            ) {
                Log.d(TAG, "onUserAudioStatusChanged")
            }

            override fun onUserShareStatusChanged(
                shareHelper: ZoomVideoSDKShareHelper?,
                userInfo: ZoomVideoSDKUser?,
                status: ZoomVideoSDKShareStatus?
            ) {
                Log.d(TAG, "onUserShareStatusChanged")
            }

            override fun onLiveStreamStatusChanged(
                liveStreamHelper: ZoomVideoSDKLiveStreamHelper?,
                status: ZoomVideoSDKLiveStreamStatus?
            ) {
                Log.d(TAG, "onLiveStreamStatusChanged")
            }

            override fun onChatNewMessageNotify(
                chatHelper: ZoomVideoSDKChatHelper?,
                messageItem: ZoomVideoSDKChatMessage?
            ) {
                Log.d(TAG, "onChatNewMessageNotify")
            }

            override fun onUserHostChanged(
                userHelper: ZoomVideoSDKUserHelper?,
                userInfo: ZoomVideoSDKUser?
            ) {
                Log.d(TAG, "onUserHostChanged")
            }

            override fun onUserManagerChanged(user: ZoomVideoSDKUser?) {
                Log.d(TAG, "onUserManagerChanged")
            }

            override fun onUserNameChanged(user: ZoomVideoSDKUser?) {
                Log.d(TAG, "onUserNameChanged")
            }

            override fun onUserActiveAudioChanged(
                audioHelper: ZoomVideoSDKAudioHelper?,
                list: MutableList<ZoomVideoSDKUser>?
            ) {
                Log.d(TAG, "onUserActiveAudioChanged")
            }

            override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) {
                Log.d(TAG, "onSessionNeedPassword")
            }

            override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {
                Log.d(TAG, "onSessionPasswordWrong")
            }

            override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
                Log.d(TAG, "onMixedAudioRawDataReceived")
            }

            override fun onOneWayAudioRawDataReceived(
                rawData: ZoomVideoSDKAudioRawData?,
                user: ZoomVideoSDKUser?
            ) {
                Log.d(TAG, "onOneWayAudioRawDataReceived")
            }

            override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
                Log.d(TAG, "onShareAudioRawDataReceived")
            }

            override fun onCommandReceived(sender: ZoomVideoSDKUser?, strCmd: String?) {
                Log.d(TAG, "onCommandReceived")
            }

            override fun onCommandChannelConnectResult(isSuccess: Boolean) {
                Log.d(TAG, "onCommandChannelConnectResult")
            }

            override fun onCloudRecordingStatus(status: ZoomVideoSDKRecordingStatus?) {
                Log.d(TAG, "onCloudRecordingStatus")
            }

            override fun onHostAskUnmute() {
                Log.d(TAG, "onHostAskUnmute")
            }

            override fun onInviteByPhoneStatus(
                status: ZoomVideoSDKPhoneStatus?,
                reason: ZoomVideoSDKPhoneFailedReason?
            ) {
                Log.d(TAG, "onInviteByPhoneStatus")
            }

            override fun onMultiCameraStreamStatusChanged(
                status: ZoomVideoSDKMultiCameraStreamStatus?,
                user: ZoomVideoSDKUser?,
                videoPipe: ZoomVideoSDKRawDataPipe?
            ) {
                Log.d(TAG, "onMultiCameraStreamStatusChanged")
            }

            override fun onMultiCameraStreamStatusChanged(
                status: ZoomVideoSDKMultiCameraStreamStatus?,
                user: ZoomVideoSDKUser?,
                canvas: ZoomVideoSDKVideoCanvas?
            ) {
                Log.d(TAG, "onMultiCameraStreamStatusChanged")
            }
        }

        ZoomVideoSDK.getInstance().addListener(listener)
    }


    private fun token_subscribe(data: Any) {
        val JObject: JSONObject = JSONObject(data.toString())
        val tokendata: JSONObject = JSONObject(JObject.get("msg").toString())
        tokeN = tokendata.get("data").toString()
        println(tokendata)


        //return tokeN
    }

    private fun dock_flag_subscribe(data: Any){
        println("ddddddddddddddddd")
        val JObject: JSONObject = JSONObject(data.toString())
        val dock_data = JObject.get("msg").toString()
        if(dock_data == "{\"data\":\"2\"}") {
            println("aaaa")
            vel_flag = true
            hnd.removeCallbacks(run) // キューキャンセル
            cnt = 0
        }
        if(dock_data == "{\"data\":\"0\"}") {
            println("bbbb")
            hnd.post(run)
        }
        println(dock_data)

    }




        /*
        override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?,
            wasRestored: Boolean
        ) {
            Log.d(TAG, "onInitializationSuccess: provider is ${provider?.javaClass}")
            Log.d(TAG, "onInitializationSuccess: youTubePlayer is ${youTubePlayer?.javaClass}")
            Toast.makeText(this, "Initialized Youtube Player successfully", Toast.LENGTH_SHORT).show()

            if (!wasRestored) {
                youTubePlayer?.cueVideo(com.example.rowmarobotapp.YOUTUBE_VIDEO_ID)
            }
        }

        override fun onInitializationFailure(
            provider: YouTubePlayer.Provider?,
            youTubeInitializationResult: YouTubeInitializationResult?
        ) {
            val REQUEST_CODE = 0

            if (youTubeInitializationResult?.isUserRecoverableError == true) {
                youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show()
            } else {
                val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

         */

}


