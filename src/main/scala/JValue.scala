package baovitt

sealed trait JValue:
    def copyText: Unit =
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new java.awt.datatransfer.StringSelection(this.toString), null)
end JValue

case object JNull extends JValue:
    override def toString: String = "null"
end JNull

sealed trait JBool extends JValue

object JBool:
    case object True extends JBool:
        override def toString: String = "true"
    end True
    case object False extends JBool:
        override def toString: String = "false"
    end False
end JBool

case class JString(str: String) extends JValue:
    override def toString: String = s"\"$str\""
end JString
case class JNum(num: String) extends JValue:
    override def toString: String = num
end JNum

case class JArray(seq: JValue*) extends JValue:
    override def toString: String = s"[${seq.mkString(",")}]"
end JArray

case class JObject(map: Map[String, JValue]) extends JValue:
    private inline def recordToString(kv: (String, JValue)): String =
        s"\"${kv._1}\":${kv._2}"
    override def toString: String = s"{${map.toList.map(recordToString(_)).mkString(",")}}"
end JObject