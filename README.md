## ARP
ARP프로토콜은 네트워크 레이어 2계층에 해당하는 데이터링크레이어에서 사용하는 프로토콜로써, 로컬 네트워크 상의 디바에스에서 해당 네트워크에 연결되어 있는 다른 디바이스들의
ip주소와 mac 주소를 매핑하여 arp 테이블만들어 로컬 네트워크 통신을 용이하게 만드는 프로토콜이다.

- ARP 프로토콜의 흐름
1. 디바이스는 로컬 네트워크에 연결되어있는 다른 디바이스의 mac 주소를 알아내기 ARP request를 해당 로컬 네트워크에 mac 주소를 알려는 ip주소를 기반으로 브로드캐스트를 한다.
2. ARP request를 받은 디바이스는 응답하여 ARP reply 패킷에 자신의 mac 주소를 담아서 request를 보낸 디바이스에게 전송한다.
3. ARP reply를 받은 디바이스는 해당 패킷에 들어있는 mac 주소를 자신의 ARP table에 업데이트한다.

- ARP를 이용한 출결관리 시스템


## 사용법
ARP 전송
사용할 네트워크 인터페이스를 숫자 입력으로 입력받아 해당 네트워크를 사용하고 있는 사용자들의 출결관리를 자동으로 한다.

ARP GUI

