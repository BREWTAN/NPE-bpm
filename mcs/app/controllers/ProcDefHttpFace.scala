package controllers

import java.io.FileInputStream

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.nights.npe.backend.db.ProcDefDAO;
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.Range
import org.nights.npe.po.Definition.CallActivity
import org.nights.npe.utils.POHelper

import com.github.mauricio.async.db.QueryResult

import akka.actor.actorRef2Scala
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka
import scala.util.Success

object ProcDefHttpFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  def test = Action {
    Ok("kkk");
    //    Ok("Got request [" + request + "]")
  }

  def getByPage(skip: Int, limit: Int) = Action.async { request =>
    val results = for {
      total <- ProcDefDAO.countAll
      rows <- ProcDefDAO.findAll(Range(skip, limit))
    } yield (total, rows)

    results.map({ f =>
      val jsonRet = Json.obj("total_rows" -> f._1.get,
        "count" -> f._2.rowsAffected,
        "fromRow" -> skip,
        "limit" -> limit,
        "rows" -> f._2.rows.map({ rs =>
          rs.map({ row =>
            Json.obj("defid" -> row("defid").asInstanceOf[String],
              "defname" -> row("defname").asInstanceOf[String],
              "version" -> row("version").asInstanceOf[String],
              "packages" -> row("packages").asInstanceOf[String],
              "xmlbody" -> row("xmlbody").asInstanceOf[String],
              "subelements" -> row("subelements").asInstanceOf[String],
              "createtime" -> row("createtime").asInstanceOf[String])
          })
        }))
      Ok(jsonRet)

    })
  }

  def validate() = Action.async { request =>
    val result = ProcDefDAO.findAll()
    result.map { qr =>
      qr.rows.map(_.asInstanceOf[KOProcdef]).map { ko =>
        println(ko.xmlbody)
      }
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

          val procdef = KOProcdef(
            process.id,
            process.taskName,
            process.Version,
            process.Package,
            process.xmlBody,
            process.nodes.filter(_._2.isInstanceOf[CallActivity]).mapValues(_.asInstanceOf[CallActivity].calledElement).values.mkString(","),
            Some(System.currentTimeMillis()));

          ProcDefDAO.insertOrUpdate(procdef)

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