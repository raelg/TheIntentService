package org.github.raelg.controllers.model

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 24/07/2013
 * Time: 12:14
 */
class Weather(val main: Main) extends JsonModel with Serializable {

    override def toString = {
        "[Weather, main = " + main.toString + "]"
    }
}
