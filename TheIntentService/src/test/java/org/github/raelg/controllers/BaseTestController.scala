package org.github.raelg.controllers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import org.apache.commons.io.FileUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.params.BasicHttpParams
import org.github.raelg.Identifiers
import org.github.raelg.R
import org.github.raelg.controllers.model.JsonModel
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 05/04/2013
 * Time: 13:40
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Bundle], classOf[Base64], classOf[Context]))
@PowerMockIgnore(Array("javax.crypto.*"))
abstract class BaseTestController {

    @Mock protected var mockController: RestController = null
    @Mock protected var mockIntent: Intent = null
    @Mock protected var mockHttpResponse: HttpResponse = null
    @Mock protected var mockHttpRequest: HttpUriRequest = null
    @Mock protected var mockBundler: Bundler = null
    @Mock protected var mockSharedPreferences: SharedPreferences = null

    protected var mockContext: Context = null
    protected var mockBundle: Bundle = null

    @Before def setUp {
        MockitoAnnotations.initMocks(this)
        when(mockController.postRequest(anyString, any(classOf[JsonModel]))).thenCallRealMethod
        when(mockController.putRequest(anyString, any(classOf[JsonModel]))).thenCallRealMethod
        when(mockController.postRequest(anyString, anyString, any(classOf[ContentType]))).thenReturn(mockHttpRequest)
        when(mockController.putRequest(anyString, anyString, any(classOf[ContentType]))).thenReturn(mockHttpRequest)
        when(mockController.getRequest(anyString)).thenReturn(mockHttpRequest)
        when(mockController.deleteRequest(anyString)).thenReturn(mockHttpRequest)
        when(mockController.execute(mockHttpRequest)).thenReturn(mockHttpResponse)
        when(mockHttpRequest.getParams).thenReturn(new BasicHttpParams)
        when(mockController.getBodyAsString(mockHttpResponse)).thenReturn("")
        mockBundle = PowerMockito.mock(classOf[Bundle])
        when(mockBundler.createBundle).thenReturn(mockBundle)
        mockContext = PowerMockito.mock(classOf[Context])
        when(mockContext.getString(R.string.err_server)).thenReturn("server error message")
        when(mockContext.getSharedPreferences(Identifiers.PREFERENCES, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences)
    }

    def getResource(fileName: String): String = {
        val file: File = new File(this.getClass.getResource("/" + fileName).toURI)
        FileUtils.readFileToString(file, "UTF-8")
    }


}