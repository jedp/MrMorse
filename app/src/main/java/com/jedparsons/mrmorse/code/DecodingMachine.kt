package com.jedparsons.mrmorse.code

import com.jedparsons.mrmorse.Alphabet
import com.jedparsons.mrmorse.Alphabet.NO_LETTER
import com.jedparsons.mrmorse.code.Stroke.DAH
import com.jedparsons.mrmorse.code.Stroke.DIT
import com.jedparsons.mrmorse.code.Stroke.NOTHING
import com.jedparsons.mrmorse.code.Stroke.PAUSE_LONG
import com.jedparsons.mrmorse.code.Stroke.PAUSE_SHORT
import com.jedparsons.mrmorse.code.SymbolTree.Error
import com.jedparsons.mrmorse.code.SymbolTree.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

data class DecodeState(
  private val rootCode: SymbolTree,
  var currentCode: SymbolTree,
  val currentWord: MutableList<Alphabet>
) {
  fun reset() {
    currentCode = rootCode
    currentWord.clear()
  }

  companion object {
    fun new(rootCode: SymbolTree) = DecodeState(rootCode, rootCode, mutableListOf())
  }
}

enum class Stroke {
  NOTHING,
  DIT,
  DAH,
  PAUSE_SHORT,
  PAUSE_LONG,
  ;
}

class DecodingMachine(
  coroutineScope: CoroutineScope,
  private val symbolTreeRoot: SymbolTree
) {

  private var state = DecodeState.new(symbolTreeRoot)
  private val _letterFlow = MutableSharedFlow<Char>(replay = 0)
  private val _textFlow = MutableSharedFlow<String>(replay = 0)

  val letterFlow: Flow<Char> = _letterFlow
    .shareIn(
      coroutineScope,
      replay = 0,
      started = SharingStarted.WhileSubscribed()
    )
  val textFlow: Flow<String> = _textFlow
    .shareIn(
      coroutineScope,
      replay = 0,
      started = SharingStarted.WhileSubscribed()
    )

  suspend fun receive(stroke: Stroke) = when (state.currentCode) {
    Error -> {
      when (stroke) {
        NOTHING ->{
          // Initial state. Ignore.
        }
        DIT -> {
          // Cannot decode. Ignore.
        }
        DAH -> {
          // Cannot decode. Ignore.
        }
        PAUSE_SHORT -> completeLetter(NO_LETTER)
        PAUSE_LONG -> {
          completeLetter(NO_LETTER)
          completeWord()
        }
      }
    }
    is Node -> {
      val currentCode = state.currentCode as Node
      when (stroke) {
        NOTHING -> {
        }
        DIT -> state.currentCode = currentCode.nextDit
        DAH -> state.currentCode = currentCode.nextDah
        PAUSE_SHORT -> completeLetter(currentCode.letter)
        PAUSE_LONG -> {
          completeLetter(currentCode.letter)
          completeWord()
        }
      }
    }
  }

  private suspend fun completeLetter(letter: Alphabet) {
    _letterFlow.emit(letter.letter)
    state.currentWord += letter
    state.currentCode = symbolTreeRoot
  }

  private suspend fun completeWord() {
    _textFlow.emit(state.currentWord.map { it.letter }.joinToString(separator = ""))
    state.reset()
  }
}