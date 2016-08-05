#coding=utf8
import os,sys
reload(sys)
sys.path.append('/home/wangwang.ww/api_ocr_python/aliyun-api-gateway-demo-sign/')
from com.aliyun.api.gateway.sdk import client
from com.aliyun.api.gateway.sdk.http import request
from com.aliyun.api.gateway.sdk.common import constant
import base64
import json

def send_body(img_base64, config_str):
    param = {}
    pic = {}
    pic['dataType'] = 50
    pic['dataValue'] = img_base64
    param['image'] = pic 

    if config_str:
        conf = {}
        conf['dataType'] = 50
        conf['dataValue'] = config_str
        param['configure'] = conf

    inputs = { "inputs" : [param]}
    body = json.dumps(inputs)
    return body


if __name__ == "__main__":
    host = "http://dm-51.data.aliyun.com"
    url = "/rest/160601/ocr/ocr_idcard.json"

    cli = client.DefaultClient(app_key="23400262", app_secret="abd2f6ii88792669548bb84ba0672b24")

    req_post = request.Request(host=host, protocol=constant.HTTPS, url=url, method="POST", time_out=30000)

    config_str = '{\"side\":\"face\"}'
    image = '/home/wangwang.ww/api_ocr_python/aliyun-api-gateway-demo-sign/com/aliyun/api/gateway/sdk/13462.jpg'
    with open(image, 'rb') as infile:
        s = infile.read()
        img_base64 = base64.b64encode(s)
    body = send_body(img_base64, config_str)

    headers = {}
    headers[constant.HTTP_HEADER_ACCEPT]=constant.CONTENT_TYPE_JSON
    headers[constant.HTTP_HEADER_CONTENT_TYPE]=constant.CONTENT_TYPE_TEXT
    #headers[constant.HTTP_HEADER_CONTENT_TYPE]=constant.CONTENT_TYPE_STREAM

    #req_post.set_body(bytearray(body, encoding="utf8"))
    req_post.set_body(body)
    req_post.set_content_type(constant.CONTENT_TYPE_STREAM)
    req_post.set_headers(headers)
    print "The response data:\n",cli.execute(req_post)
