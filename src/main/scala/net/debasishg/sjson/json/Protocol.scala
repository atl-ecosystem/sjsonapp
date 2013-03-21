package net.debasishg
package sjson
package json

import scalaz._
import Scalaz._

import dispatch.classic.json._
import JsonSerialization._

trait Writes[T] {
  def writes(o: T): ValidationNel[String, JsValue]
}

trait Reads[T] {
  def reads(json: JsValue): ValidationNel[String, T]
}

trait Format[T] extends Writes[T] with Reads[T]

trait Protocol {
  implicit def IntFormat: Format[Int]
  implicit def ShortFormat: Format[Short]
  implicit def LongFormat: Format[Long]
  implicit def BooleanFormat: Format[Boolean]
  implicit def FloatFormat: Format[Float]
  implicit def DoubleFormat: Format[Double]
  implicit def StringFormat: Format[String]
}

trait DefaultProtocol extends CollectionTypes with Generic with Primitives
object DefaultProtocol extends DefaultProtocol {
  def field[T: Reads](name: String, json: JsValue): ValidationNel[String, T] = {
    val JsObject(m) = json
    m.get(JsString(name))
     .map(fromjson[T](_))
     .getOrElse(("field " + name + " not found").fail.toValidationNel)
  }

  def field[T: Reads](name: String, json: JsValue, valid: T => ValidationNel[String, T]): ValidationNel[String, T] = {
    val JsObject(m) = json
    m.get(JsString(name))
     .map(fromjson[T](_).flatMap(valid))
     .getOrElse(("field " + name + " not found").fail.toValidationNel)
  }

  def field_c[T: Reads](name: String): JsValue => ValidationNel[String, T] = {json: JsValue =>
    val JsObject(m) = json
    m.get(JsString(name))
     .map(fromjson[T](_))
     .getOrElse(("field " + name + " not found").fail.toValidationNel)
  }
}
