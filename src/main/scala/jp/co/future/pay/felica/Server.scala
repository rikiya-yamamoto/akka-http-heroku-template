package jp.co.future.pay.felica

import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.unmarshalling.Unmarshal._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.util.Properties

//import akka.http.scaladsl.server.directives.MarshallingDirectives._

import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration._

case class UpdateRequest(encryptedMessage: String)


object Main extends App {
  val repeat = 1000

  val port = Properties.envOrElse("PORT", "8080").toInt
  // for Heroku compatibility
  implicit val system = ActorSystem("test")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(20 seconds)

  //
//  val dbUri = new URI(System.getenv("DATABASE_URL"))
//  val username = dbUri.getUserInfo.split(":")(0)
//  val password = dbUri.getUserInfo.split(":")(1)
//  var dbUrl = s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"
  if (System.getenv("STACK") == null) {
    // means we're not on Heroku
//    dbUrl = s"${dbUrl}?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
  }
//  val db = Database.forURL(dbUrl, driver = "org.postgresql.Driver", user = username, password = password)

  import system.dispatcher

  val route = path("activate") {
    post {
      formFields('encryptedRequest) { encryptedRequest => {
        complete {
          <r>activate</r>
        }
      }
      }
    }
  } ~ path("rebalance") {
    post {
      formFields('encryptedRequest) { encryptedRequest => {
        complete {
          <r>rebalance</r>
        }
      }
      }
    }
  } ~ path("validate") {
    post {
      formFields('encryptedRequest) { encryptedRequest => {
        complete {
          println(encryptedRequest)
          <r>validate</r>
        }
      }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port)
  println("Starting on port: " + port)
}

