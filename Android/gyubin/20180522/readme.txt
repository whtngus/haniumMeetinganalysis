conference_pre.xml

- 회의 주제 , 회의실 text=>hint

Conference_pre.class

- 회의 주제 Conference.class , SaveConference.class 로 데이터 전달

Conference

- 음성파일 저장할때 음성파일이름에 회의주제 추가해서 저장
(음성파일은 Conference에서 SaveConference로 넘어갈때 저장됩니다)

SaveConference.class


- 저장 버튼 누르기 전 회의주제 노출

CalendarView

-  캘린더 및 날짜가져오기(날짜 클릭시 회의록도 볼 수있게?)