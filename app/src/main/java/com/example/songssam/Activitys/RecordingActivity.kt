package com.example.songssam.Activitys

import android.Manifest.permission.*
import android.annotation.SuppressLint
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
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.UniversalAudioInputStream
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.writer.WriterProcessor
import com.example.songssam.R
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteOrder
import java.text.SimpleDateFormat


class RecordingActivity : AppCompatActivity() {

    //필요한 권한 선언
    private val requiredPermissions = arrayOf(
        RECORD_AUDIO, WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE
    )

    lateinit var dispatcher: AudioDispatcher
    lateinit var tarsosDSPAudioFormat: TarsosDSPAudioFormat
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File // Declare the file variable

    var isRecording = false

    var dictionary = HashMap<Double, String>()

    private val pitch: TextView by lazy {
        findViewById(R.id.pitch)
    }
    private val tv_playtime: TextView by lazy {
        findViewById(R.id.tv_playtime)
    }
    private val reset_btn: soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.reset_btn)
    }
    private val play_btn: soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.play_btn)
    }
    private val pause_btn: soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.pause_btn)
    }
    private val success_btn: soup.neumorphism.NeumorphButton by lazy {
        findViewById(R.id.success_btn)
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

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바(App Bar)로 지정

        val actionBar: ActionBar? = supportActionBar //앱바 제어를 위해 툴바 액세스
        actionBar!!.setDisplayHomeAsUpEnabled(true) // 앱바에 뒤로가기 버튼 만들기
        actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back) // 뒤로가기 버튼 색상 설정

        mediaPlayer = MediaPlayer.create(this@RecordingActivity, R.raw.everything_black)
        title.text = "Everything Black"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        file = File(storageDir, "recorded_audio.wav")

        tarsosDSPAudioFormat = TarsosDSPAudioFormat(
            TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
            22050F,
            2 * 8,
            1,
            2 * 1,
            22050F,
            ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder())
        );
        initPlayBTN()
        initPauseBTN()
        initResetBTN()
        initSuccessBTN()
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

    private fun makeline() {
        // 검은 라인 뷰 생성
        val line = View(this)
        line.setBackgroundColor(resources.getColor(R.color.black)) // 색상 설정

        val containerWidth = container.width // 컨테이너의 너비

        // 뷰를 컨테이너에 추가
        val lineWidth = 1 // 사각형의 너비
        val lineHeight = container.height // 사각형의 높이
        val params = FrameLayout.LayoutParams(lineWidth, lineHeight)
        params.leftMargin = (containerWidth - lineWidth) / 2 // 컨테이너 가운데에서 시작

        container.addView(line, params)
    }

    private fun initResetBTN() {
        reset_btn.setOnClickListener {
            mediaPlayer.reset()
        }
    }


    private fun initSuccessBTN() {
        success_btn.setOnClickListener {
            //TODO("녹음 완료 후 클릭 시 다음 엑티비티 전환")
        }
    }

    private fun initPlayBTN() {
        play_btn.setOnClickListener {
            Log.d("tag", "playButton click")
            getRecordingAuth()
        }
    }

    private fun initPauseBTN() {
        pause_btn.setOnClickListener {
            Log.d("tag", "pauseButton click")
            stopAudio()
            stopRecording()
            isRecording = false
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
            playAudio()
            recordAudio()
            isRecording = true
        } else {
            // Permissions not granted, show a permission context popup.
            Log.d("record", "Permission not granted")
            showPermissionContextPopup()
        }
    }


    fun stopAudio() {
        Log.d("record", "stopAudio 실행")
        play_btn.isClickable = true
        pause_btn.isClickable = false
        mediaPlayer.stop()
        mediaPlayer = MediaPlayer.create(this@RecordingActivity, R.raw.everything_black)
    }

    fun playAudio() {
        Log.d("record", "playAudio 실행")
        play_btn.isClickable = false
        pause_btn.isClickable = true
        mediaPlayer.start()
        seekBar.visibility = View.VISIBLE   // 진행바 보이게
        // 음악 진행 상황을 스레드에서 표현
        object : Thread() {
            // 재생시간 표현 위한 기본 포맷
            var timeFormat = SimpleDateFormat("mm:ss")

            override fun run() {
                if (mediaPlayer == null) return     // 음악 재생 중 아니라면 그냥 리턴

                seekBar.max = mediaPlayer.duration  // 음악 길이만큼 최대 길이 지정
                // 재생되고 있는 동안
                while (mediaPlayer.isPlaying) {
                    // 회면 UI 바꾸기(기존 Thread에서는 화면 UI를 변경할 수 없음)
                    runOnUiThread {
                        seekBar.progress = mediaPlayer.currentPosition  // seekBar에 현재 진행 상활 표현
                        tv_playtime.text =
                            "진행 시간 : " + timeFormat.format(mediaPlayer.currentPosition)
                    }
                    // 잠깐 멈추기
                    SystemClock.sleep(200)  // 0.2초
                } // end oh while
            }   // end of run()
        }.start()   // end of Thread()
    }

    fun recordAudio() {
        Log.d("record", "recordAudio 실행")
        val start = System.currentTimeMillis()
        releaseDispatcher()
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        try {
            val randomAccessFile = RandomAccessFile(file, "rw")
            val recordProcessor = WriterProcessor(tarsosDSPAudioFormat, randomAccessFile)
            dispatcher.addAudioProcessor(recordProcessor)
            val pitchDetectionHandler = object : PitchDetectionHandler {
                override fun handlePitch(res: PitchDetectionResult, e: AudioEvent) {
                    val pitchInHz = res.pitch
                    var octav = ProcessPitch(pitchInHz) // pitch -> note
                    runOnUiThread {
                        pitch.setText(octav)
                        val end = System.currentTimeMillis() // note가 입력된 시간 가져오기(일반시각)
                        val time = (end - start) / 1000.0 // 녹음이 시작된 이후의 시간으로 변경
                        dictionary.put(time, octav) // hashmap에 <time, note> 입력
                    }
                }
            }
            val pitchProcessor =
                PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                    22050F,
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

    fun stopRecording() {
        releaseDispatcher()
    }

    fun releaseDispatcher() {
        if (::dispatcher.isInitialized) {
            if (!dispatcher.isStopped) dispatcher.stop()
        }
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

        makeline()
        // 빨간 사각형 뷰 생성
        val redSquare = View(this)
        redSquare.setBackgroundColor(resources.getColor(R.color.red)) // 색상 설정

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
        container.addView(redSquare, params)

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
                container.removeView(redSquare) // 뷰 제거
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션 반복 시 필요한 작업
            }
        })

        // 애니메이션 시작
        redSquare.startAnimation(animation)
    }

    companion object {
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


}