[load.py] #1.0
트레이닝 데이터에서 딕셔너리 형태로 필요한 값만 뽑아내는 전처리 작업 입니다.

----------------------------------------------

[load.py] #2.0
인풋이 바뀔 때 탈락되는 정보들을 위해 코드 수정. 
">"로 split 한 후, "<"의 인덱스 위치를 찾아 0 혹은 1이 아닐 경우, 앞의 문장을 append.