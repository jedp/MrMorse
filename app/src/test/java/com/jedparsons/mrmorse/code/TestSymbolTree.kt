package com.jedparsons.mrmorse.code

import com.google.common.truth.Truth.assertThat
import com.jedparsons.mrmorse.Alphabet
import org.junit.Test

class TestSymbolTree {

  private val root = SymbolTree.buildSymbolTree()

  @Test
  fun lookupAllSymbols() {
    Alphabet.values().map {
      assertThat(SymbolTree.getLetterAtCode(root, it.code).letter).isEqualTo(it.letter)
    }
  }
}
