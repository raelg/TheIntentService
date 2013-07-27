package org.github.raelg

import android.content.Context
import android.content.SharedPreferences
import org.github.raelg.utils.PropertyUtils
import java.util.Properties

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 22/07/2013
 * Time: 23:02
 */
object App {

    def getContext = {
        context
    }

    private var context: Context = null
}

class App extends android.app.Application {

    override def onCreate() {
        super.onCreate

        val settings: SharedPreferences = getSharedPreferences(Identifiers.PREFERENCES, Context.MODE_PRIVATE)
        if (settings.getBoolean("isFirstRun", true)) {
            val properties: Properties = PropertyUtils.getProperties("project.properties")
            val baseUrl: String = properties.getProperty("baseUrl")
            val editor: SharedPreferences.Editor = settings.edit
            editor.putString(Identifiers.BASE_URL, baseUrl)
            editor.putBoolean("isFirstRun", false)
            editor.commit
        }
        App.context = this
    }
}