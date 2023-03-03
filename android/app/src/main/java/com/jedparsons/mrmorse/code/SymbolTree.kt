package com.jedparsons.mrmorse.code

import com.jedparsons.mrmorse.Alphabet
import com.jedparsons.mrmorse.Alphabet.ERROR
import com.jedparsons.mrmorse.Alphabet.NO_LETTER
import logcat.logcat

sealed class SymbolTree {
  object Error : SymbolTree()

  data class Node(
    var letter: Alphabet,
    var nextDit: SymbolTree,
    var nextDah: SymbolTree
  ) : SymbolTree()

  companion object {

    /**
     * Map Alphabet into a symbol tree we can traverse one dit or dah at a time.
     */
    fun buildSymbolTree(): Node {

      val root = Node(NO_LETTER, Error, Error)

      Alphabet.values().map { symbol ->
        var start: Node = root
        val len = symbol.code.length
        symbol.code.toCharArray().mapIndexed { index, c ->
          val next: SymbolTree = when (c) {
            '.' -> {
              // Maybe create a new node.
              if (start.nextDit == Error) {
                start.nextDit = Node(ERROR, Error, Error)
              }
              start.nextDit
            }
            '_' -> {
              // Maybe create a new node.
              if (start.nextDah == Error) {
                start.nextDah = Node(ERROR, Error, Error)
              }
              start.nextDah
            }
            else -> error("Not a dit or a dah: $c")
          }

          // Assign our letter if we are at the end of the path.
          if (index == len - 1) {
            logcat { "Assigning $symbol" }
            (next as Node).letter = symbol
          }
          start = next as Node
        }
      }
      return root
    }

    fun getLetterAtCode(
      root: Node,
      code: String
    ): Alphabet {
      var node = root
      code.toCharArray().map { c ->
        val next = when (c) {
          '.' -> node.nextDit
          '_' -> node.nextDah
          else -> error("Not a dit or dah: $c")
        }
        node = next as Node
      }
      return node.letter
    }
  }
}