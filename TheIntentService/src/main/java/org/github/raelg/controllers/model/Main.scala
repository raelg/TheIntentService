package org.github.raelg.controllers.model

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 24/07/2013
 * Time: 12:14
 */
class Main(val temp: Double, val humidity: Int) extends JsonModel with Serializable {

    override def toString = {
        "temp: " + temp + ", humidity: " + humidity
    }

    lazy val tempInCelsius = temp - 273.15
}
