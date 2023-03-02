package com.jedparsons.mrmorse

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AlphabetTest {

  @Test
  fun testValidLetter() {
    Alphabet.values().map {
      assertThat(Alphabet.validLetter(it.letter)).isTrue()
    }

    assertThat(Alphabet.validLetter('%')).isFalse()
  }

  @Test
  fun testValidCode() {
    Alphabet.values().map {
      assertThat(Alphabet.validCode(it.code)).isTrue()
    }

    assertThat(Alphabet.validCode("_._._.")).isFalse()
  }

  @Test
  fun testForLetter() {
    Alphabet.values().map {
      assertThat(it).isEqualTo(Alphabet.forLetter(it.letter))
    }
  }

  @Test
  fun testForCode() {
    Alphabet.values().map {
      assertThat(it).isEqualTo(Alphabet.forCode(it.code))
    }
  }

  @Test
  fun testValidLetters() {
    assertThat(
      Alphabet.validLetters(
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-.,?/".toList()
      )
    ).isTrue()

    assertThat(Alphabet.validLetters("@#$%^".toList())).isFalse()
  }

  @Test
  fun testValidCodes() {
    assertThat(
      Alphabet.validCodes(
        listOf(
          ".___",
          ".",
          "_..",
          "_...",
          ".._",
          "__.",
          "."
        ).toList()
      )
    ).isTrue()

    assertThat(
      Alphabet.validCodes(
        listOf(
          ".___",
          ".",
          "_..",
          "_...",
          ".._",
          "__.",
          ".",
          "...___." // garbage
        ).toList()
      )
    ).isFalse()
  }
}
