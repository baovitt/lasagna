package baovitt

import cats.parse.{Parser0, Parser => P, Numbers}
import cats.parse.strings.Json.delimited.{parser => jsonString}

object Lasagna:
  private val whitespace: P[Unit] = P.charIn(" \t\r\n").void
  private val whitespaces0: Parser0[Unit] = whitespace.rep0.void

  private val parser: P[JValue] = P.recursive[JValue] { recurse =>
    val pnull = P.string("null").as(JNull)
    val bool = P.string("true").as(JBool.True).orElse(P.string("false").as(JBool.False))
    val str = jsonString.map(JString(_))
    val num = Numbers.jsonNumber.map(JNum(_))

    val listSep: P[Unit] =
      P.char(',').soft.surroundedBy(whitespaces0).void

    def rep[A](pa: P[A]): Parser0[List[A]] =
      pa.repSep0(listSep).surroundedBy(whitespaces0)

    val list = rep(recurse).with1
      .between(P.char('['), P.char(']'))
      .map { vs => JArray(vs*) }

    val kv: P[(String, JValue)] =
      jsonString ~ (P.char(':').surroundedBy(whitespaces0) *> recurse)

    val obj = rep(kv).with1
      .between(P.char('{'), P.char('}'))
      .map { vs => JObject(vs.toMap) }

    P.oneOf(str :: num :: list :: obj :: bool :: pnull :: Nil)
  }

  private def apply(value: String): Either[LasagnaError, Lasagna] =
    parser.parse(value.trim)
        .map(json => new Lasagna(json._2))
        .left.map (_ => FailedToParseError)

  def fromString(json: String): Either[LasagnaError, Lasagna] =
    apply(json)
        
end Lasagna

final case class Lasagna private (json: JValue):

  private def copy: Unit = ()

  override def toString: String = json.toString

  def copyText: Unit = json.copyText

  def getJValue: JValue = json
    
end Lasagna