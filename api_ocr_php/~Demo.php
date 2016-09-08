<?php
/*
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
include_once 'Util/Autoloader.php';

$demo = new Demo();
//$demo->doGet();
$demo->doPostString();

/**
*请求示例
*如一个完整的url为http://api.aaaa.com/createobject?key1=value&key2=value2
*$host为http://api.aaaa.com
*$path为/createobject
*query为key1=value&key2=value2
*/
class Demo
{
	private static $appKey = "23408262";
    private static $appSecret = "abd2f66288792669548bb84ba0672b24";
	//协议(http或https)://域名:端口，注意必须有http://或https://
    //private static $host = "http://test.alicloudapi.com";
    private static $host = "https://dm-51.data.aliyun.com";


	

	/**
	*method=GET请求示例
	*/
    public function doGet() {
		//域名后、query前的部分
		$path = "/get";
		$request = new HttpRequest($this::$host, $path, HttpMethod::GET, $this::$appKey, $this::$appSecret);

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_TEXT);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_TEXT);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		$request->setHeader("b-header2", "headervalue2");
		$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setQuery("b-query2", "queryvalue2");
		$request->setQuery("a-query1", "queryvalue1");

        //指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		$request->setSignHeader("a-header1");
		$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
		print_r($response);
	}

	/**
	*method=POST且是表单提交，请求示例
	*/
	public function doPostForm() {
		//域名后、query前的部分
		$path = "/postform";
		$request = new HttpRequest($this::$host, $path, HttpMethod::POST, $this::$appKey, $this::$appSecret);

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_FORM);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_JSON);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		$request->setHeader("b-header2", "headervalue2");
		$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setQuery("b-query2", "queryvalue2");
		$request->setQuery("a-query1", "queryvalue1");

		//注意：业务body部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setBody("b-body2", "bodyvalue2");
		$request->setBody("a-body1", "bodyvalue1");

        //指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		$request->setSignHeader("a-header1");
		$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
		print_r($response);
	}
    

    /**
	*get imge base64
	*/
    public function imgToBase64($filePath){
        $img_base64 = '';
        $img_info = getimagesize($filePath);
        $img_type = $img_info[2];
        $fp = fopen($filePath,'r');
        if($fp){
            $file_content = chunk_split(base64_encode(fread($fp,$filePath)));
            $img_base64 = 'data:image/'.$img_type.';base64,'.$file_content;
            fclose($fp);
        }
        return $img_base64;
    }

	/**
	*method=POST且是非表单提交，请求示例,ocr应用实例
	*/
	public function doPostString() {
		//域名后、query前的部分
		//$path = "/poststring";
        $path = "/rest/160601/ocr/ocr_idcard.json";
		$request = new HttpRequest($this::$host, $path, HttpMethod::POST, $this::$appKey, $this::$appSecret);
        $base64_img_string = $this->imgToBase64("C:\Users\cuiyou.wc\Desktop\imgage\5467.jpg");
		//传入内容是json格式的字符串
		//$bodyContent = "{\"inputs\": [{\"image\": {\"dataType\": 50,\"dataValue\": \"base64_image_string\"},\"configure\": {\"dataType\": 50,\"dataValue\": \"{\\\"side\\\":\\\"face\\\"}\"}}]}";
        
        $bodyContent = "{\"inputs\": [{\"image\": {\"dataType\": 50,\"dataValue\": \"".$base64_img_string."\"},\"configure\": {\"dataType\": 50,\"dataValue\": \"{\\\"side\\\":\\\"face\\\"}\"}}]}";

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_JSON);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_JSON);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		//$request->setHeader("b-header2", "headervalue2");
		//$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		//$request->setQuery("b-query2", "queryvalue2");
		//$request->setQuery("a-query1", "queryvalue1");

		//注意：业务body部分，不能设置key值，只能有value
		if (0 < strlen($bodyContent)) {
			$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_MD5, base64_encode(md5($bodyContent, true)));
			$request->setBodyString($bodyContent);
		}

		//指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		//$request->setSignHeader("a-header1");
		//$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
        echo $response;
		print_r($response);
	}


	/**
	*method=POST且是非表单提交，请求示例
	*/
	public function doPostStream() {
		//域名后、query前的部分
		$path = "/poststream";
		$request = new HttpRequest($this::$host, $path, HttpMethod::POST, $this::$appKey, $this::$appSecret);
		//Stream的内容
		$bytes = array();
		//传入内容是json格式的字符串
		$bodyContent = "{\"inputs\": [{\"image\": {\"dataType\": 50,\"dataValue\": \"base64_image_string(此行)\"},\"configure\": {\"dataType\": 50,\"dataValue\": \"{\"side\":\"face(#此行此行)\"}\"}}]}";

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_STREAM);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_JSON);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		$request->setHeader("b-header2", "headervalue2");
		$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setQuery("b-query2", "queryvalue2");
		$request->setQuery("a-query1", "queryvalue1");

		//注意：业务body部分，不能设置key值，只能有value
		foreach($bytes as $byte) { 
            $bodyContent .= chr($byte); 
        }
		if (0 < strlen($bodyContent)) {
			$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_MD5, base64_encode(md5($bodyContent, true)));
			$request->setBodyStream($bodyContent);
		}

		//指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		$request->setSignHeader("a-header1");
		$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
		print_r($response);
	}

	//method=PUT方式和method=POST基本类似，这里不再举例

	/**
	*method=DELETE请求示例
	*/
    public function doDelete() {
		//域名后、query前的部分
		$path = "/delete";
		$request = new HttpRequest($this::$host, $path, HttpMethod::DELETE, $this::$appKey, $this::$appSecret);

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_TEXT);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_TEXT);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		$request->setHeader("b-header2", "headervalue2");
		$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setQuery("b-query2", "queryvalue2");
		$request->setQuery("a-query1", "queryvalue1");

        //指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		$request->setSignHeader("a-header1");
		$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
		print_r($response);
	}


	/**
	*method=HEAD请求示例
	*/
    public function doHead() {
		//域名后、query前的部分
		$path = "/head";
		$request = new HttpRequest($this::$host, $path, HttpMethod::HEAD, $this::$appKey, $this::$appSecret);

        //设定Content-Type，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_CONTENT_TYPE, ContentType::CONTENT_TYPE_TEXT);
		
        //设定Accept，根据服务器端接受的值来设置
		$request->setHeader(HttpHeader::HTTP_HEADER_ACCEPT, ContentType::CONTENT_TYPE_TEXT);
        //如果是调用测试环境请设置
		//$request->setHeader(SystemHeader::X_CA_STAG, "TEST");


        //注意：业务header部分，如果没有则无此行(如果有中文，请做Utf8ToIso88591处理)
		//mb_convert_encoding("headervalue2中文", "ISO-8859-1", "UTF-8");
		$request->setHeader("b-header2", "headervalue2");
		$request->setHeader("a-header1", "headervalue1");

        //注意：业务query部分，如果没有则无此行；请不要、不要、不要做UrlEncode处理
		$request->setQuery("b-query2", "queryvalue2");
		$request->setQuery("a-query1", "queryvalue1");

        //指定参与签名的header
		$request->setSignHeader(SystemHeader::X_CA_TIMESTAMP);
		$request->setSignHeader("a-header1");
		$request->setSignHeader("b-header2");

		$response = HttpClient::execute($request);
		print_r($response);
	}
}