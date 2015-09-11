package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, JsObject, OWrites}
import reactivemongo.bson.BSONObjectID

case class Summary(_id: Option[BSONObjectID], gtin: String, title: String, bookTitle: String, subTitle: String,
                   description: String, content: String, creationDate: Option[DateTime], modificationDate: Option[DateTime])

object Summary {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import play.api.libs.functional._
  import play.modules.reactivemongo.json.BSONFormats._

//  implicit val summary: Format[Summary] = Json.format[Summary]

  implicit val dateTimeReads = Reads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
  implicit val dateTimeWrites = Writes.jodaDateWrites("yyyy-MM-dd HH:mm:ss")

  implicit val summaryReads = Json.reads[Summary]
//  implicit val summaryWrites = Json.writes[Summary]

//  implicit val summaryReads: Reads[Summary] = (
//    (__ \ "type").read[String](Reads.verifying[String](_ == "summaries")) andKeep
//    (__ \ "gtin").read[String] and
//    (__ \ "title").read[String]
//  )((gtin, title) => Summary(None, gtin, title, "", "", "", "", None, None))


  implicit val summaryWrites: OWrites[Summary] = (
    (__ \ "type").write[String] and
    (__ \ "gtin").write[String] and
    (__ \ "title").write[String]
  ) { summary: Summary => ("summaries", summary.gtin, summary.title) }

}

