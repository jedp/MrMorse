package com.jedparsons.mrmorse.quiz

import android.content.Context
import com.jedparsons.mrmorse.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import logcat.logcat

data class Quiz(
  val level: Int,
  val text: String
)

interface QuizGenerator {
  val quizFlow: Flow<Quiz>
  fun setLevel(level: Int)
  suspend fun submitAnswer(answer: String)
}

class RealQuizGenerator(
  context: Context,
  coroutineScope: CoroutineScope
):QuizGenerator {

  private val lessons = context.resources.getStringArray(R.array.lessons)
  private var level = 4
  private val _quizFlow = MutableStateFlow(Quiz(level, ""))

  override val quizFlow: Flow<Quiz> = _quizFlow
    .shareIn(
      scope = coroutineScope,
      replay = 0,
      started = SharingStarted.WhileSubscribed()
    )

  override fun setLevel(level: Int) {
    this.level = level
  }

  override suspend fun submitAnswer(answer: String) {
    if (answer == _quizFlow.value.text) {
      logcat { "YAY" }
    }
    nextQuestion()
  }

  private suspend fun nextQuestion() {
    val text = lessons[level].splitToSequence(", ").shuffled().take(1).first()
    logcat { "Next text: $text" }
    _quizFlow.emit(Quiz(level, text))
  }
}