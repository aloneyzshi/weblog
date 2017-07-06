package com.netease.qa.log.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static HttpClient defaultHttpClient;

    public static final PoolingClientConnectionManager defaultConnManager =
            new PoolingClientConnectionManager();

    static {
        defaultConnManager.setMaxTotal(60);
        defaultConnManager.setDefaultMaxPerRoute(30);
        defaultHttpClient = new DefaultHttpClient(defaultConnManager);
        // 设置连接超时时间为5秒
        defaultHttpClient.getParams()
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // 设置读取的超时时间为10000秒。
        defaultHttpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        TrustManager easyTrustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates,
                    String authType) throws java.security.cert.CertificateException {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates,
                    String authType) throws java.security.cert.CertificateException {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        };

        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {easyTrustManager}, null);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }
        SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
        Scheme sch = new Scheme("https", 443, sf);
        defaultHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }

    public static void setHttpClient(HttpClient httpClient) {
        defaultHttpClient = httpClient;
    }

    public static Response httpGet(String url) {
        return httpGet(url, null);
    }

    /**
     * 执行 GET 请求.
     * @param url
     * @param headers
     * @return
     */
    public static Response httpGet(String url, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(url);

        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                httpGet.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        return httpExecute(httpGet);
    }

    public static Response httpPost(String url, Map<String, String> params) {
        return httpPost(url, params, null);
    }

    /**
     * 执行 SPOT 请求，包含请求参数、请求 Header.
     * @param url
     * @param params
     * @param headerMap
     * @return
     */
    public static Response httpPost(String url, Map<String, String> params,
            Map<String, String> headerMap) {

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Entry<String, String> e : params.entrySet()) {
                nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")));

        if (headerMap != null) {
            for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
                httpPost.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        return httpExecute(httpPost);
    }

    public static Response httpPost(String url, String body) {
        return httpPost(url, body, null);
    }

    /**
     * 执行 POST 请求.
     * @param url
     * @param body
     * @param headerMap
     * @return
     */
    public static Response httpPost(String url, String body, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(url);

        httpPost.setEntity(new StringEntity(body, Charset.forName("UTF-8")));

        if (headerMap != null) {
            for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
                httpPost.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        return httpExecute(httpPost);
    }

    private static Response httpExecute(HttpUriRequest request) {
        logger.debug("HttpRequestUri:" + request.getURI());

        Response response = new Response();
        HttpEntity entity = null;
        try {
            HttpResponse httpResponse = defaultHttpClient.execute(request);
            entity = httpResponse.getEntity();
            String content = EntityUtils.toString(entity, Charset.forName("UTF-8"));
            response.setCode(httpResponse.getStatusLine().getStatusCode()).setContent(content);
            logger.debug("HttpRequestRes:" + response);
        } catch (Exception e) {
            logger.error("HttpExecuteError: " + request.getURI());
            logger.error(e.getMessage(), e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return response;
    }

    public static class Response {
        private int code = -1;

        public int getCode() {
            return code;
        }

        public Response setCode(int code) {
            this.code = code;
            return this;
        }

        public String getContent() {
            return content;
        }

        public Response setContent(String content) {
            this.content = content;
            return this;
        }

        private String content;

        @Override
        public String toString() {
            return "HttpUtils.Response [code=" + code + ", content=" + content + "]";
        }

    }

}
