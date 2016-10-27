package com.aliyun.api.gateway.demo.util;

import com.aliyun.api.gateway.demo.Client;
import com.aliyun.api.gateway.demo.Request;
import com.aliyun.api.gateway.demo.constant.Constants;
import com.aliyun.api.gateway.demo.constant.ContentType;
import com.aliyun.api.gateway.demo.constant.HttpHeader;
import com.aliyun.api.gateway.demo.constant.HttpSchema;
import com.aliyun.api.gateway.demo.enums.Method;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

/**
 * Created by cuiyou.wc on 2016/10/27.
 */
public class CommonUtil {
    private String host;

    private String url;

    private String app_key;

    private String app_secret;

    private List<String> custom_sign_prefix;

    private String image_path;

    public CommonUtil(String host,String url,String app_key,String app_secret,List<String> custom_sign_prefix,String image_path){
        this.host = host;
        this.url = url;
        this.app_key = app_key;
        this.app_secret = app_secret;
        this.custom_sign_prefix = custom_sign_prefix;
        this.image_path = image_path;
    }

    public String getHost(){
        return this.host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getApp_key(){
        return this.app_key;
    }

    public void setApp_key(String app_key){
        this.app_key = app_key;
    }

    public String getApp_secret(){
        return this.app_secret;
    }

    public void setApp_secret(String app_secret){
        this.app_secret = app_secret;
    }

    public List<String> getCustom_sign_prefix(){
        return this.custom_sign_prefix;
    }

    public void setCustom_sign_prefix(List<String> custom_sign_prefix){
        this.custom_sign_prefix= custom_sign_prefix;
    }

    public String getImage_path(){
        return this.image_path;
    }

    public void setImage_path(String image_path){
        this.image_path = image_path;
    }

    /**
     * 打印Response
     *
     * @param response
     * @throws java.io.IOException
     */
    private void print(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getStatusLine().getStatusCode()).append(Constants.LF);
        for (Header header : response.getAllHeaders()) {
            sb.append(MessageDigestUtil.iso88591ToUtf8(header.getValue())).append(Constants.LF);
        }
        sb.append(readStreamAsStr(response.getEntity().getContent())).append(Constants.LF);
        System.out.println("The response is:" + sb.toString());

        //get the response body
        /*StringBuilder sbBody = new StringBuilder();
        sbBody.append(readStreamAsStr(response.getEntity().getContent())).append(Constants.LF);
        System.out.println("The response body is:" + sbBody.toString());*/
    }

    /**
     * 将流转换为字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readStreamAsStr(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel dest = Channels.newChannel(bos);
        ReadableByteChannel src = Channels.newChannel(is);
        ByteBuffer bb = ByteBuffer.allocate(4096);

        while (src.read(bb) != -1) {
            bb.flip();
            dest.write(bb);
            bb.clear();
        }
        src.close();
        dest.close();

        return new String(bos.toByteArray(), Constants.ENCODING);
    }

    /**
     * 将图片转换为base64编码后的字符串
     * @param path
     * @return
     * @throws Exception
     */
    public String imgToBase64(String path) throws Exception{
        byte[] data = null;
        InputStream in = new FileInputStream(path);
        data = new byte[in.available()];
        in.read(data);
        in.close();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     * @param base64
     * @param path
     * @return
     * @throws Exception
     */
    public boolean base64ToImg(String base64, String path) throws Exception{
        if(base64 == null){return false;}
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(base64);
        for(int i = 0;i < bytes.length; ++i){
            if(bytes[i] < 0){//调整异常数据
                bytes[i] += 256;
            }
        }
        //生成jpeg图片
        OutputStream out = new FileOutputStream(path);
        out.write(bytes);
        out.flush();
        out.close();
        return true;
    }

    /**
     * 请求为字节
     * @param body
     * @throws Exception
     */
    public void HttpsPostBytes(String body) throws Exception {
        //请求URL
        String url = "/rest/160601/ocr/ocr_idcard.json";
        //Body内容
        byte[] bytesBody = body.getBytes(Constants.ENCODING);

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.POST_BYTES, HttpSchema.HTTPS + this.host + url, this.app_key, this.app_secret, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setSignHeaderPrefixList(this.custom_sign_prefix);
        request.setBytesBody(bytesBody);

        //调用服务端
        HttpResponse response = Client.execute(request);
        if(response != null){
            String strResult = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("The response result is:"+strResult);
        }
        //print(response);
    }

    /**
     * 请求为string格式
     * @param body
     * @throws Exception
     */
    public void HttpsPostString(String body) throws Exception {;
        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.POST_STRING, HttpSchema.HTTPS + this.host + this.url, this.app_key, this.app_secret, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setSignHeaderPrefixList(this.custom_sign_prefix);
        request.setStringBody(body);

        //调用服务端
        HttpResponse response = Client.execute(request);

        print(response);
    }

    /**
     * 发送请求获得response
     * @throws Exception
     */
    public void sendPostRequestWithBody()throws Exception{
        String base64Str = imgToBase64(this.image_path);
        //System.out.println(base64Str);
        int num = base64Str.length();
        System.out.println("The size after encode is:" + num);
        String body ="{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":" + "\"" + base64Str + "\"" + "}," +
                "\"configure\":{\"dataType\":50,\"dataValue\":\"{ \\\"side\\\": \\\"face\\\" }\"}}]}";
        HttpsPostString(body);
    }

    /**
     * 将json字符串转化为json表述形式
     * @param jsonStr
     * @return
     */
    public String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for(int i=0;i<jsonStr.length();i++){
            char c = jsonStr.charAt(i);
            if(level>0&&'\n'==jsonForMatStr.charAt(jsonForMatStr.length()-1)){
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c+"\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c+"\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }
    public static String getLevelStr(int level){
        StringBuffer levelStr = new StringBuffer();
        for(int levelI = 0;levelI<level ; levelI++){
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    /**
     * get filenames without sub-directory
     * @param path
     * @return
     */
    public static String[] getFileNames(String path){
        File file = new File(path);
        String[] fileName = file.list();
        return fileName;
    }

    /**
     * get filenames with sub-directory
     * @param path
     * @param fileName
     */
    public static void getAllFileName(String path,ArrayList<String> fileName){
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if(names != null){
            fileName.addAll(Arrays.asList(names));
        }
        for(File a:files){
            if(a.isDirectory()){
                getAllFileName(a.getAbsolutePath(),fileName);
            }
        }
    }


}
