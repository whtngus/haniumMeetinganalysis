# -*- coding: utf-8 -*-
"""
Created on Wed Mar 28 22:19:55 2018

@author: Suhyun
"""

f = open("8CM00054(input1).txt", 'rt', encoding='utf-16')
name = ""
talk = []
speak = {}
not_person = ['al', 'ot']

while(True):
    data = f.readline() 
    if not data: break
    
    if("<u who=" in data or "<s n=" in data):
        # 발화자 구분
        if "who" in data:
            name = data[8:10]
            if name in not_person:
                continue
            if not (name in speak):
               speak[name] = [] 
        
        # 대화 추출
        temp = data.split(">")
        s = ""
        for v in temp:
            idx = v.find('<')
            if idx < 2: continue
            speak[name].append(v[:idx])


for who, s in speak.items():
    print("발화자: ",who)
    print(s)