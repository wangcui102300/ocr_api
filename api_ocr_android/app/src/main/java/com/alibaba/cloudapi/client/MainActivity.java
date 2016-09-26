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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.cloudapi.client.R;
import com.alibaba.cloudapi.client.constant.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "client.cloudapi.alibaba.com.androidsdk.MESSAGE";
    public final static Thread worker = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




    public void sendHttpPostBytes(View view) {

        final Intent intent = new Intent(this, DisplayMessageActivity.class);

        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String getPath = "/rest/160601/ocr/ocr_idcard.json";

                String imgBase64 = bitmaptoString(BitmapFactory.decodeResource(getResources(), R.drawable.sfz));
                // 拼装请求body的json字符串
                JSONObject requestObj = new JSONObject();
                try {
                    JSONObject configObj = new JSONObject();
                    JSONObject obj = new JSONObject();
                    JSONArray inputArray = new JSONArray();
                    configObj.put("side", "face");
                    obj.put("image", getParam(50, imgBase64));
                    obj.put("configure", getParam(50, configObj.toString()));
                    inputArray.put(obj);
                    requestObj.put("inputs", inputArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String content =  requestObj.toString() ;

                Log.w("data",content);

                //----------
               // String content =  "Hi there ,this is 一个 string";

                HttpUtil.getInstance().httpPostBytes(getPath , null , null , content.getBytes(Constants.CLOUDAPI_ENCODING) , null , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        intent.putExtra(EXTRA_MESSAGE , e.getMessage());
                        startActivity(intent);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        intent.putExtra(EXTRA_MESSAGE , getResultString(response));
                        startActivity(intent);
                    }
                });

            }
        };
        new Thread(runnable).start();
    }

    /*
       * 获取参数的json对象
       */
    public   JSONObject getParam(int type, String dataValue) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dataType", type);
            obj.put("dataValue", dataValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
    public String bitmaptoString(Bitmap bitmap) {


        // 将Bitmap转换成字符串

        String string = null;

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bStream);

        byte[] bytes = bStream.toByteArray();
     Base64Encoder base=new Base64Encoder();
        string = base.encode(bytes); //

      //  string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return string;

    }

    private static String getResultString(Response response) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append("【服务器返回结果为】").append(Constants.CLOUDAPI_LF).append(Constants.CLOUDAPI_LF);
        result.append("ResultCode:").append(Constants.CLOUDAPI_LF).append(response.code()).append(Constants.CLOUDAPI_LF).append(Constants.CLOUDAPI_LF);
        if(response.code() != 200){
            result.append("错误原因：").append(response.header("X-Ca-Error-Message")).append(Constants.CLOUDAPI_LF).append(Constants.CLOUDAPI_LF);
        }

        result.append("ResultBody:").append(Constants.CLOUDAPI_LF).append(new String(response.body().bytes() , Constants.CLOUDAPI_ENCODING));

        return result.toString();
    }
}
