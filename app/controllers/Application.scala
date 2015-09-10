package controllers

import javax.inject.Inject

import scala.concurrent.duration.DurationInt

import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{ Action, Controller }

import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}

/*
 * Example using ReactiveMongo + Play JSON library.
 *
 * There are two approaches demonstrated in this controller:
 * - using JsObjects directly
 * - using case classes that can be turned into Json using Reads and Writes.
 *
 * This controller uses case classes and their associated Reads/Writes
 * to read or write JSON structures.
 *
 * Instead of using the default Collection implementation (which interacts with
 * BSON structures + BSONReader/BSONWriter), we use a specialized
 * implementation that works with JsObject + Reads/Writes.
 *
 * Of course, you can still use the default Collection implementation
 * (BSONCollection.) See ReactiveMongo examples to learn how to use it.
 */
class Application @Inject() (
  val reactiveMongoApi: ReactiveMongoApi,
  val messagesApi: MessagesApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  implicit val timeout = 10.seconds


}
