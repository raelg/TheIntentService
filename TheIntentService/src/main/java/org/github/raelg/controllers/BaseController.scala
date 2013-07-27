package org.github.raelg.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import org.github.raelg.services.TheIntentService
import org.github.raelg.Identifiers
import com.google.gson.GsonBuilder
import java.net.ConnectException
import java.io.IOException

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 05/04/2013
 * Time: 15:20
 */

class BaseIntentBuilder[T <: BaseController](context: Context, resultReceiver: ResultReceiver, clazz : Class[T]) {
    protected val intent = new Intent(context, classOf[TheIntentService])
    intent.putExtra(TheIntentService.CONTROLLER_SERVICE, clazz.getName)
    intent.putExtra(TheIntentService.RECEIVER, resultReceiver)
    def toIntent : Intent = intent
}

object BaseController {
    val fromGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()

    final val STATUS_SUCCESS: Int = 1
    final val STATUS_RETRYING: Int = 0
    final val STATUS_ERROR: Int = 2


    def fromJson[T](jsonString: String, clazz: Class[T]): T = {
        fromGson.fromJson(jsonString, clazz)
    }
}

abstract class BaseController(restController: RestController, bundler: Bundler) {

    @throws[ControllerException]("when the server returns a fatal 400 range response")
    @throws[ServerException]("when the server returns a 500 range response, and should retry")
    @throws[ConnectException]("when there is a connection problem")
    @throws[IOException]("when there is a connection problem")
    def executeRequest(context: Context, intent: Intent): Bundle

    protected def shouldRetry(statusCode: Int): Boolean = {
        statusCode >= 500
    }

    def getSharedPreferences(context: Context) = {
        context.getSharedPreferences(Identifiers.PREFERENCES, android.content.Context.MODE_PRIVATE)
    }

    def getSetting(context: Context, setting: String): String = {
        getSharedPreferences(context).getString(setting, null)
    }

    def storeSetting(context: Context, setting: String, value: String) {
        getSharedPreferences(context).edit.putString(setting, value).commit
    }

}