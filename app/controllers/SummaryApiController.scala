package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{ Action, Controller }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, Json}, Json.toJsFieldJsValueWrapper

import play.modules.reactivemongo.{
MongoController, ReactiveMongoApi, ReactiveMongoComponents
}
import play.modules.reactivemongo.json._, collection.JSONCollection

import scala.concurrent.{Future, TimeoutException}

class SummaryApiController @Inject() (val reactiveMongoApi: ReactiveMongoApi,
                                      val messagesApi: MessagesApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("summaries")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //
  import play.api.data.Form
  import models._
  import models.Summary._

  /**
   * Handle default path requests, redirect to employee list
   */
  def index = Action { Home }

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.SummaryApiController.list())

  /**
   * Display the paginated list of employees.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on employee names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    val futurePage = if (filter.length > 0) {
      collection.find(Json.obj("name" -> filter)).cursor[Summary]().collect[List]()
    }
    else collection.genericQueryBuilder.cursor[Summary]().collect[List]()

      futurePage.map({ summaries =>
        implicit val msg = messagesApi.preferred(request)

      Ok(Json.toJson(Map("summaries" -> summaries)))
    }).recover {
      case t: TimeoutException =>
        Logger.error("Problem found in summary list process")
        InternalServerError(t.getMessage)
    }
  }

  /**
   * Display the 'edit form' of a existing Employee.
   *
   * @param id Id of the employee to edit
   */
//  def edit(id: String) = Action.async { request =>
//    val futureEmp = collection.find(Json.obj("_id" -> Json.obj("$oid" -> id))).cursor[Employee]().collect[List]()
//    futureEmp.map { emps: List[Employee] =>
//      implicit val msg = messagesApi.preferred(request)
//
//      Ok(html.editForm(id, employeeForm.fill(emps.head)))
//    }.recover {
//      case t: TimeoutException =>
//        Logger.error("Problem found in employee edit process")
//        InternalServerError(t.getMessage)
//    }
//  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the employee to edit
   */
//  def update(id: String) = Action.async { implicit request =>
//    employeeForm.bindFromRequest.fold(
//    { formWithErrors =>
//      implicit val msg = messagesApi.preferred(request)
//      Future.successful(BadRequest(html.editForm(id, formWithErrors)))
//    },
//    employee => {
//      val futureUpdateEmp = collection.update(Json.obj("_id" -> Json.obj("$oid" -> id)), employee.copy(_id = BSONObjectID(id)))
//      futureUpdateEmp.map { result =>
//        Home.flashing("success" -> s"Employee ${employee.name} has been updated")
//      }.recover {
//        case t: TimeoutException =>
//          Logger.error("Problem found in employee update process")
//          InternalServerError(t.getMessage)
//      }
//    })
//  }

  /**
   * Handle the 'new employee form' submission.
   */
  def create = Action.async(parse.json) { implicit request =>
    request.body.validate[Summary].map {
      summary =>
        collection.insert(summary).map {
          lastError =>
            Logger.debug(s"Successfully updated with lastError: $lastError")
            Created(s"Summary updated")
        }
    }.recoverTotal{
      e => Future.successful(BadRequest(JsError.toJson(e)))
    }
  }

  /**
   * Handle employee deletion.
   */
//  def delete(id: String) = Action.async {
//    val futureInt = collection.remove(Json.obj("_id" -> Json.obj("$oid" -> id)), firstMatchOnly = true)
//    futureInt.map(i => Home.flashing("success" -> "Employee has been deleted")).recover {
//      case t: TimeoutException =>
//        Logger.error("Problem deleting employee")
//        InternalServerError(t.getMessage)
//    }
//  }

}
