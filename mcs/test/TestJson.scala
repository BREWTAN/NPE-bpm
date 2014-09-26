import java.io.FileInputStream
import play.api.libs.json.Json
import play.api.libs.json.JsArray

object TestJson {

  def main(args: Array[String]): Unit = {

    var ret = new JsArray
    for(a <- 1 to 5)
    ret = ret.+:(Json.obj("name" -> "lll",
      "size" -> 10,
      "error" -> "xml文件解析失败"));

    println(":::" + Json.obj("name" -> "lll",
      "size" -> 10,
      "error" -> "xml文件解析失败"))
    println(ret.toString())
  }

}