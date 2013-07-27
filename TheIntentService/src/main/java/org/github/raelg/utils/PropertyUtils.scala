package org.github.raelg.utils

import android.content.res.AssetManager
import org.github.raelg.App
import java.io.InputStream
import java.util.Properties

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 21/06/2013
 * Time: 11:30
 */
object PropertyUtils {

    def getProperties(fileName: String): Properties = {
        val assetManager = App.getContext.getResources.getAssets
        val properties = new Properties
        loadProperties(assetManager, fileName, properties)
        if (assetManager.list("").contains("local.properties")) {
            loadProperties(assetManager, "local.properties", properties)
        }
        properties
    }

    private def loadProperties(assetManager: AssetManager, fileName: String, properties: Properties) {
        val inputStream: InputStream = assetManager.open(fileName)
        properties.load(inputStream)
        inputStream.close()
    }
}