/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.alibaba.cloudapi.client;

import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import android.util.Base64;

import com.alibaba.cloudapi.client.constant.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by fred on 16/9/7.
 */
public class HttpUtil {
    static HttpUtil instance = new HttpUtil();
    static Object lock = new Object();
    OkHttpClient client;
    String httpSchema = HttpSchema.CLOUDAPI_HTTP;


    public static HttpUtil getInstance(){
        if(null == instance){
            synchronized (lock){
                if(null == instance){
                    instance = new HttpUtil();
                }
            }
        }

        return  instance;
    }

    private HttpUtil(){

        /**
         * 以HTTPS方式提交请求
         * 本SDK采取忽略证书的模式,目的是方便大家的调试
         * 为了安全起见,建议采取证书校验方式
         */
        if(AppConfiguration.IS_HTTPS){
            httpSchema = HttpSchema.CLOUDAPI_HTTPS;

            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };

            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            client = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory() , xtm).hostnameVerifier(DO_NOT_VERIFY).build();
        }
        else {
            client = new OkHttpClient();
        }



    }

    public void httpGet(String path , Map<String , String> pathParams , Map<String , String> queryParams , Map<String , String> headerParams , Callback callback)
    {

        Request request = this.buildHttpRequest(httpSchema , HttpMethod.CLOUDAPI_GET , AppConfiguration.APP_HOST , path , pathParams , queryParams , null  , null , ContentType.CLOUDAPI_CONTENT_TYPE_FORM , ContentType.CLOUDAPI_CONTENT_TYPE_JSON , headerParams);
        Call call = client.newCall(request);
        try {
            Response respnose = call.execute();
            callback.onResponse(call , respnose);
        }
        catch (IOException ex){
            callback.onFailure(call , ex);
        }
    }

    public void httpPostForm(String path , Map<String , String> pathParams , Map<String , String> queryParams , Map<String , String> formParams , Map<String , String> headerParams , Callback callback)
    {


        Request request = this.buildHttpRequest(httpSchema , HttpMethod.CLOUDAPI_POST , AppConfiguration.APP_HOST , path , pathParams , queryParams , formParams , null , ContentType.CLOUDAPI_CONTENT_TYPE_FORM , ContentType.CLOUDAPI_CONTENT_TYPE_JSON , headerParams);
        Call call = client.newCall(request);
        try {
            Response respnose = call.execute();
            callback.onResponse(call , respnose);
        }
        catch (IOException ex){
            callback.onFailure(call , ex);
        }
    }

    public void httpPostBytes(String path , Map<String , String> pathParams , Map<String , String> queryParams , byte[] body , Map<String , String> headerParams , Callback callback)
    {

        Request request = this.buildHttpRequest(httpSchema , HttpMethod.CLOUDAPI_POST , AppConfiguration.APP_HOST , path , pathParams , queryParams , null  , body , ContentType.CLOUDAPI_CONTENT_TYPE_STREAM , ContentType.CLOUDAPI_CONTENT_TYPE_JSON , headerParams);
        Call call = client.newCall(request);
        try {
            Response respnose = call.execute();
            callback.onResponse(call , respnose);
        }
        catch (IOException ex){
            callback.onFailure(call , ex);
        }
    }

    public void httpPutBytes(String path , Map<String , String> pathParams , Map<String , String> queryParams , byte[] body , Map<String , String> headerParams , Callback callback)
    {

        Request request = this.buildHttpRequest(httpSchema , HttpMethod.CLOUDAPI_PUT , AppConfiguration.APP_HOST , path , pathParams , queryParams , null  , body , ContentType.CLOUDAPI_CONTENT_TYPE_STREAM , ContentType.CLOUDAPI_CONTENT_TYPE_JSON , headerParams);
        Call call = client.newCall(request);
        try {
            Response respnose = call.execute();
            callback.onResponse(call , respnose);
        }
        catch (IOException ex){
            callback.onFailure(call , ex);
        }
    }

    public void httpDelete(String path , Map<String , String> pathParams , Map<String , String> queryParams , Map<String , String> headerParams , Callback callback)
    {

        Request request = this.buildHttpRequest(httpSchema , HttpMethod.CLOUDAPI_DELETE , AppConfiguration.APP_HOST , path , pathParams , queryParams , null  , null , ContentType.CLOUDAPI_CONTENT_TYPE_FORM , ContentType.CLOUDAPI_CONTENT_TYPE_JSON , headerParams);
        Call call = client.newCall(request);
        try {
            Response respnose = call.execute();
            callback.onResponse(call , respnose);
        }
        catch (IOException ex){
            callback.onFailure(call , ex);
        }
    }


    private Request buildHttpRequest(String protocol , String method , String host , String path , Map<String , String> pathParams , Map<String , String> queryParams ,  Map<String , String> formParams , byte[] body , String requestContentType , String acceptContentType , Map<String , String> headerParams){

        /**
         * 将pathParams中的value替换掉path中的动态参数
         * 比如 path=/v2/getUserInfo/[userId]，pathParams 字典中包含 key:userId , value:10000003
         * 替换后path会变成/v2/getUserInfo/10000003
         */
        String pathWithPathParameter = this.combinePathParam(path , pathParams);



        /**
         *  拼接URL
         *  HTTP + HOST + PATH(With pathparameter) + Query Parameter
         */
        StringBuilder url = new StringBuilder().append(protocol).append(host).append(pathWithPathParameter);

        if(null != queryParams && queryParams.size() > 0){
            url.append("?").append(HttpUtil.getInstance().buildParamString(queryParams));
        }

        if(null == headerParams){
            headerParams = new HashMap<String, String>();
        }


        Date current = new Date();
        //设置请求头中的时间戳
        headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_DATE , getHttpDateHeaderValue(current));

        //设置请求头中的时间戳，以timeIntervalSince1970的形式
        headerParams.put(SystemHeader.CLOUDAPI_X_CA_TIMESTAMP, String.valueOf(current.getTime()));

        //请求放重放Nonce,15分钟内保持唯一,建议使用UUID
        headerParams.put(SystemHeader.CLOUDAPI_X_CA_NONCE, UUID.randomUUID().toString());

        //设置请求头中的UserAgent
        headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_USER_AGENT, Constants.CLOUDAPI_USER_AGENT);

        //设置请求头中的主机地址
        headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_HOST , host);

        //设置请求头中的Api绑定的的AppKey
        headerParams.put(SystemHeader.CLOUDAPI_X_CA_KEY, AppConfiguration.APP_KEY);

        //设置签名版本号
        headerParams.put(SystemHeader.CLOUDAPI_X_CA_VERSION , Constants.CLOUDAPI_CA_VERSION_VALUE);

        //设置请求数据类型
        headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE , requestContentType);

        //设置应答数据类型
        headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_ACCEPT , acceptContentType);

        /**
         *  如果formParams不为空
         *  将Form中的内容拼接成字符串后使用UTF8编码序列化成Byte数组后加入到Request中去
         */
        RequestBody requestBody = null;
        if(null != formParams && formParams.size() > 0){
            requestBody = RequestBody.create(MediaType.parse(requestContentType) , buildParamString(formParams));
        }
        /**
         *  如果类型为byte数组的body不为空
         *  将body中的内容MD5算法加密后再采用BASE64方法Encode成字符串，放入HTTP头中
         *  做内容校验，避免内容在网络中被篡改
         */
        else if(null != body && body.length >0){
            requestBody = RequestBody.create(MediaType.parse(requestContentType) , body);
            headerParams.put(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_MD5 , this.base64AndMD5(body));
        }

        /**
         *  将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串用hmacSha256算法双向加密进行签名
         *  签名内容放到Http头中，用作服务器校验
         */
        headerParams.put(SystemHeader.CLOUDAPI_X_CA_SIGNATURE , SignUtil.sign(method , headerParams , pathWithPathParameter , queryParams , formParams));

        /**
         *  凑齐所有HTTP头之后，将头中的数据全部放入Request对象中
         *  Http头编码方式：先将字符串进行UTF-8编码，然后使用Iso-8859-1解码生成字符串
         */
        for(String key : headerParams.keySet()){
            String value = headerParams.get(key);
            if(null != value && value.length() > 0){
                byte[] temp = value.getBytes(Constants.CLOUDAPI_ENCODING);
                headerParams.put(key , new String(temp , Constants.CLOUDAPI_HEADER_ENCODING));
            }
        }
        Headers headers = Headers.of(headerParams);
        return new Request.Builder().method(method , requestBody).url(url.toString()).headers(headers).build();
    }

    private String buildParamString(Map<String , String> params){
        StringBuilder result = new StringBuilder();
        if(null != params && params.size() > 0){
            boolean isFirst = true;
            for(String key : params.keySet()){
                if(isFirst){
                    isFirst = false;
                }
                else{
                    result.append("&");
                }

                try {
                    result.append(key).append("=").append(URLEncoder.encode(params.get(key), Constants.CLOUDAPI_ENCODING.displayName()));
                }
                catch (UnsupportedEncodingException ex){
                    throw new RuntimeException(ex);
                }

            }
        }

        return result.toString();
    }

    private String combinePathParam(String path , Map<String , String> pathParams){
        if(pathParams == null){
            return path;
        }

        for(String key : pathParams.keySet()){
            path = path.replace("["+key+"]" , pathParams.get(key));
        }
        return path;
    }



    private String getHttpDateHeaderValue(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    /**
     * 先进行MD5摘要再进行Base64编码获取摘要字符串
     *
     * @return
     */
    private String base64AndMD5(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes can not be null");
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(bytes);
            final byte[] encodeBytes = Base64.encode(md.digest() , Base64.DEFAULT);
            byte[] encodeBytes2 = new byte[24];
            for(int i = 0 ; i < 24 ; i++){
                encodeBytes2[i] = encodeBytes[i];
            }
            return new String(encodeBytes2 , Constants.CLOUDAPI_ENCODING);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("unknown algorithm MD5");
        }
    }

}
