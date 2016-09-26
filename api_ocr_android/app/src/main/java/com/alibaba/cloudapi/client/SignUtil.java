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

import android.util.Base64;

import com.alibaba.cloudapi.client.constant.Constants;
import com.alibaba.cloudapi.client.constant.HttpHeader;
import com.alibaba.cloudapi.client.constant.SystemHeader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by fred on 16/9/7.
 */
public class SignUtil {

    /**
     *  签名方法
     *  本方法将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串用hmacSha256算法双向加密进行签名
     */
    public static String sign(String method , Map<String, String> headersParams , String pathWithParameter , Map<String, String> queryParams , Map<String, String> formParam) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            byte[] keyBytes = AppConfiguration.APP_SECRET.getBytes(Constants.CLOUDAPI_ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256"));

            //将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串
            String signString = buildStringToSign(method , headersParams , pathWithParameter , queryParams , formParam);

            //对字符串进行hmacSha256加密，然后再进行BASE64编码
            byte[] signResult = hmacSha256.doFinal(signString.getBytes(Constants.CLOUDAPI_ENCODING));
            byte[] base64Bytes = Base64.encode(signResult , Base64.DEFAULT);
            return new String(base64Bytes , Constants.CLOUDAPI_ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串
     */
    private static String buildStringToSign(String method , Map<String, String> headerParams, String pathWithParameter, Map<String, String> queryParams ,  Map<String, String> formParams) {

        StringBuilder sb = new StringBuilder();
        sb.append(method).append(Constants.CLOUDAPI_LF);

        //如果有@"Accept"头，这个头需要参与签名
        if (headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_ACCEPT) != null) {
            sb.append(headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_ACCEPT));
        }
        sb.append(Constants.CLOUDAPI_LF);

        //如果有@"Content-MD5"头，这个头需要参与签名
        if (headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_MD5) != null) {
            sb.append(headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_MD5));
        }
        sb.append(Constants.CLOUDAPI_LF);

        //如果有@"Content-Type"头，这个头需要参与签名
        if (headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE) != null) {
            sb.append(headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE));
        }
        sb.append(Constants.CLOUDAPI_LF);

        //签名优先读取HTTP_CA_HEADER_DATE，因为通过浏览器过来的请求不允许自定义Date（会被浏览器认为是篡改攻击）
        if (headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_DATE) != null) {
            sb.append(headerParams.get(HttpHeader.CLOUDAPI_HTTP_HEADER_DATE));
        }
        sb.append(Constants.CLOUDAPI_LF);

        //将headers合成一个字符串
        sb.append(buildHeaders(headerParams));

        //将path、queryParam、formParam合成一个字符串
        sb.append(buildResource(pathWithParameter, queryParams , formParams));
        return sb.toString();
    }

    /**
     * 将path、queryParam、formParam合成一个字符串
     */
    private static String buildResource(String pathWithParameter, Map<String, String> queryParams ,  Map<String, String> formParams) {
        StringBuilder result = new StringBuilder();
        result.append(pathWithParameter);

        //使用TreeMap,默认按照字母排序
        TreeMap<String , String> parameter = new TreeMap<>();
        if(null!= queryParams && queryParams.size() > 0){
            parameter.putAll(queryParams);
        }

        if(null != formParams && formParams.size() > 0){
            parameter.putAll(formParams);
        }

        if(parameter.size() > 0) {
            result.append("?");
            boolean isFirst = true;
            for (String key : parameter.keySet()) {
                if (isFirst == false) {
                    result.append("&");
                } else {
                    isFirst = false;
                }
                result.append(key).append("=").append(parameter.get(key));
            }
        }
        return result.toString();
    }

    /**
     *  将headers合成一个字符串
     *  需要注意的是，HTTP头需要按照字母排序加入签名字符串
     *  同时所有加入签名的头的列表，需要用逗号分隔形成一个字符串，加入一个新HTTP头@"X-Ca-Signature-Headers"
     */
    private static String buildHeaders(Map<String, String> headers) {
        //使用TreeMap,默认按照字母排序
        Map<String, String> headersToSign = new TreeMap<String, String>();

        if (headers != null) {
            StringBuilder signHeadersStringBuilder = new StringBuilder();

            int flag = 0;
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey().startsWith(Constants.CLOUDAPI_CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
                    if (flag != 0) {
                        signHeadersStringBuilder.append(",");
                    }
                    flag++;
                    signHeadersStringBuilder.append(header.getKey());
                    headersToSign.put(header.getKey(), header.getValue());
                }
            }

            //同时所有加入签名的头的列表，需要用逗号分隔形成一个字符串，加入一个新HTTP头@"X-Ca-Signature-Headers"
            headers.put(SystemHeader.CLOUDAPI_X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : headersToSign.entrySet()) {
            sb.append(e.getKey()).append(':').append(e.getValue()).append(Constants.CLOUDAPI_LF);
        }
        return sb.toString();
    }

}
