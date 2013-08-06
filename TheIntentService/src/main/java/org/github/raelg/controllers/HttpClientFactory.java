package org.github.raelg.controllers;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 22/05/2013
 * Time: 16:08
 */
public class HttpClientFactory {

    private static HttpClient customHttpClient;

    private HttpClientFactory() {

    }

    public static synchronized HttpClient getHttpClient() {
        if (customHttpClient == null) {

            HttpParams httpParams = getTimeoutParameters();
            ClientConnectionManager cm = setupConnectionManager(httpParams);
            customHttpClient = new DefaultHttpClient(cm, httpParams);
        }

        return customHttpClient;
    }

    private static synchronized ClientConnectionManager setupConnectionManager(HttpParams httpParams) {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            return new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized HttpParams getTimeoutParameters() {
        HttpParams httpParameters = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParameters, 1000);
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, 1000);
        return httpParameters;
    }

}
