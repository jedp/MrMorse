package com.jedparsons.mrmorse.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jedparsons.mrmorse.R
import com.jedparsons.mrmorse.code.Stroke
import com.jedparsons.mrmorse.code.Stroke.DAH
import com.jedparsons.mrmorse.code.Stroke.DIT
import com.jedparsons.mrmorse.code.Stroke.NOTHING
import com.jedparsons.mrmorse.code.Stroke.PAUSE_LONG
import com.jedparsons.mrmorse.code.Stroke.PAUSE_SHORT
import com.jedparsons.mrmorse.ui.KeyEvent.KEY_DOWN
import com.jedparsons.mrmorse.ui.KeyEvent.KEY_UP
import com.jedparsons.mrmorse.ui.theme.Amber
import com.jedparsons.mrmorse.ui.theme.Purple200

@Composable
fun KeyScreen(
  quizViewModel: QuizViewModel,
  codeViewModel: CodeViewModel,
  keyViewModel: KeyViewModel
) {

  val quizWord: String by quizViewModel.quizWord.observeAsState(initial = "")
  val codeText: String by codeViewModel.codeSequence.observeAsState(initial = "")
  val decipheredText: String by codeViewModel.decipheredText.observeAsState(initial = "")

  BoxWithConstraints(
    contentAlignment = Alignment.Center
  ) {
    Column(
      Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      QuizWordField(text = quizWord)
      CodeWordField(codeText = codeText, decipheredText = decipheredText)
      TappableButton(
        text = stringResource(R.string.tap_me).toUpperCase(Locale.current),
        onPress = keyViewModel::onPress,
        onRelease = keyViewModel::onRelease
      )
    }
  }
}

class QuizViewModel : ViewModel() {

  private var _quizWord: MutableLiveData<String> = MutableLiveData("")

  val quizWord: LiveData<String> = _quizWord

  fun updateWord(word: String) {
    _quizWord.value = word
  }
}

class CodeViewModel : ViewModel() {

  private var _codeSequence: MutableLiveData<String> = MutableLiveData("")
  private var _letterSequence: MutableLiveData<String> = MutableLiveData("")

  val codeSequence: LiveData<String> = _codeSequence
  val decipheredText: LiveData<String> = _letterSequence

  fun resetText() {
    _codeSequence.value = ""
    _letterSequence.value = ""
  }

  fun addStroke(stroke: Stroke) {
    when (stroke) {
      NOTHING -> {}
      DIT -> _codeSequence.value += "\u00B7"
      DAH -> _codeSequence.value += "\u2013"
      PAUSE_SHORT -> _codeSequence.value += " "
      PAUSE_LONG -> {
      }
    }
  }

  fun addLetter(letter: Char) {
    _letterSequence.value += letter
  }
}

enum class KeyEvent {
  KEY_DOWN,
  KEY_UP,
  ;
}

class KeyViewModel : ViewModel() {

  private var _keyPosition: MutableLiveData<KeyEvent> = MutableLiveData(KEY_UP)

  val position: LiveData<KeyEvent> = _keyPosition

  fun onPress() {
    _keyPosition.value = KEY_DOWN
  }

  fun onRelease() {
    _keyPosition.value = KEY_UP
  }

}

@Composable
fun QuizWordField(
  text: String
) {
  Box(
    Modifier.testTag("tag-quiz-$text"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      modifier = Modifier.padding(8.dp),
      fontSize = 60.sp,
      style = MaterialTheme.typography.body1
    )
  }
}

@Composable
fun CodeWordField(
  codeText: String,
  decipheredText: String
) {
  Box(
    Modifier.testTag("tag-code-$codeText"),
    contentAlignment = Alignment.Center
  ) {
    Column {
      Text(
        text = decipheredText,
        modifier = Modifier.padding(8.dp),
        fontSize = 60.sp,
        color = Purple200,
        style = MaterialTheme.typography.body1
      )
      Text(
        text = codeText,
        modifier = Modifier.padding(8.dp),
        fontSize = 40.sp,
        style = MaterialTheme.typography.body1
      )
    }
  }
}

@Composable
fun TappableButton(
  text: String,
  onPress: () -> Unit,
  onRelease: () -> Unit
) {

  val interactionSource = remember { MutableInteractionSource() }

  val tappable = Modifier.clickable(
    interactionSource = interactionSource,
    indication = LocalIndication.current
  ) {
    // something
  }

  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect {
      when (it) {
        is PressInteraction.Press -> onPress()
        is PressInteraction.Release -> onRelease()
        else -> {
        }
      }
    }
  }

  Box(
    Modifier
      .testTag("tag-tap-$text")
      .then(tappable),
    contentAlignment = Alignment.Center
  ) {
    Card(
      border = BorderStroke(1.dp, color = Amber),
      shape = RoundedCornerShape(10),
    ) {
      Text(
        text = text,
        modifier = Modifier.padding(8.dp),
        fontSize = 60.sp,
        style = MaterialTheme.typography.body1
      )
    }
  }
}