package edu.rvc.student.audioapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioFilePath: String? = null
    private var isRecording = false

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioSetup()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    btnRecord.isEnabled = false
                    Toast.makeText(this, "Record permission required",
                        Toast.LENGTH_LONG).show()
                }else{
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE)
                }
                return
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    btnRecord.isEnabled = false
                    Toast.makeText(this, "External Storage permission required",
                        Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
    private fun audioSetup() {
        if (!hasMicrophone()) {
            btnStop.isEnabled = false
            btnPlay.isEnabled = false
            btnRecord.isEnabled = false
        }else {
            btnPlay.isEnabled = false
            btnStop.isEnabled = false
        }

        audioFilePath = Environment.getExternalStorageDirectory().absolutePath + "/myaudio.3gp"

        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE)
    }

    private fun hasMicrophone(): Boolean {
        val pmanager = this.packageManager
        return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    fun recordAudio(view: View) {
        isRecording = true
        btnStop.isEnabled = true
        btnPlay.isEnabled = false
        btnRecord.isEnabled = false

        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setOutputFile(audioFilePath)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder?.start()
    }

    fun stopAudio(view: View) {

        btnStop.isEnabled = false
        btnPlay.isEnabled = true

        if (isRecording) {
            btnRecord.isEnabled = false
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }else {
            mediaPlayer?.release()
            mediaPlayer = null
            btnRecord.isEnabled = true
        }
    }

    fun playAudio(view: View) {
        btnPlay.isEnabled = false
        btnRecord.isEnabled = false
        btnStop.isEnabled = true

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioFilePath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        val permission = ContextCompat.checkSelfPermission(this, permissionType)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
        }
    }
}
