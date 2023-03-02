package com.jedparsons.mrmorse

/**
 * Source: http://www.arrl.org/learning-morse-code
 */
enum class Alphabet(
  val letter: Char,
  val code: String
) {

  NO_LETTER('~', ""),
  ALPHA('A', "._"),
  BRAVO('B', "_..."),
  CHARLIE('C', "_._."),
  DELTA('D', "_.."),
  ECHO('E', "."),
  FOXTROT('F', ".._."),
  GOLF('G', "__."),
  HOTEL('H', "...."),
  INDIA('I', ".."),
  JULIET('J', ".___"),
  KILO('K', "_._"),
  LIMA('L', "._.."),
  MIKE('M', "__"),
  NOVEMBER('N', "_."),
  OSCAR('O', "___"),
  PAPA('P', ".__."),
  QUEBEC('Q', "__._"),
  ROMEO('R', "._."),
  SIERRA('S', "..."),
  TANGO('T', "_"),
  UNIFORM('U', ".._"),
  VICTOR('V', "..._"),
  WHISKEY('W', ".__"),
  XRAY('X', "_.._"),
  YANKEE('Y', "_.__"),
  ZULU('Z', "__.."),
  ONE('1', ".____"),
  TWO('2', "..___"),
  THREE('3', "...__"),
  FOUR('4', "...._"),
  FIVE('5', "....."),
  SIX('6', "_...."),
  SEVEN('7', "__..."),
  EIGHT('8', "___.."),
  NINER('9', "____."),
  ZERO('0', "_____"),
  // Punctuation.
  DASH('-', "_..._"),
  PERIOD('.', "._._._"),
  COMMA(',', "__..__"),
  QUESTION('?', "_.._.."),
  SLASH('/', "_.._."),
  // Special Characters.
  ERROR('*', "........"),
  BREAK(';', "..._._"),
  ;

  override fun toString(): String = letter.toString()

  companion object {

    private val letterMap: Map<Char, Alphabet>
      get() = Alphabet.values().associateBy { it.letter }

    private val codeMap: Map<String, Alphabet>
      get() = Alphabet.values().associateBy { it.code }

    fun forLetter(letter: Char): Alphabet = letterMap[letter] ?: NO_LETTER

    fun forCode(code: String): Alphabet = codeMap[code] ?: NO_LETTER

    fun validLetter(letter: Char): Boolean = letter in letterMap

    fun validLetters(letters: List<Char>): Boolean = letters.map { validLetter(it) }.all { it }

    fun validCode(code: String) = code in codeMap

    fun validCodes(codes: List<String>): Boolean = codes.map { validCode(it) }.all { it }

    fun lettersToWord(letters: List<Char>): List<Alphabet> = letters.map { forLetter(it) }

    fun codesToWord(codes: List<String>): List<Alphabet> = codes.map { forCode(it) }
  }
}
