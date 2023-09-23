# teamBingo

수정사항 - 
빙고판의 크기를 생성자로 지정할 수 있도록 변경

수정 계획
n명의 참가자를 static serverList로 관리하여
1번 참가자가 start를 누르기 전 까지 계속 받도록 변경

빙고 start 후

빙고 setting 시작
다른 참가자가 빙고 세팅을 완료하기 전 까지 wait()로 
setting 완료한 참가자들 대기

boolean issettingover = true 를 세는 count 변수를 통해
setting 완료시 notifiall로 다 깨워서 빙고 맞추기 시작

custom exception
notyourturn exception을 만들어서 상대방 차례에 대기

등등

