package org.github.raelg.controllers

import android.os.{ResultReceiver, Bundle, Handler}

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 25/07/2013
 * Time: 16:54
 */
object FunctionToResultReceiver {

    implicit def fnToResultReceiver(fn: (Int, Bundle) => Any)(implicit handler : Handler) : ResultReceiver = {
        new ResultReceiver(handler){
            override def onReceiveResult(resultCode: Int, resultData: Bundle) {
                fn(resultCode, resultData)
            }
        }
    }

}
