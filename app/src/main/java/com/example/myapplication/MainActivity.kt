package com.example.myapplication

import android.content.res.Resources
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var up: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        startSpeechToText()
    }

    private fun startSpeechToText() {
        val editText = findViewById<EditText>(R.id.editText)
        val first:TextView = findViewById(R.id.first)

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(v: Float) {}

            override fun onBufferReceived(bytes: ByteArray) {}

            override fun onEndOfSpeech() {}

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)//getting all the matches
                //displaying the first match
                if (matches != null) {
                    iPandPort


                    val res: Resources = getResources()
                    up = res.getStringArray( R.array.up )
                    if (matches[0].toLowerCase() in up){
                        editText.setText("up")
                        CMD = "up"
                    }else{
                        editText.setText(matches[0])
                    }



                    val cmd_increase_servo =
                        Socket_AsyncTask()
                    cmd_increase_servo.execute()

                }

            }

            val iPandPort: Unit
                get() {
                    val iPandPort = "192.168.43.193:21780"
                    Log.d("MYTEST", "IP String: $iPandPort")
                    val temp = iPandPort.split(":").toTypedArray()
                    wifiModuleIp = temp[0]
                    wifiModulePort = Integer.valueOf(temp[1])
                    Log.d("MY TEST", "IP:$wifiModuleIp")
                    Log.d("MY TEST", "PORT:$wifiModulePort")
                }
            inner class Socket_AsyncTask :
                AsyncTask<Void?, Void?, Void?>() {
                var socket: Socket? = null
                protected override fun doInBackground(vararg params: Void?): Void? {
                    try {
                        val inetAddress =
                            InetAddress.getByName(wifiModuleIp)
                        socket = Socket(inetAddress, wifiModulePort)
                        val dataOutputStream =
                            DataOutputStream(socket!!.getOutputStream())
                        dataOutputStream.writeUTF(CMD)
                        dataOutputStream.close()
                        socket!!.close()
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    return null
                }
            }

            override fun onPartialResults(bundle: Bundle) {}

            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        btSpeech.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    editText.hint = getString(R.string.text_hint)
                }

                MotionEvent.ACTION_DOWN -> {
                    speechRecognizer.startListening(speechRecognizerIntent)
                    editText.setText("")
                    editText.hint = "Listening..."
                    var str=editText.hint

                }
            }
            false
        })
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
                startActivity(intent)
                finish()
                Toast.makeText(this, "Enable Microphone Permission..!!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        var wifiModuleIp = ""
        var wifiModulePort = 0
        var CMD = "0"
        var test = false
    }
}