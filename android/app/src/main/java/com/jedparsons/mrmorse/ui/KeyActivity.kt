package com.jedparsons.mrmorse.ui

import android.os.Bundle
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_Q
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.jedparsons.mrmorse.AppContainer
import com.jedparsons.mrmorse.MrMorseApplication
import com.jedparsons.mrmorse.ui.KeyEvent.KEY_DOWN
import com.jedparsons.mrmorse.ui.KeyEvent.KEY_UP
import com.jedparsons.mrmorse.ui.theme.MrMorseTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.logcat
import android.view.KeyEvent as AndroidKeyEvent

class KeyActivity : ComponentActivity() {

  private val codeViewModel: CodeViewModel by viewModels()
  private val quizViewModel: QuizViewModel by viewModels()
  private val keyViewModel: KeyViewModel by viewModels()

  private var inKeyPress = false

  private lateinit var app: AppContainer
  private lateinit var quizFlowJob: Job
  private lateinit var codeFlowJob: Job
  private lateinit var letterFlowJob: Job
  private lateinit var textFlowJob: Job

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    app = (application as MrMorseApplication).appContainer

    setContent {
      MrMorseTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colors.background
        ) {
          KeyScreen(
            quizViewModel,
            codeViewModel,
            keyViewModel
          )
        }
      }
    }

    keyViewModel.position.observe(this) {
      when (it) {
        KEY_DOWN -> {
          app.demodulator.onPress()
          app.oscillator.pressKey()
        }
        KEY_UP -> {
          app.demodulator.onRelease()
          app.oscillator.releaseKey()
        }
        else -> {
        }
      }
    }

    quizFlowJob = app.quizGenerator.quizFlow.onEach { quiz ->
      quizViewModel.updateWord(quiz.text)
    }.launchIn(app.mainScope)

    codeFlowJob = app.demodulator.codeFlow.onEach { stroke ->
      codeViewModel.addStroke(stroke)
    }.launchIn(app.mainScope)

    letterFlowJob = app.demodulator.letterFlow.onEach { letter ->
      codeViewModel.addLetter(letter)
    }.launchIn(app.mainScope)

    textFlowJob = app.demodulator.textFlow.onEach { textSequence ->
      // Let the answer be visible for a moment.
      delay(1000)
      logcat { "Submitting answer: $textSequence" }
      app.quizGenerator.submitAnswer(textSequence)
      codeViewModel.resetText()
    }.launchIn(app.mainScope)
  }

  override fun onDestroy() {
    quizFlowJob.cancel()
    codeFlowJob.cancel()
    letterFlowJob.cancel()
    textFlowJob.cancel()
    super.onDestroy()
  }

  override fun onKeyDown(keyCode: Int, event: AndroidKeyEvent?): Boolean {
    if (keyCode != KEYCODE_Q) {
      return false
    }
    logcat { "Key Down: $keyCode, $event" }
    app.demodulator.onPress()
    app.oscillator.pressKey()
    inKeyPress = true
    return true
  }

  override fun onKeyUp(keyCode: Int, event: AndroidKeyEvent?): Boolean {
    if (!inKeyPress) {
      return false
    }
    logcat { "Key Up: $keyCode, $event" }
    app.demodulator.onRelease()
    app.oscillator.releaseKey()
    inKeyPress = false
    return true
  }

  companion object {
    const val TAG = "MorseKeyActivity"
  }
}
