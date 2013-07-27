package org.github.raelg.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.google.inject.Guice
import com.google.inject.Injector
import org.apache.http.conn.HttpHostConnectException
import org.github.raelg.Identifiers
import org.github.raelg.controllers.{BaseController, ControllerException, ServerException}
import java.io.IOException
import java.io.InterruptedIOException
import java.util.Timer
import java.util.TimerTask
import org.github.raelg.module.ModuleLoader
import java.net.ConnectException

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 04/04/2013
 * Time: 10:11
 */
object TheIntentService {
    final val CONTROLLER_SERVICE = "service"
    final val RECEIVER = "receiver"
}

sealed class TheIntentService extends IntentService("Service:TheOneTrueService") {

    override def onHandleIntent(intent: Intent) {
        execute(intent)
    }

    final val TAG: String = "intent-service"
    private final val MAX_RETRIES: Int = 5

    private var retryCount: Int = 0
    private final val injector: Injector = Guice.createInjector(new ModuleLoader)

    private def execute(intent: Intent) {
        val receiver: ResultReceiver = intent.getParcelableExtra(TheIntentService.RECEIVER)
        val controller = intent.getStringExtra(TheIntentService.CONTROLLER_SERVICE)
        try {
            val clazz: Class[_] = Class.forName(controller)
            val service: BaseController = injector.getInstance(clazz).asInstanceOf[BaseController]
            val bundle: Bundle = service.executeRequest(this, intent)
            receiver.send(BaseController.STATUS_SUCCESS, bundle)
        }
        catch {
            case e: ControllerException =>
                handleError(receiver, controller)
            case e: ServerException =>
                retry(intent, receiver, controller)
            case e: HttpHostConnectException =>
                Log.i(TAG, "HttpHostConnectException", e)
                retry(intent, receiver, controller)
            case e: ConnectException =>
                Log.i(TAG, "ConnectException", e)
                retry(intent, receiver, controller)
            case e: InterruptedIOException =>
                Log.i(TAG, "InterruptedIOException", e)
                retry(intent, receiver, controller)
            case e: IOException =>
                Log.i(TAG, "IOException", e)
                retry(intent, receiver, controller)
            case e: Exception =>
                Log.e(TAG, "service-exception", e)
                handleError(receiver, e.getMessage)
        }
    }

    def getSharedPreferences(context: Context): SharedPreferences = {
        context.getSharedPreferences(Identifiers.PREFERENCES, Context.MODE_PRIVATE)
    }

    private def handleError(receiver: ResultReceiver, errorMessage: String) {
        val b = new Bundle
        b.putString(Intent.EXTRA_TEXT, errorMessage)
        receiver.send(BaseController.STATUS_ERROR, b)
    }

    private def retry(intent: Intent, receiver: ResultReceiver, controller: String) {
        if (retryCount < MAX_RETRIES) {
            Log.i("intent-service", "Retrying %s, retryCount %d".format(controller, retryCount))
            retryCount += 1
            val task: TimerTask = new TimerTask {
                def run() {
                    execute(intent)
                }
            }
            val timer = new Timer
            timer.schedule(task, retryCount * 1000)
            receiver.send(BaseController.STATUS_RETRYING, new Bundle)
        }
        else {
            handleError(receiver, "Unable to establish connection")
        }
    }

}