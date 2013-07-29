package org.github.raelg

import android.view.Menu
import org.github.raelg.controllers.{BaseController, WeatherController}
import org.github.raelg.controllers.model.Weather
import android.util.Log
import android.widget.TextView
import org.github.raelg.controllers.FunctionToResultReceiver.fnToResultReceiver
import android.app.Activity
import android.os.{Bundle, Handler}

class HelloAndroidActivity extends Activity {

    private lazy val errorTxt : TextView = findViewById(R.id.error_txt).asInstanceOf[TextView]
    private lazy val tempTxt : TextView = findViewById(R.id.temp).asInstanceOf[TextView]
    private lazy val humidityTxt : TextView = findViewById(R.id.humidity).asInstanceOf[TextView]

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        errorTxt.setText("")

        implicit val handler = new Handler

        val weatherControllerIntent = new WeatherController.IntentBuilder(this,
            (resultCode: Int, resultData: Bundle) =>
                resultCode match {
                    case BaseController.STATUS_SUCCESS =>
                        val weather = resultData.getSerializable(WeatherController.Args.Weather).asInstanceOf[Weather]
                        tempTxt.setText("%.2f".format(weather.main.tempInCelsius))
                        humidityTxt.setText(weather.main.humidity.toString)
                        errorTxt.setText("")
                    case BaseController.STATUS_ERROR =>
                        errorTxt.setText("Unable to connect to server")
                    case BaseController.STATUS_RETRYING =>
                        Log.d("WeatherController", "retrying")
                        errorTxt.setText("You may have poor 3G or Wifi connectivity")
                }
            , "London,uk").toIntent
        startService(weatherControllerIntent)
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        getMenuInflater.inflate(R.menu.main, menu)
        true
    }
}