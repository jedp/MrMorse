package com.jedparsons.mrmorse.code

import com.jedparsons.mrmorse.code.Stroke.DAH
import com.jedparsons.mrmorse.code.Stroke.DIT
import com.jedparsons.mrmorse.code.Stroke.NOTHING
import com.jedparsons.mrmorse.code.Stroke.PAUSE_LONG
import com.jedparsons.mrmorse.code.Stroke.PAUSE_SHORT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import logcat.logcat

class KeystrokeDemodulator(
  private val coroutineScope: CoroutineScope,
  private val decodingMachine: DecodingMachine
) {

  private var inWord = false
  private var lastPress = System.currentTimeMillis()
  private var ditRateMillis = 130L
  private val timer = CountdownTimer(Dispatchers.IO, ditRateMillis * 8, ::paused)

  val textFlow: Flow<String> = decodingMachine.textFlow
  val letterFlow: Flow<Char> = decodingMachine.letterFlow
  private val _codeFlow = MutableSharedFlow<Stroke>(replay = 0)
  val codeFlow: Flow<Stroke> = _codeFlow
    .shareIn(
      coroutineScope,
      replay = 0,
      started = SharingStarted.WhileSubscribed()
    )

  fun setRate(delay: Long) {
    ditRateMillis = delay
  }

  fun onPress() {
    timer.reset()
    val now = System.currentTimeMillis()
    if (inWord && now - lastPress > ditRateMillis * 2) {
      coroutineScope.launch {
        logcat { "Letter boundary" }
        _codeFlow.emit(PAUSE_SHORT)
        decodingMachine.receive(PAUSE_SHORT)
      }
    }
    lastPress = now
    inWord = true
  }

  fun onRelease() {
    val now = System.currentTimeMillis()
    val interval = now - lastPress
    coroutineScope.launch {
      if (interval > ditRateMillis) {
        logcat { "DAH" }
        _codeFlow.emit(DAH)
        decodingMachine.receive(DAH)
      } else {
        logcat { "DIT" }
        _codeFlow.emit(DIT)
        decodingMachine.receive(DIT)
      }
    }
    timer.start()
    lastPress = now
  }

  private fun paused() {
    inWord = false
    coroutineScope.launch {
      logcat { "Word boundary" }
      _codeFlow.emit(PAUSE_LONG)
      decodingMachine.receive(PAUSE_LONG)
    }
  }
}

class CountdownTimer(
  coroutineDispatcher: CoroutineDispatcher,
  private val delayMillis: Long,
  private val action: () -> Unit
) {

  private val job = SupervisorJob()
  private val scope = CoroutineScope(coroutineDispatcher + job)

  private var timer: Job? = null

  fun start() {
    timer = scope.launch {
      delay(delayMillis)
      action()
    }
  }

  fun reset() {
    timer?.cancel()
  }
}