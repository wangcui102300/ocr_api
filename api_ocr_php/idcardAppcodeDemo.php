<?php
    $file = "/home/***/idcard45.jpg"; // 文件路径 
    if($fp = fopen($file, "rb", 0)) { 
        $binary = fread($fp, filesize($file)); // 文件读取
        fclose($fp); 
        $base64 = base64_encode($binary); // 转码
        //echo $base64; // 显示base64码
    }   
    $host = "https://dm-51.data.aliyun.com";
    $path = "/rest/160601/ocr/ocr_idcard.json";
    $method = "POST";
    $appcode = "1086bc874f1a4d18b271e758add089fb";
    $headers = array();
    array_push($headers, "Authorization:APPCODE " . $appcode);
    //根据API的要求，定义相对应的Content-Type
    array_push($headers, "Content-Type".":"."application/json; charset=UTF-8");
    $querys = "";
    $bodys = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"$base64\"},\"configure\":{\"dataType\":50,\"dataValue\":\"{\\\"side\\\":\\\"face\\\"}\"}}]}";
    $url = $host . $path;

    $curl = curl_init();
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($curl, CURLOPT_FAILONERROR, false);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
    //curl_setopt($curl, CURLOPT_HEADER, true);
    curl_setopt($curl, CURLOPT_HEADER, false);
    if (1 == strpos("$".$host, "https://"))
    {
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
    }
    curl_setopt($curl, CURLOPT_POSTFIELDS, $bodys);
    var_dump(curl_exec($curl));
?>
