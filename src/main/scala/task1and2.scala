import spray.json.DefaultJsonProtocol.jsonFormat4
import java.io._
import scala.io.Source
import scala.util.parsing.json._
import spray.json.DefaultJsonProtocol._
import spray.json._

case class Impression(id: Option[String], app_id: Option[Int], country_code: Option[String], advertiser_id: Option[Int])

case class Click(impression_id: Option[String], revenue: Option[Double])


object Impression {
  implicit val format: JsonFormat[Impression] = jsonFormat4(Impression.apply)
}

object Click {
  implicit val format: JsonFormat[Click] = jsonFormat2(Click.apply)
}

object task1and2 {
  def main(args: Array[String]): Unit = {


    val impressionFile = "input/impressions.json"
    val clickFile = "input/clicks.json"

    val jsonString_im = Source.fromFile(impressionFile).mkString
    val jsonString_cl = Source.fromFile(clickFile).mkString

//Read events stored in JSON files
    val impressions = try {
      jsonString_im.parseJson.convertTo[List[Map[String, JsValue]]]
        .flatMap { impressionMap =>
          val id = impressionMap.get("id").flatMap(_.convertTo[Option[String]])
          val app_id = impressionMap.get("app_id") match {
            case Some(JsNumber(n)) => Some(n.toInt)
            case Some(JsString(s)) if s.nonEmpty => Some(s.toInt)
            case _ => None
          }
          val country_code = impressionMap.get("country_code").flatMap(_.convertTo[Option[String]])
          val advertiser_id = impressionMap.get("advertiser_id") match {
            case Some(JsNumber(n)) => Some(n.toInt)
            case Some(JsString(s)) if s.nonEmpty => Some(s.toInt)
            case _ => None
          }
          List(Impression(id, app_id, country_code, advertiser_id))
        }

    } catch {
      case e: Exception =>
        println(s"Error parsing JSON: ${e.getMessage}")
        List.empty[Impression]
    }

    //        println(s"Number of impressions: ${impressions.length}")
    //        impressions.foreach(println)
    val clicks = try {
      jsonString_cl.parseJson.convertTo[List[Map[String, JsValue]]]
        .flatMap { clickMap =>
          val impression_id = clickMap.get("impression_id").flatMap(_.convertTo[Option[String]])
          val revenue = clickMap.get("revenue") match {
            case Some(JsNumber(n)) => Some(n.toDouble)
            case Some(JsString(s)) if s.nonEmpty => Some(s.toDouble)
            case _ => None
          }
          List(Click(impression_id, revenue))
        }

    } catch {
      case e: Exception =>
        println(s"Error parsing JSON: ${e.getMessage}")
        List.empty[Click]
    }
//    Calculate metrics for some dimensions
    val result = impressions
      .groupBy(impression => (impression.app_id, impression.country_code))
      .mapValues { impressions =>
        val impressionCount = impressions.size
        val clickCount = clicks.count(click => impressions.exists(_.id == click.impression_id))
        val revenueSum = clicks.filter(click => impressions.exists(_.id == click.impression_id))
          .map(_.revenue.getOrElse(0.0)).sum
        (impressionCount, clickCount, revenueSum)
      }
      .map { case ((appId, countryCode), (impressionCount, clickCount, revenueSum)) =>
        JsObject(
          "app_id" -> JsNumber(appId.getOrElse(0)),
          "country_code" -> JsString(countryCode.getOrElse("")),
          "impressions" -> JsNumber(impressionCount),
          "clicks" -> JsNumber(clickCount),
          "revenue" -> JsNumber(revenueSum)
        )
      }
      .toList


    val file = new File("output2.json")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(result.toJson.compactPrint)
    bw.close()
//    Make a recommendation for the top 5 advertiser_ids to display for each app and country combination.
    val impressionsByAppAndCountry = impressions.groupBy(impression => (impression.app_id, impression.country_code))

    val revenuePerImpressionByAdvertiser = impressions.flatMap { impression =>
      val clicksForImpression = clicks.filter(_.impression_id == impression.id)
      clicksForImpression.map { click =>
        (impression.advertiser_id, click.revenue.getOrElse(0.0))
      }
    }.groupBy(_._1).mapValues { clicksForAdvertiser =>
      val (revenueSum, impressionCount) = clicksForAdvertiser.foldLeft((0.0, 0)) { case ((sum, count), (advertiser_id, revenue)) =>
        (sum + revenue, count + 1)
      }
      if (impressionCount > 0) revenueSum / impressionCount else 0.0
    }

    val topAdvertisersByAppAndCountry = impressionsByAppAndCountry.map { case ((appId, countryCode), impressions) =>
      val advertisersWithRates = impressions.flatMap { impression =>
        impression.advertiser_id.map { advertiserId =>

          (impression.advertiser_id, revenuePerImpressionByAdvertiser.get(impression.advertiser_id).map(Some(_)).getOrElse(None))
        }
      }
      val topAdvertisers = advertisersWithRates.filter(_._2.getOrElse(0.0) > 0.0).sortBy(-_._2.getOrElse(0.0)).take(5).map(_._1)
      JsObject(
        "app_id" -> JsNumber(appId.getOrElse(0)),
        "country_code" -> JsString(countryCode.getOrElse("")),
        "recommended_advertiser_ids" -> JsArray(topAdvertisers.map(id => JsNumber(id.getOrElse(0))).toVector)
      )
    }.toList


//    val resultString = topAdvertisersByAppAndCountry.toJson.prettyPrint
//    println(resultString)

    val file3 = new File("output3.json")
    val bw3 = new BufferedWriter(new FileWriter(file3))
    bw3.write(topAdvertisersByAppAndCountry.toJson.compactPrint)
    bw3.close()


  }
}