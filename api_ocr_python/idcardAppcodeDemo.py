import urllib, urllib2, sys
import ssl
import base64

host = 'https://dm-51.data.aliyun.com'
path = '/rest/160601/ocr/ocr_idcard.json'
method = 'POST'
appcode = '1086bc874f1a4d18b271e758add256fb'
querys = ''
bodys = {}
url = host + path

image = '/home/**/idcard45.jpg'
with open(image, 'rb') as infile:
    s = infile.read()
    img_base64 = base64.b64encode(s)

bodys[''] = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"%s\"},\"configure\":{\"dataType\":50,\"dataValue\":\"{\\\"side\\\":\\\"face\\\"}\"}}]}" % img_base64
post_data = bodys['']
request = urllib2.Request(url, post_data)
request.add_header('Authorization', 'APPCODE ' + appcode)
request.add_header('Content-Type', 'application/json; charset=UTF-8')
ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE
response = urllib2.urlopen(request, context=ctx)
content = response.read()
if (content):
    print "The response content is:\n",content
