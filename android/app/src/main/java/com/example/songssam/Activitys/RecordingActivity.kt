package com.example.songssam.Activitys

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
    private val requiredPermissions = arrayOf(RECORD_AUDIO,WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE)

    lateinit var dispatcher: AudioDispatcher
    lateinit var tarsosDSPAudioFormat: TarsosDSPAudioFormat
    lateinit var mediaPlayer : MediaPlayer
    private lateinit var file: File // Declare the file variable

    var isRecording = false

    var dictionary = HashMap<Double, String>()

    private val pitch: TextView by lazy {
        findViewById(R.id.pitch)
    }
    private val tv_playtime: TextView by lazy {
        findViewById(R.id.tv_playtime)
    }
    private val reset_btn :soup.neumorphism.NeumorphFloatingActionButton by lazy{
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

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
        requestPermissions(this,requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
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
        pause_btn.isClickable=false
        mediaPlayer.stop()
        mediaPlayer = MediaPlayer.create(this@RecordingActivity, R.raw.everything_black)
    }

    fun playAudio() {
        Log.d("record", "playAudio 실행")
        play_btn.isClickable = false
        pause_btn.isClickable=true
        mediaPlayer.start()
        seekBar.visibility = View.VISIBLE   // 진행바 보이게
        // 음악 진행 상황을 스레드에서 표현
        object:Thread() {
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
                        tv_playtime.text = "진행 시간 : " + timeFormat.format(mediaPlayer.currentPosition)
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

        return "$noteName$octave"
    }

    companion object {
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


}