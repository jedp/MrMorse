package com.jedparsons.mrmorse

import android.content.Context
import com.jedparsons.mrmorse.audio.KeyOscillator
import com.jedparsons.mrmorse.audio.RealKeyOscillator
import com.jedparsons.mrmorse.audio.RealSamplePlayer
import com.jedparsons.mrmorse.audio.SamplePlayer
import com.jedparsons.mrmorse.code.DecodingMachine
import com.jedparsons.mrmorse.code.KeystrokeDemodulator
import com.jedparsons.mrmorse.code.SymbolTree
import com.jedparsons.mrmorse.quiz.QuizGenerator
import com.jedparsons.mrmorse.quiz.RealQuizGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

interface AppContainer {
  val mainScope: CoroutineScope
  val audioDispatcher: CoroutineDispatcher
  val oscillator: SamplePlayer
  val demodulator: KeystrokeDemodulator
  val quizGenerator: QuizGenerator
}

class RealAppContainer(
  context: Context
) : AppContainer {

  override val mainScope = MainScope()
  override val audioDispatcher: CoroutineDispatcher = Dispatchers.Default
  override val oscillator = RealSamplePlayer()

  private val symbolTree = SymbolTree.buildSymbolTree()
  private val decodingMachine = DecodingMachine(mainScope, symbolTree)

  override val demodulator = KeystrokeDemodulator(mainScope, decodingMachine)
  override val quizGenerator = RealQuizGenerator(context, mainScope)
}
