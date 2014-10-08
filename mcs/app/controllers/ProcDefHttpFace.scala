package controllers

import java.io.FileInputStream
import scala.concurrent.ExecutionContext
import org.nights.npe.util.POHelper
import akka.actor.actorRef2Scala
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka
import org.nights.npe.fsm.backend.db.ProcDefDAO
import org.nights.npe.fsm.backend.db.Range
import com.github.mauricio.async.db.QueryResult
import scala.concurrent.Future
import play.mvc.SimpleResult

object ProcDefHttpFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  def test = Action {
    Ok("kkk");
    //    Ok("Got request [" + request + "]")
  }

  def getByPage(skip: Int, limit: Int) = Action.async { request =>
    val result = ProcDefDAO.findAll(Range(skip, limit))
    result.map { qr =>
        Ok("getPage At:" + qr)
    }

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
            "size" -> filesize));
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
    Ok(Json.obj("files" -> ret));

  }
}