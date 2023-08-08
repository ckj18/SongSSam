package com.example.songssam.Activitys

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
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


class RecordingActivity : AppCompatActivity() {

    //필요한 권한 선언
    private val requiredPermissions = arrayOf(RECORD_AUDIO)

    lateinit var dispatcher: AudioDispatcher
    lateinit var tarsosDSPAudioFormat: TarsosDSPAudioFormat
    lateinit var file: File

    var isRecording = false
    var isPlaying = false
    val filename = "recorded_sound.wav"

    private val pitch : TextView by lazy {
        findViewById(R.id.pitch)
    }
    private val tv_playtime : TextView by lazy {
        findViewById(R.id.tv_playtime)
    }
    private val play_btn : soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.play_btn)
    }
    private val pause_btn : soup.neumorphism.NeumorphFloatingActionButton by lazy {
        findViewById(R.id.pause_btn)
    }
    private val success_btn : soup.neumorphism.NeumorphButton by lazy {
        findViewById(R.id.success_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        val sdCard = Environment.getExternalStorageDirectory()
        file = File(sdCard, filename)

        val tarsosDSPAudioFormat= TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
            22050F,
            2 * 8,
            1,
            2 * 1,
            22050F,
            ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));

        initPlayBTN()
        initPauseBTN()
        initSuccessBTN()
    }


    private fun initSuccessBTN() {
        success_btn.setOnClickListener{
            //TODO("녹음 완료 후 클릭 시 다음 엑티비티 전환")
        }
    }

    private fun initPlayBTN() {
        play_btn.setOnClickListener{
            getRecordingAuth()
        }
    }

    private fun initPauseBTN() {
        pause_btn.setOnClickListener {
            stopAudio()
            stopRecording()
            isRecording = false
        }
    }

    private fun getRecordingAuth() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //권한이 부여받은게 맞는지 check 권한부여받았으면 true 아니면 false
        val audioRequestPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        //권한이 부여되지않으면 어플 종료
        if(!audioRequestPermissionGranted){
            showPermissionContextPopup()
        }
        else{
            playAudio()
            isRecording = true
            recordAudio()
        }
    }

    fun stopAudio() {
        if (isPlaying) { // 이미 재생 중이라면 중지
            releaseDispatcher()
            pitch.text = "" // 현재 표시된 피치 값 초기화
            isPlaying = false
        }
    }

    fun playAudio() {
        try {
            releaseDispatcher()

            val fileInputStream = FileInputStream(file)
            dispatcher =
                AudioDispatcher(UniversalAudioInputStream(fileInputStream, tarsosDSPAudioFormat), 1024, 0)

            val playerProcessor = AndroidAudioPlayer(tarsosDSPAudioFormat, 2048, 0)
            dispatcher.addAudioProcessor(playerProcessor)

            val pitchDetectionHandler = object : PitchDetectionHandler {
                override fun handlePitch(res: PitchDetectionResult, e: AudioEvent) {
                    val pitchInHz = res.pitch
                    runOnUiThread {
                        pitch.setText(pitchInHz.toString())
                    }
                }
            }

            val pitchProcessor =
                PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050F, 1024, pitchDetectionHandler)
            dispatcher.addAudioProcessor(pitchProcessor)

            val audioThread = Thread(dispatcher, "Audio Thread")
            audioThread.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun recordAudio() {
        releaseDispatcher()
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        try {
            val randomAccessFile = RandomAccessFile(file, "rw")
            val recordProcessor = WriterProcessor(tarsosDSPAudioFormat, randomAccessFile)
            dispatcher.addAudioProcessor(recordProcessor)

            val pitchDetectionHandler = object : PitchDetectionHandler {
                override fun handlePitch(res: PitchDetectionResult, e: AudioEvent) {
                    val pitchInHz = res.pitch
                    runOnUiThread {
                        pitch.setText(pitchInHz.toString())
                    }
                }
            }

            val pitchProcessor =
                PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050F, 1024, pitchDetectionHandler)
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
            .setMessage("목소리 샘플 획득을 위한 녹음 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    companion object{
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION =201
    }


}