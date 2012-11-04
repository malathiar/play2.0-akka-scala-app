package actors

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import play.api.libs.ws.WS

class ProductJsonActor extends Actor {

  val log = Logging(context.system, this)
  def receive = {
    case _ => {
      val response = WS.url("https://www.googleapis.com/shopping/search/v1/public/products").get()
      val body = response.await(5000).get.body
      log.debug("body****" + body)
      sender ! body
    }
  }
}