package controllers

import java.io.FileInputStream

import org.nights.npe.util.POHelper

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.routing.Broadcast
import akka.routing.FromConfig
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller

object ProcDefHttpFace extends Controller {

  def test = Action {
    Ok("kkk");
    //    Ok("Got request [" + request + "]")
  }

  def upload = Action(parse.multipartFormData) { request =>
    println("tmp file==" + request);
    println("okok::" + request.body.files);
    var ret = new JsArray

    request.body.files.foreach { f =>
      val is = new FileInputStream(f.ref.file);
      val filesize = is.available();
      try {
        val x = scala.xml.XML.load(is);
        val process = POHelper.fromXML(x);
        println("x==" + x);
        if (process.isvalid) {
        	ret = ret.+:(Json.obj("name" -> f.filename,
                        "url" -> "/npe/procdef/import.html#",
            "delete_url" -> "/npe/procdef/import.html#delete",
            "delete_type" -> "DELETE",
            "type" -> "xml/text",
        	"size" -> filesize
        	));
        } else {
          ret = ret.+:(Json.obj("name" -> f.filename,
            "size" -> filesize,
            "isvalid" -> process.isvalid,
            "error" -> process.parseError.toString));
        }

      } catch {
        case sax: org.xml.sax.SAXParseException => {
          sax.printStackTrace();
          ret = ret.+:(Json.obj("name" -> f.filename,
            "size" -> filesize,
            "isvalid" -> false,
            "error" -> "xml文件解析失败"));
        }
      } finally {
        //        is.close()
      }

    };
    //      println("data=="+request.body.asMultipartFormData);
    //    ret = Json.arr( 
    //      Json.obj(
    //        "name" -> "file",
    //        "size" -> 100
    //      )
    //        
    //    );
    println("return ::" + ret)
    Ok(Json.obj("files" -> ret));

    //      request.body.file("picture").map { picture =>
    //        import java.io.File
    //        val filename = picture.filename
    //        val contentType = picture.contentType
    //        picture.ref.moveTo(new File("/tmp/picture"))
    //        Ok("File uploaded")
    //      }.getOrElse {
    //        Redirect(routes.Application.index).flashing(
    //          "error" -> "Missing file")
    //      }
  }
}