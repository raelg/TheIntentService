package org.github.raelg.test

import android.test.AndroidTestCase
import org.github.raelg.controllers.{BaseController, WeatherController}
import org.github.raelg.controllers.model.Weather
import java.util.concurrent.Semaphore
import android.util.Log
import junit.framework.Assert._
import org.github.raelg.controllers.FunctionToResultReceiver.fnToResultReceiver
import android.os.{Bundle, Handler}


/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 24/07/2013
 * Time: 18:47
 */
class WeatherControllerIntegrationTest() extends AndroidTestCase {

    override def setUp() = {
        super.setUp()

        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST)
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST)
        /*
            adb shell setprop log.tag.org.apache.http.headers VERBOSE
            adb shell setprop log.tag.org.apache.http.wire VERBOSE
         */
    }

    def testWeatherController() : Unit = {
        val semaphore = new Semaphore(1, true)
        semaphore.acquire()

        implicit val handler = null

        val weatherControllerIntent = new WeatherController.IntentBuilder(getContext,
            (resultCode: Int, resultData: Bundle) =>
                resultCode match {
                    case BaseController.STATUS_SUCCESS =>
                        val weather = resultData.getSerializable(WeatherController.Args.Weather).asInstanceOf[Weather]
                        assertNotNull(weather.main.temp)
                        assertNotNull(weather.main.humidity)
                        semaphore.release()
                    case BaseController.STATUS_ERROR =>
                        fail("failure")
                        semaphore.release()
                    case BaseController.STATUS_RETRYING =>
                        Log.d("WeatherController", "retrying")
                    case _ =>
                        semaphore.release()
                        fail("failure")
                        throw new RuntimeException("Failure")
                }
        , "London,uk").toIntent
        getContext.startService(weatherControllerIntent)

        waitForSignal(semaphore)
    }

    def waitForSignal(semaphore: Semaphore) {
        while (semaphore.availablePermits() == 0) {
            // wait
        }
    }

}
