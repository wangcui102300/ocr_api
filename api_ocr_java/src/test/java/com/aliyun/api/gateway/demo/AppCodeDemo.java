package com.aliyun.api.gateway.demo;


import com.aliyun.api.gateway.demo.util.HttpsUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyou.wc on 2016/12/12.
 */
public class AppCodeDemo {
    /**
     * 将图片转换为base64编码后的字符串
     * @param path
     * @return
     * @throws Exception
     */
    public static String imgToBase64(String path) throws Exception{
        byte[] data = null;
        InputStream in = new FileInputStream(path);
        data = new byte[in.available()];
        in.read(data);
        in.close();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }
    public static void main(String[] args) throws Exception{
        String host = "https://dm-51.data.aliyun.com";
        String path = "/rest/160601/ocr/ocr_idcard.json";
        //测试图片位置
        String basePath = "C:\\Users\\cuiyo\\Desktop\\imgage\\12190.png";
        String base64Str =  imgToBase64(basePath);
        //System.out.println("\n" + base64Str + "\n");
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd7e94948385f570e3c139105
        headers.put("Authorization", "APPCODE 1086bc871a4d18b271e758add054fb");
        //headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);
        Map<String, String> querys = new HashMap<String, String>();
        String bodys ="{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":" + "\"" + base64Str + "\"" + "}," +
                "\"configure\":{\"dataType\":50,\"dataValue\":\"{ \\\"side\\\": \\\"face\\\" }\"}}]}";


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpsUtils.doPost(host, path, method, headers, querys, bodys);
            //System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
