## ARP
ARP프로토콜은 네트워크 레이어 2계층에 해당하는 데이터링크레이어에서 사용하는 프로토콜로써, 로컬 네트워크 상의 디바에스에서 해당 네트워크에 연결되어 있는 다른 디바이스들의
ip주소와 mac 주소를 매핑하여 arp 테이블만들어 로컬 네트워크 통신을 용이하게 만드는 프로토콜이다.

- ARP 프로토콜의 흐름
1. 디바이스는 로컬 네트워크에 연결되어있는 다른 디바이스의 mac 주소를 알아내기 ARP request를 해당 로컬 네트워크에 mac 주소를 알려는 ip주소를 기반으로 브로드캐스트를 한다.
2. ARP request를 받은 디바이스는 응답하여 ARP reply 패킷에 자신의 mac 주소를 담아서 request를 보낸 디바이스에게 전송한다.
3. ARP reply를 받은 디바이스는 해당 패킷에 들어있는 mac 주소를 자신의 ARP table에  업데이트한다.

## 사용법
ARP 출결 체크 프로그램

실행환경: VMware 가상머신 상에서 Ubuntu linux 18.04 LTS 무선 LAN 카드(Realtek RTL8188EU Wireless LAN 802.11n USB 2.0 Network Adapter)로 을 이용하여 로컬 네트워크 접속

1. gcc -o arpsystem arpsystem.c -lpcap -lpthread -lmysqlclient (컴파일)
2. sudo ./arpsystem (실행)
3. 사용하는 네트워크 인터페이스를 번호 입력 후 enter (네트워크인터페이스 선택)

ARP GUI
* Sign Up(데이터베이스에 추가)
Mac address에 xx:xx:xx:xx:xx:xx 포맷의 형태로 맥 주소 입력 및 이름 입력 후 send를 클릭하면 데이터베이스에 데이터 추가

* Browse
기본적으로 refresh 버튼을 누르면 데이터베이스 내의 모든 칼럼을 출력
추후 추가

* 주의 사항
-해당 로컬 네트워크에 접속 시 와이파이 연결옵션에서 MAC 주소 유형을 랜덤 MAC이 아닌 휴대전화 MAC으로 설정 해두어야 함
