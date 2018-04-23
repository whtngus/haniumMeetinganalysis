# -*- coding: utf-8 -*-
"""
Created on Wed Mar 28 22:19:55 2018

@author: Suhyun
"""

f = open("8CM00054.txt", 'rt', encoding='utf-16')
name = ""
talk = []
speak = {}

while(True):
    data = f.readline() 
    if not data: break
    
    if("<u who=" in data or "<s n=" in data):
        if "who" in data:
            name = data[8:10]
            if not (data[8:10] in speak):
               speak[name] = [] 

        temp = data.split(">")
        #speak[name] = speak[name] + [v[:-3] for v in temp if "/s"  in v or ("pause"  in v and v.find("<") != 0)]
        for v in temp:
            if "/s"  in v:
                speak[name].append(v[:-3])
            
            if "pause"  in v and v.find("<") != 0:
                speak[name].append(v[:-7])
            


print(speak)