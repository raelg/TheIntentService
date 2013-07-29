package org.github.raelg.controllers

import com.google.gson.GsonBuilder
import org.apache.http.client.methods._
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import java.net.URI
import org.apache.http.entity.StringEntity
import org.github.raelg.controllers.model.JsonModel
import org.apache.http.protocol.HTTP
import com.google.inject.Inject
import com.google.inject.name.Named

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 23/07/2013
 * Time: 16:13
 */
sealed trait ContentType
case object JSON extends ContentType
case object TEXT extends ContentType
case object PNG extends ContentType


class RestController @Inject()(@Named("baseUrl") baseUrl: String) {

    private val toGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").excludeFieldsWithoutExposeAnnotation().create()

    private val httpClient = HttpClientFactory.getHttpClient

    def execute(request: HttpUriRequest) = {
        httpClient.execute(request)
    }

    /**
     * Reads httpResponse into a String which is always necessary to consume the content and release the httpResponse
     */
    def getResponse(request: HttpUriRequest): (Int, String) = {
        val httpResponse: HttpResponse = execute(request)
        (getStatusCode(httpResponse), getBodyAsString(httpResponse))
    }

    def getResponseWithHeaders(request: HttpUriRequest) : (Int, String, Map[String,String]) = {
        val httpResponse: HttpResponse = execute(request)
        (getStatusCode(httpResponse), getBodyAsString(httpResponse), getHeaders(httpResponse))
    }

    def getBodyAsString(httpResponse: HttpResponse) = {
        val httpEntity = httpResponse.getEntity
        EntityUtils.toString(httpEntity)
    }

    def getStatusCode(httpResponse: HttpResponse) = {
        httpResponse.getStatusLine.getStatusCode
    }

    def getHeaders(httpResponse: HttpResponse): Map[String, String] = {
        httpResponse.getAllHeaders.map(h => (h.getName, h.getValue)).toMap
    }

    def postRequest(url: String, requestBody: String, contentType: ContentType): HttpUriRequest = {
        def uri = getUrlWithFragment(url)

        val httpPost = new HttpPost(uri)
        val entity = new StringEntity(requestBody, HTTP.UTF_8)
        setContentType(entity, contentType)

        httpPost.setEntity(entity)

        httpPost
    }

    def postRequest(url: String, jsonModel: JsonModel): HttpUriRequest = {
        postRequest(url, toGson.toJson(jsonModel), JSON)
    }

    def deleteRequest(url: String): HttpUriRequest = {
        val uri = getUrlWithFragment(url)
        val httpDelete = new HttpDelete(uri)

        httpDelete
    }

    def setContentType(entity: StringEntity, contentType: ContentType) = {
        contentType match {
            case JSON => entity.setContentType("application/json")
            case TEXT => entity.setContentType("text/plain")
            case PNG => entity.setContentType("image/png")
        }
    }

    def getRequest(url: String): HttpUriRequest = {
        def uri = getUrlWithFragment(url)

        val httpGet = new HttpGet(uri)

        httpGet
    }

    def setAcceptType(request: HttpUriRequest, contentType: ContentType) = {
        contentType match {
            case JSON =>
                request.setHeader("Accept", "application/json")
            case TEXT =>
                request.setHeader("Accept", "text/plain")
            case PNG =>
                request.setHeader("Accept", "image/png")
        }
    }

    def getUrlWithFragment(url: String): URI = {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            new URI(baseUrl + url)
        } else {
            new URI(url)
        }
    }

    def putRequest(url: String, requestBody: String, contentType: ContentType): HttpUriRequest = {
        val uri = getUrlWithFragment(url)

        val httpPut = new HttpPut(uri)
        val entity = new StringEntity(requestBody, HTTP.UTF_8)
        setContentType(entity, contentType)
        httpPut.setEntity(entity)

        httpPut
    }

    def putRequest(url: String, jsonModel: JsonModel): HttpUriRequest = {
        putRequest(url, toGson.toJson(jsonModel), JSON)
    }

    def getHeader(httpResponse: HttpResponse, headerName: String): String = {
        if (httpResponse.containsHeader(headerName.toLowerCase)) {
            httpResponse.getLastHeader(headerName).getValue
        } else {
            httpResponse.getLastHeader(headerName).getValue
        }
    }

}
