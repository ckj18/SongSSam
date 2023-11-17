package com.example.songssam.Activitys

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.UniversalAudioInputStream
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.writer.WriterProcessor
import com.bumptech.glide.Glide
import com.example.songssam.API.SongSSamAPI.songssamAPI
import com.example.songssam.R
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


class RecordingActivity : AppCompatActivity() {
    //필요한 권한 선언
    private val requiredPermissions = arrayOf(
        RECORD_AUDIO, WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE
    )
    private val filename = intent.getStringExtra("songId") + ".wav"
    var dispatcher: AudioDispatcher? = null
    lateinit var tarsosDSPAudioFormat: TarsosDSPAudioFormat
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var file: File // Declare the file variable
    private var isRecording = false

    private val pitch: TextView by lazy {
        findViewById(R.id.pitch)
    }
    private val tv_playtime: TextView by lazy {
        findViewById(R.id.tv_playtime)
    }
    private val hearBTN: soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.hear_btn)
    }
    private val play_btn: soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.play_btn)
    }
    private val success_btn: soup.neumorphism.NeumorphButton by lazy {
        findViewById(R.id.success_btn)
    }
    private val cover: de.hdodenhof.circleimageview.CircleImageView by lazy {
        findViewById(R.id.cover)
    }
    private val artist: TextView by lazy {
        findViewById(R.id.artist)
    }
    private val title: TextView by lazy {
        findViewById(R.id.title)
    }
    private val seekBar: ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }
    private val container: FrameLayout by lazy {
        findViewById(R.id.perfect_score)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
        showData()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바(App Bar)로 지정

        val actionBar: ActionBar? = supportActionBar //앱바 제어를 위해 툴바 액세스
        actionBar!!.setDisplayHomeAsUpEnabled(true) // 앱바에 뒤로가기 버튼 만들기
        actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back) // 뒤로가기 버튼 색상 설정

        val sdCard = Environment.getExternalStorageDirectory()
        file = File(sdCard, filename)

        tarsosDSPAudioFormat = TarsosDSPAudioFormat(
            TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
            22050F,
            2 * 8,
            1,
            2 * 1,
            22050F,
            ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder())
        )

        initRecordingBTN()
        initHearBTN()
        initSuccessBTN()
    }

    private fun initHearBTN() {
        hearBTN.setOnClickListener {
            try {
                releaseDispatcher()
                val fileInputStream = FileInputStream(file)
                dispatcher = AudioDispatcher(
                    UniversalAudioInputStream(
                        fileInputStream,
                        tarsosDSPAudioFormat
                    ), 1024, 0
                )
                val playerProcessor: AudioProcessor =
                    AndroidAudioPlayer(tarsosDSPAudioFormat, 2048, 0)
                dispatcher!!.addAudioProcessor(playerProcessor)
                val pitchDetectionHandler =
                    PitchDetectionHandler { res, e ->
                        val pitchInHz = res.pitch
                        runOnUiThread { pitch.setText(pitchInHz.toString() + "") }
                    }
                val pitchProcessor: AudioProcessor = PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                    22050f,
                    1024,
                    pitchDetectionHandler
                )
                dispatcher!!.addAudioProcessor(pitchProcessor)
                val audioThread = Thread(dispatcher, "Audio Thread")
                audioThread.start()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun releaseDispatcher() {
        if (dispatcher != null) {
            if (!dispatcher!!.isStopped)
                dispatcher!!.stop()
            dispatcher = null
        }
    }

    private fun showData() {
        artist.text = intent.getStringExtra("artist")
        title.text = intent.getStringExtra("title")
        Glide.with(cover).load(intent.getStringExtra("cover")).into(cover)
    }

    private fun initRecordingBTN() {
        play_btn.setOnClickListener {
            if (isRecording) {
                //녹음되고 있을 떄
                play_btn.setImageResource(R.drawable.record_round)
                stopAudio()
                stopRecording()
                isRecording = false
            } else {
                //녹음하려할 떄
                getRecordingAuth()
                play_btn.setImageResource(R.drawable.stop)
                playAudio()
                recordAudio()
                isRecording = true
            }
        }
    }

    private fun stopRecording() {
        releaseDispatcher()
    }

    private fun recordAudio() {
        releaseDispatcher()
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        try {
            val randomAccessFile = RandomAccessFile(file, "rw")
            val recordProcessor: AudioProcessor =
                WriterProcessor(tarsosDSPAudioFormat, randomAccessFile)
            dispatcher.addAudioProcessor(recordProcessor)
            val pitchDetectionHandler =
                PitchDetectionHandler { res, e ->
                    val pitchInHz = res.pitch
                    runOnUiThread { pitch.setText(pitchInHz.toString() + "") }
                }
            val pitchProcessor: AudioProcessor = PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                22050f,
                1024,
                pitchDetectionHandler
            )
            dispatcher.addAudioProcessor(pitchProcessor)
            val audioThread = Thread(dispatcher, "Audio Thread")
            audioThread.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun playAudio() {
        Log.d("record", "playAudio 실행")
        playInstUrl(intent.getStringExtra("instUrl"))
        seekBar.visibility = View.VISIBLE   // 진행바 보이게
        // 음악 진행 상황을 스레드에서 표현
        object : Thread() {
            // 재생시간 표현 위한 기본 포맷
            var timeFormat = SimpleDateFormat("mm:ss")
            override fun run() {
                if (mediaPlayer == null) return     // 음악 재생 중 아니라면 그냥 리턴
                seekBar.max = mediaPlayer!!.duration  // 음악 길이만큼 최대 길이 지정
                // 재생되고 있는 동안
                while (mediaPlayer!!.isPlaying) {
                    // 회면 UI 바꾸기(기존 Thread에서는 화면 UI를 변경할 수 없음)
                    runOnUiThread {
                        seekBar.progress = mediaPlayer!!.currentPosition  // seekBar에 현재 진행 상활 표현
                        tv_playtime.text =
                            "진행 시간 : " + timeFormat.format(mediaPlayer!!.currentPosition)
                    }
                    // 잠깐 멈추기
                    SystemClock.sleep(200)  // 0.2초
                } // end oh while
            }   // end of run()
        }.start()   // end of Thread()
    }

    private fun playInstUrl(instUrl: String?) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    val url = "https://songssam.site:8443/song/download_inst?url=" + instUrl
                    setDataSource(url)
                    setOnPreparedListener {
                        it.start()
                    }
                    setOnErrorListener { _, _, _ ->
                        false
                    }
                    prepareAsync()
                }
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error playing audio: ${e.message}")
        }
    }

    private fun getOutputMediaFile(): String {
        val mediaDir = File(Environment.getExternalStorageDirectory(), "MyRecordings")
        if (!mediaDir.exists()) {
            mediaDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile = File(mediaDir, "REC_$timeStamp.mp4")
        Log.d("record", "녹음 저장")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://songssam.site:8443")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(
                        30,
                        TimeUnit.SECONDS
                    )
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        val apiService = retrofit.create(songssamAPI::class.java)
        val accessToken = "Bearer " + GlobalApplication.prefs.getString("accessToken", "")
        val call = apiService.uploadSong(accessToken, mediaFile, songId = 36518341)
        call.enqueue(object : Callback<Void> { // Use Callback<Void> as the callback type
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure here
                Log.d("updateFavoriteSong", t.stackTraceToString())
                Toast.makeText(
                    this@RecordingActivity,
                    "네트워크 오류와 같은 이유로 오류 발생!",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@RecordingActivity, MainActivity::class.java)
                    GlobalApplication.prefs.setString("chooseSong", "done")
                    startActivity(intent)
                } else {
                    // Handle non-successful response here
                    Toast.makeText(this@RecordingActivity, "서버가 닫혀있습니다!", Toast.LENGTH_LONG)
                        .show()
                    Log.d("updateFavoriteSong", "연결 실패")
                }
            }
        })
        Log.d("record", "녹음 전송 완료")
        return mediaFile.absolutePath
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun initSuccessBTN() {
        success_btn.setOnClickListener {
            //TODO("녹음 완료 후 클릭 시 다음 엑티비티 전환")
        }
    }

    private fun getRecordingAuth() {
        requestPermissions(this, requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if both permissions are granted
        val audioPermissionGranted =
            grantResults.contains(PackageManager.PERMISSION_GRANTED)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && audioPermissionGranted) {
            // Both permissions are granted, proceed with recording and file access.
            Log.d("record", "Both permissions granted")
            val filename = intent.getStringExtra("songId") + ".wav"
            val sdcard = Environment.getExternalStorageDirectory()
            file = File(sdcard, filename)

            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
            isRecording = true
        } else {
            // Permissions not granted, show a permission context popup.
            Log.d("record", "Permission not granted")
            showPermissionContextPopup()
        }
    }

    fun stopAudio() {
        Log.d("record", "stopAudio 실행")
        stopMediaPlayer()
    }

    private fun showPermissionContextPopup() {
        android.app.AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("목소리 샘플 획득을 위한 녹음 권한 및 외부 저장소 수정 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    fun ProcessPitch(pitchInHz: Float): String {
        val noteNumber = (12 * (Math.log(pitchInHz / 440.0) / Math.log(2.0)) + 69).toInt()
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val octave = noteNumber / 12 + 1
        val noteName = noteNames[noteNumber % 12]

        Thread {
            runOnUiThread {
                if (octave >= 2) {
                    startAnimation(noteNumber - 12)
                    Log.d("record", noteNumber.toString())
                }
            }
        }.start()
        return "$noteName$octave"
    }

    private fun startAnimation(pitchInHz: Int) {
        // 하얀 사각형 뷰 생성
        val whiteSquare = View(this)
        whiteSquare.setBackgroundColor(resources.getColor(R.color.white)) // 색상 설정

        // 뷰를 컨테이너에 추가
        val containerWidth = container.width // 컨테이너의 너비
        val containerHeight = container.height // 컨테이너의 높이
        val squareWidth = 10 // 사각형의 너비
        val squareHeight = 10 // 사각형의 높이
        val params = FrameLayout.LayoutParams(squareWidth, squareHeight)
        var note = if (pitchInHz > 72) 72
        else pitchInHz
        params.leftMargin = (containerWidth - squareWidth) / 2 // 컨테이너 가운데에서 시작
        params.topMargin = (containerHeight - squareHeight) * (72 - note) / 72 // C2부터 B7까지
        container.addView(whiteSquare, params)

        // TranslateAnimation 생성 및 설정
        val animation = TranslateAnimation(0f, -container.width.toFloat(), 0f, 0f)
        animation.duration = 3000 // 애니메이션 지속 시간 (밀리초)
        animation.fillAfter = true // 애니메이션 종료 후 위치 고정
        animation.interpolator = LinearInterpolator()

        // 애니메이션 리스너 추가 (애니메이션이 끝나면 뷰 제거)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션 시작 시 필요한 작업
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 애니메이션 종료 시 필요한 작업
                container.removeView(whiteSquare) // 뷰 제거
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션 반복 시 필요한 작업
            }
        })

        // 애니메이션 시작
        whiteSquare.startAnimation(animation)
    }

    companion object {
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


}