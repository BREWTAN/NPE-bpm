package controllers

import java.io.FileInputStream
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.Range
import org.nights.npe.po.Definition.CallActivity
import org.nights.npe.po.Definition.Process
import org.nights.npe.utils.POHelper
import org.nights.npe.utils.ProcDefMap
import com.fasterxml.jackson.databind.JsonNode
import com.github.mauricio.async.db.QueryResult
import akka.actor.actorRef2Scala
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka
import org.nights.npe.backend.db.KOProcdef
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.DeserializationFeature

object ProcDefHttpFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  def test = Action {
    Ok("kkk");
    //    Ok("Got request [" + request + "]")
  }

  def getByPage(skip: Int, limit: Int, page: Boolean) = Action.async { request =>
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
              "createtime" -> row("createtime").toString)
          })
        }))
      Ok(jsonRet)

    })
  }

  def getMainProc = Action.async { request =>

    ProcDefDAO.findAll().map({ f =>
      val jsonRet = Json.toJson(f.rows.map({ rs =>
        rs.map({ row =>
          Json.obj("defid" -> row("defid").asInstanceOf[String],
            "defname" -> row("defname").asInstanceOf[String],
            "version" -> row("version").asInstanceOf[String],
            "packages" -> row("packages").asInstanceOf[String],
            "xmlbody" -> row("xmlbody").asInstanceOf[String],
            "subelements" -> row("subelements").asInstanceOf[String],
            "createtime" -> row("createtime").toString)
        })
      }))
      Ok(jsonRet)

    })
  }
  def delete(defid: String) = Action.async { request =>
    ProcDefDAO.delete(defid).map({ f =>
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def update(defid: String) = Action { request =>
    //    val json = request.body.asJson.get;
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    val body = mapper.readValue(request.body.asJson.get.toString, classOf[KOProcdef])
    println("body==" + body)
    ProcDefDAO.updateSelective(body)
    Ok("LLL")
  }
  def validate() = Action.async { request =>
    val result = ProcDefDAO.findAll()
    val validator = new ProcDefMap
    result.map { qr =>
      if (qr.rowsAffected > 0) {
        //        println("qr==" + qr + ",," + ProcDefDAO.resultRow(qr))
        ProcDefDAO.resultRow(qr).asInstanceOf[List[KOProcdef]].map { ko =>
          validator.appendDefXML(ko.xmlbody)
        }
        if (!validator.checkIntegrity) {
          val error = validator.procDefs.filter(!_._2.isvalid).mapValues({ v =>
            val p: Process = v.asInstanceOf[Process]
            Json.obj("id" -> p.id, "name" -> p.e.name, "message" -> p.parseError.mkString(";"))
          }).values.toArray

          Ok(Json.obj("status" -> "-1",
            "error" -> error))
        } else Ok(Json.obj("status" -> 0))
      } else {
        Ok(Json.obj("status" -> "-1", "message" -> "no flows found"))
      }
    }
  }

  def upload = Action(parse.multipartFormData) { request =>
    //    println("tmp file==" + request);
    //    println("okok::" + request.body.files);
    var ret = new JsArray

    request.body.files.foreach { f =>
      val is = new FileInputStream(f.ref.file);
      val filesize = is.available();
      try {
        val x = scala.xml.XML.load(is);
        val process = POHelper.fromXML(x);
        //        println("x==" + x);
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
            x.toString,
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