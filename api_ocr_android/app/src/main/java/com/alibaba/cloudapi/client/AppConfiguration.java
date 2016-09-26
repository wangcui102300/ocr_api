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

/**
 * Created by fred on 16/9/7.
 */
public class AppConfiguration {
    /**
     *  Api绑定的的AppKey，可以在“阿里云官网”->"API网关"->"应用管理"->"应用详情"查看
     */
    public static final String APP_KEY = " ";

    /**
     *  Api绑定的的AppSecret，用来做传输数据签名使用，可以在“阿里云官网”->"API网关"->"应用管理"->"应用详情"查看
     */
    public static final String APP_SECRET = " ";

    /**
     * 是否以HTTPS方式提交请求
     * 本SDK采取忽略证书的模式,目的是方便大家的调试
     * 为了安全起见,建议采取证书校验方式
     */
    public static final boolean IS_HTTPS = true;

    /**
     *  Api绑定的域名
     */
    //public static final String APP_HOST = "jisutianqi.market.alicloudapi.com";
    public static final String APP_HOST = "dm-51.data.aliyun.com";


    /**
     *  超时时间，单位为秒
     */
    public static final int APP_CONNECTION_TIMEOUT = 5;
}
