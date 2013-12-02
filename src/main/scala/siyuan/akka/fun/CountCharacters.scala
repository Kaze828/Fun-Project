package siyuan.akka.fun

import java.text.DecimalFormat;

//Implement the missing methods.
object CountCharacters {
  val TENS = Array("",
    " ten",
    " twenty",
    " thirsty",
    " forty",
    " fifty",
    " sixty",
    " seventy",
    " eighty",
    " ninety")

  val NUMBERS = Array(
    "",
    " one",
    " two",
    " three",
    " four",
    " five",
    " six",
    " seven",
    " eight",
    " nine",
    " ten",
    " eleven",
    " twelve",
    " thirteen",
    " fourteen",
    " fifteen",
    " sixteen",
    " seventeen",
    " eighteen",
    " nineteen")
  val SCALES = Array(" billion", " million", " thousand", "")

  def hundreds( i : Int ) : String = {
    if ( (i /100) > 0 ) {
      NUMBERS( i /100 ) + " hundred"
    }else{
      ""
    }
  }

  def tens( i: Int ) :String ={
    if( i < 20 ){
      NUMBERS(i)
    }else{
      TENS( i /10 )
    }
  }

  def digits( i: Int): String ={
    NUMBERS(i)
  }

  def transfer( i: Int, s: Int): String = {
    if (i == 0) return ""
    if (i % 100 > 20) {
      return  hundreds(i) + tens(i % 100) + digits(i%10) + SCALES(s) + " "
    } else{
      return  hundreds(i) + tens(i % 100) + SCALES(s) + " "
    }
  }

  /*
  returns i as spelled in english (without commas, "and"s etc)
  assume US notation, ie billion = 10^9
  eg.
  toWords(9) = "nine"
  toWords(99) = "ninety nine"
  toWords(999) = "nine hundred ninety nine"
  */
  def toWords(i: Int): String = {
    /*
    range -2,147,483,648 to 2,147,483,647
    */

    if (i == 0) {
      return "zero"
    }

    /* get the abs value of i;
    ~i+1 is faster than Math.abs(i)
     */
    val abs_i = if (i < 0) (~i + 1) else i

    /*
    format it to be length by 12 so that can be easily divided by 3
     */
    val list = (new DecimalFormat("000000000000")).format(abs_i).sliding(3, 3).toList

    /*
    transfer for each part and then append them
    */
    var acc = ""
    list.indices.foreach(index => acc = acc + transfer(Integer.valueOf( list(index) ), index))

    // add minus to negative number
    return if (i > 0) acc else "minus " + acc;

  }

  //countCharsInWords(9) = 4
  //countCharsInWords(99) = 10
  //countCharsInWords(999) = 21
  def countCharsInWords(i: Int): Int = toWords(i).filter(_ != ' ').length


  /*
  more efficient implementation of countCharsInWords.
  */
  def countCharsInWordsOptimised(i: Int): Int = toWords(i).par.count( _ != ' ')

  /*
  more efficient implementation of countCharsInWords.
  */
  //def countCharsInWordsOptimised(i: Int): Int = ???
  def main(args: Array[String]) {
    println(toWords(-2147483647))
    println(toWords(546))
    println(toWords(13016))
    println(toWords(1006))
        println(countCharsInWords(-2147483647))
    println(countCharsInWordsOptimised(9))
    println(countCharsInWordsOptimised(99))
    println(countCharsInWordsOptimised(999))

  }

}

