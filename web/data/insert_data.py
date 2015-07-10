#!/bin/env python
#-*- encoding: utf-8 -*-
#@author:skylmsgq
import httplib 
import time
import urllib
import json
import random


def query():
    for i in range(45, 200):
        
        device_id = '11-11-11-11-11-' + str(i)
        
        url = '/api/users/0615ed04443a0c0dc472bb95119f135bb/devices/' + device_id +'/data'
        
        datetime = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        longitude = str(random.uniform(121200000,121800000)/1000000)
        latitude = str(random.uniform(30900000,31300000)/1000000)
        tmp = random.uniform(10,30)
        e_temperature = str(tmp)
        s_temperature = str(tmp + 1)
        humidity = str(random.uniform(40,60))
        pressure = str(random.uniform(9990,10010)/1000)

        print date_time


        body = json.JSONEncoder().encode({"device_data": [{"time": datetime,"longitude": "121.51","latitude": "31.22", "e_temperature": "25","humidity": "55","pressure": "98", "s_temperature": "25" }]})
        
        # print datetime
        try:    

            conn = httplib.HTTPConnection("202.121.178.239")   
            url = '/api/users/0615ed04443a0c0dc472bb95119f135bb/devices/11-11-11-11-11-45/data'
            
            headers = {'X-user-id' : '0615ed04443a0c0dc472bb95119f135bb','X-auth-token' : '03adc0ed45c54ac0b424ab22e6703782'} 

            conn.request('POST',url = url, body = body, headers=headers) 
            
            # key = response.getheader("x-bdyy")      
            
            response = conn.getresponse()

            # print response.read()
        except Exception, e:   
            conn.close()
            print ('[HttpGET] get from server failed, errmsg=%s' % (e))
            return -1
query()