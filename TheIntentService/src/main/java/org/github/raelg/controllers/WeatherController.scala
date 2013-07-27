package org.github.raelg.controllers

import android.content.Context
import android.content.Intent
import android.os.{ResultReceiver, Bundle}
import com.google.inject.Inject
import org.apache.http.HttpStatus
import org.github.raelg.controllers.model._

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 22/07/2013
 * Time: 23:46
 */

object WeatherController {

    object Args {
        val Weather = "user"
    }

    private val I_LOCATION = "location"

    class IntentBuilder(context: Context, resultReceiver: ResultReceiver, location: String) extends BaseIntentBuilder(context, resultReceiver, classOf[WeatherController]){
        intent.putExtra(I_LOCATION, location)
    }
}


class WeatherController @Inject()(controller: RestController, bundler: Bundler) extends BaseController(controller, bundler) {

    override def executeRequest(context: Context, intent: Intent): Bundle = {

        val location = intent.getStringExtra(WeatherController.I_LOCATION)
        val url = "http://api.openweathermap.org/data/2.5/weather?q=" + location
        val request = controller.getRequest(url)

        val (status, response) = controller.getResponse(request)

        val bundle = bundler.createBundle

        status match {
            case HttpStatus.SC_OK =>

                val weather = BaseController.fromJson(response, classOf[Weather])
                // this should be putParcelable
                bundle.putSerializable(WeatherController.Args.Weather, weather)

                bundle
            case _ =>
                if (shouldRetry(status))
                    throw new ServerException("Server error, status: " + status)
                else
                    throw new ControllerException("Fatal error, status: " + status)
        }
    }
}