## ARP
ARP(Address Resolution Protocol)는 IP 주소를 기반으로 MAC 주소를 알아내는 통신 프로토콜이다. 이더넷 네트워크 상에서 목적지 IP 주소를 알지만 MAC주소를 모르는 경우 IP 주소를 기반으로 해당 로컬 네트워크에 ARP Request 패킷을 브로드캐스트 (broadcast)를 한다. 그리고 해당 IP 주소를 가진 디바이스가 이에 응답하여 자신 디바이스 MAC 주소를 ARP Reply 패킷을 만들어 브로드캐스트를 한 주소로 패킷을 전달하여 자신의 MAC 주소를 알려준다

- ARP 프로토콜의 흐름
1. 디바이스는 로컬 네트워크에 연결되어있는 다른 디바이스의 mac 주소를 알아내기 ARP request를 해당 로컬 네트워크에 mac 주소를 알려는 ip주소를 기반으로 브로드캐스트를 한다.
2. ARP request를 받은 디바이스는 응답하여 ARP reply 패킷에 자신의 mac 주소를 담아서 request를 보낸 디바이스에게 전송한다.
3. ARP reply를 받은 디바이스는 해당 패킷에 들어있는 mac 주소를 추출하여 알아내려는 목적지 IP 주소를 가진 MAC주소를 알아낸다.

## 사용법
ARP 출결 체크 프로그램

실행환경: VMware 가상머신 상에서 Ubuntu linux 18.04 LTS 무선 LAN 카드(Realtek RTL8188EU Wireless LAN 802.11n USB 2.0 Network Adapter)로 을 이용하여 로컬 네트워크 접속

1. gcc -o ARPsystem ARPsystem.c -lpcap -lpthread -lmysqlclient (컴파일)
2. sudo ./ARPsystem (실행)
3. 사용하는 네트워크 인터페이스를 번호 입력 후 enter (네트워크인터페이스 선택)
4. 선택된 네트워크에 ARP Request 브로드캐스트
5. ARP Reply를 받으면 해당 Reply에서 MAC주소 추출 후 데이터베이스 조회 후 목록에 존재하면 해당 MAC주소 데이터를 업데이트

ARP GUI
* Admin 탭 (데이터베이스에 데이터 추가 및 삭제)
  Mac address에 xx:xx:xx:xx:xx:xx 포맷의 형태로 맥 주소 입력 및 이름 입력 후 send를 클릭하면 데이터베이스에 데이터 추가
  추가하는 인터페이스 우측에 테이블에서 삭제할 데이터를 선택 후 Delete 클릭

* Browse 탭
 기본적으로 refresh 버튼을 누르면 데이터베이스 내의 모든 칼럼을 출력
 Search option에는 콤보박스로 선택 없음, 출근, 등록, 지각, 결근이 있으며 선택하게 되면 해당하는 데이터만을 조회 후 출력


# 유의 사항
출결 체크를 하기 위하여 로컬 네트워크에 접속 시 데이터베이스에 등록된 MAC주소여야 하므로 와이파이 연결옵션 - 고급 - MAC 주소 유형을 랜덤 MAC이 아닌 휴대전화 MAC으로 설정 해두는것을 추천한다.
