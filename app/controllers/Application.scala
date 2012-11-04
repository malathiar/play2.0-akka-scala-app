package controllers

import com.codahale.jerkson.Json

import actors.ProductJsonActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import anorm.NotAssigned
import models.Bar
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.single
import play.api.data.Form
import play.api.libs.concurrent.akkaToPlay
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {

  val barForm = Form(
    single("name" -> nonEmptyText))

  def index = Action {
    Ok(views.html.index(barForm))
  }

  def addBar() = Action { implicit request =>
    barForm.bindFromRequest.fold(
      errors => BadRequest,
      {
        case (name) =>
          Bar.create(Bar(NotAssigned, name))
          Redirect(routes.Application.index())
      })
  }

  def listBars() = Action {
    val bars = Bar.findAll()

    val json = Json.generate(bars)

    Ok(json).as("application/json")
  }

  def listProducts() = Action {
    val system = ActorSystem("MySystem")
    val myActor = system.actorOf(Props[ProductJsonActor], name = "myactor")

    Async {
      implicit  val timeout= Timeout(20.seconds)
      
      (myActor ? "hello").mapTo[String].asPromise.map {
        response => Ok(response).as("application/json")
      }
    }
  }
}