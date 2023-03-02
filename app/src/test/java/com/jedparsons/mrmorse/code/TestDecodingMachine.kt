package com.jedparsons.mrmorse.code

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.jedparsons.mrmorse.Alphabet.NO_LETTER
import com.jedparsons.mrmorse.code.Stroke.DAH
import com.jedparsons.mrmorse.code.Stroke.DIT
import com.jedparsons.mrmorse.code.Stroke.PAUSE_LONG
import com.jedparsons.mrmorse.code.Stroke.PAUSE_SHORT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestDecodingMachine {

  private val testDispatcher = UnconfinedTestDispatcher()
  private val coroutineScope = TestScope(testDispatcher)
  private val symbolTree = SymbolTree.buildSymbolTree()

  private val machine = DecodingMachine(coroutineScope, symbolTree)

  @Test
  fun receiveGarbage() = runTest {
    machine.textFlow.flowOn(testDispatcher).test {

      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DIT)
      machine.receive(DIT)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(PAUSE_LONG)

      assertThat(awaitItem()).contains(NO_LETTER.toString())
    }
  }

  @Test
  fun receiveWord() = runTest {
    machine.textFlow.flowOn(testDispatcher).test {

      machine.receive(DIT)
      machine.receive(PAUSE_SHORT)
      machine.receive(DAH)
      machine.receive(PAUSE_LONG)

      assertThat(awaitItem()).contains("ET")
    }
  }

  @Test
  fun receiveWords() = runTest {
    machine.textFlow.flowOn(testDispatcher).test {

      // JED
      machine.receive(DIT)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(PAUSE_SHORT)
      machine.receive(DIT)
      machine.receive(PAUSE_SHORT)
      machine.receive(DAH)
      machine.receive(DIT)
      machine.receive(DIT)
      machine.receive(PAUSE_LONG)

      // BUGE
      machine.receive(DAH)
      machine.receive(DIT)
      machine.receive(DIT)
      machine.receive(DIT)
      machine.receive(PAUSE_SHORT)
      machine.receive(DIT)
      machine.receive(DIT)
      machine.receive(DAH)
      machine.receive(PAUSE_SHORT)
      machine.receive(DAH)
      machine.receive(DAH)
      machine.receive(DIT)
      machine.receive(PAUSE_SHORT)
      machine.receive(DIT)
      machine.receive(PAUSE_LONG)

      assertThat(awaitItem()).contains("JED")
      assertThat(awaitItem()).contains("BUGE")
    }
  }
}
