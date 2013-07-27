package org.github.raelg

import android.view.Menu
import org.github.raelg.controllers.{BaseController, FakeServiceController}
import org.github.raelg.controllers.model.Weather
import android.util.Log
import android.widget.TextView
import org.github.raelg.controllers.FunctionToResultReceiver.fnToResultReceiver
import android.app.Activity
import android.os.{Bundle, Handler}

class HelloAndroidActivity extends Activity{


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

        val errorTxt : TextView = findViewById(R.id.error_txt).asInstanceOf[TextView]
        val temp : TextView = findViewById(R.id.temp).asInstanceOf[TextView]
        val humidity : TextView = findViewById(R.id.humidity).asInstanceOf[TextView]

        errorTxt.setText("")

        implicit val handler = new Handler

        val fakeServiceControllerIntent = FakeServiceController.IntentBuilder(this,
            (resultCode: Int, resultData: Bundle) =>
                resultCode match {
                    case BaseController.STATUS_SUCCESS =>
                        val weather = resultData.getSerializable(FakeServiceController.Args.Weather).asInstanceOf[Weather]
                        temp.setText("%.2f".format(weather.main.temp - 273.15))
                        humidity.setText(weather.main.humidity.toString)
                        errorTxt.setText("")
                    case BaseController.STATUS_ERROR =>
                        errorTxt.setText("Unable to connect to server")
                        errorTxt.setText("")
                    case BaseController.STATUS_RETRYING =>
                        Log.d("FakeServiceController", "retrying")
                        errorTxt.setText("You may have poor 3G or Wifi connectivity")
                }
            , "London,uk").toIntent
        startService(fakeServiceControllerIntent)
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        getMenuInflater.inflate(R.menu.main, menu)
        true
    }
}