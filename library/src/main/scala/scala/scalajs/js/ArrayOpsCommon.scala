/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js API               **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013-2018, LAMP/EPFL   **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-lang.org/     **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */

package scala.scalajs.js

import scala.annotation.tailrec

private[js] object ArrayOpsCommon {

  /** Efficient concatenation of two JS arrays. */
  def concat[A](left: Array[_ <: A], right: Array[_ <: A]): Array[A] = {
    val leftLength = left.length
    val rightLength = right.length
    val result = new Array[A](leftLength + rightLength)

    @inline
    @tailrec
    def loop(src: Array[_ <: A], i: Int, len: Int, offset: Int): Unit = {
      if (i != len) {
        result(i+offset) = src(i)
        loop(src, i+1, len, offset)
      }
    }

    loop(left, 0, leftLength, 0)
    loop(right, 0, rightLength, leftLength)
    result
  }

  def reduceLeft[A, B >: A](array: Array[A], op: (B, A) => B): B = {
    val length = array.length
    if (length <= 0)
      throwUnsupported("empty.reduceLeft")

    @inline
    @tailrec
    def loop(start: Int, z: B): B =
      if (start == length) z
      else loop(start+1, op(z, array(start)))

    loop(1, array(0))
  }

  def reduceRight[A, B >: A](array: Array[A], op: (A, B) => B): B = {
    val length = array.length
    if (length <= 0)
      throwUnsupported("empty.reduceRight")

    @inline
    @tailrec
    def loop(end: Int, z: B): B =
      if (end == 0) z
      else loop(end-1, op(array(end-1), z))

    loop(length-1, array(length-1))
  }

  /** Extract the throw in a separate, non-inlineable method. */
  private def throwUnsupported(msg: String): Nothing =
    throw new UnsupportedOperationException(msg)

}
