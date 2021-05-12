#include <stdio.h>
#include <sys/socket.h>
#include <net/if.h>
#include <net/if_arp.h>
#include <linux/if_ether.h>
#include <linux/if_packet.h> /*struct sockaddr_ll*/
#include <sys/ioctl.h>
#include <strings.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <signal.h>
#include <pcap.h>
#include <pthread.h>
#include <unistd.h>
#include <mysql/mysql.h>
#include <time.h>

typedef unsigned char uchar;
typedef unsigned short ushort;

/* ethernet frame header */
struct eth_hdr
{
    uchar h_dest[6];	// destination ether addr
    uchar h_source[6];	// source ether addr
    ushort h_proto;		// packet type ID field
}__attribute__((packed));
static const int ETHERNET_SIZE = sizeof(struct eth_hdr);

/* ARP Header */
struct arp_hdr
{
    ushort ar_hrd;  	//H/W type : ethernet
    ushort ar_pro;  	//Protocol
    uchar ar_hln;   	//H/W size
    uchar ar_pln;   	//Protocal size
    ushort ar_op;   	//Opcode replay
    uchar ar_sha[6];	//sender MAC
    uchar ar_sip[4];	//sender IP
    uchar ar_tha[6];	//target MAC
    uchar ar_tip[4];	//target IP
}__attribute__((packed));
static const int ARP_SIZE = sizeof(struct arp_hdr);

/*--------------------------------------------*/
static uchar g_buf[sizeof(struct eth_hdr)+sizeof(struct arp_hdr)];
static const char * g_source_ip = NULL;
static char * g_interface = NULL;
static int g_sock = -1;
static pcap_if_t* dev;
static pcap_if_t* alldevs;
/*--------------------------------------------*/
static MYSQL* conn;
static MYSQL_RES *res;
static MYSQL_ROW row;
static int query_stat;
/*--------------------------------------------*/
//get interface mac addr.
// ex) interface2mac("eth0", buf)
// return : 1 success
//	  	  : 0 fail
int interface2mac(const char* interface, uchar * mac)  
{
    int fd = socket(PF_INET,SOCK_STREAM, 0);  //make socket, socket(ipv4,tcp/ip, 0)
    if(fd == -1)
    {
		printf("socket"); //error message print
		return 0;
    }

    struct ifreq iflist;
    bzero(&iflist, sizeof(iflist));
    strncpy(iflist.ifr_name, interface, sizeof(iflist.ifr_name));
    if(ioctl(fd, SIOCGIFHWADDR, &iflist) == -1)
    {
		printf("ioctl failed");
		return 0;
    }

    struct sockaddr* sa = &iflist.ifr_hwaddr;
    memcpy(mac, sa->sa_data, 6);

    close(fd);

	return 1;
}

// get mac address to arp cash
// exam) get_arp_to_arpcash(ip)
// return : 1 success
//	  	  : 0 failure
int get_arp_to_arpcash(unsigned long ip)
{
    int fd = 0;
    if((fd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) //if(make socket failure)
	{
		printf("socket making error");
		return 0;
	}

    struct sockaddr_in sin;
    bzero(&sin, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = ip;
    sin.sin_port = htons(67);

    int i = sendto(fd, NULL, 0, 0, (struct sockaddr *)&sin, sizeof(sin)); //sendto(socket discriptor, data to send, data size, flags option, target, addr, target addr size)
    close(fd);

    return (i == 0);
}

//get mac address from ip, interface
// return 1 : success
//	  0 : failure
int arp_cash_lookup(const char* interface, unsigned long ip, uchar * mac)
{
    int sock = 0;
    struct arpreq ar;
    struct sockaddr_in* sin = 0;

    bzero(&ar, sizeof(ar));

    strncpy(ar.arp_dev, interface, sizeof(ar.arp_dev));
    sin = (struct  sockaddr_in *)&ar.arp_pa;
    sin->sin_family = AF_INET;
    sin->sin_addr.s_addr = ip;

    if(sock = socket(AF_INET, SOCK_DGRAM, 0) == -1)
		return 0;

    if(ioctl(sock, SIOCGARP, (caddr_t)&ar) == -1)
    {
		close(sock);
		return 0;
    }

    close(sock);
    memcpy(mac, ar.arp_ha.sa_data, 6);

    return 1;
}

//string to mac address
// exam) "01:02:03:0d:0e:0f"
// return : 1 success
//	    	0 faillure
int str2mac(const  char * str_mac, uchar * mac) 
{
    int ret = sscanf(str_mac, "%hhx:%hhx:%hhx:%hhx:%hhx:%hhx",
		    &mac[0], &mac[1],&mac[2],&mac[3],&mac[4],&mac[5]);

    return ret;
 }

//string to ip
// ex) "192.168.0.1" -> "\xc0\xa8\x00\x01"
// return : 1 success
//	  : 0 failure
int str2ip(const char * str_ip, uchar * ip)
{
    int ret = sscanf(str_ip, "%hhu.%hhu.%hhu.%hhu",
		    &ip[0],&ip[1],&ip[2],&ip[3]);
	return ret;
}

//convert ip to mac address
// return : 1 success
//	     	0 failure
int ip2mac(const char * intf, const char * str_ip, uchar* mac)
{
    int i = 0;
    unsigned int ip = 0;
    if(str2ip(str_ip, (uchar *)&ip) == 0)
	{
		printf("error check: ip2mac->str2mac");
		return 0;
	}
    do
    {
		if(arp_cash_lookup(intf, ip, mac) == 1)
	    	return 1;
	
		get_arp_to_arpcash(ip);

		sleep(1);
    }while(i++ < 3);

	printf("error check: ip2mac");
    return 0;
}

//init arp apcket
void init_packet(struct eth_hdr * e, struct arp_hdr * a)
{
    bzero(e, sizeof(*e));
    memset(e->h_dest, 0xff, sizeof(e->h_dest));
    e->h_proto = htons(0x0806); // ARP protocol 

    bzero(a, sizeof(*a));
    a->ar_hrd = htons(0x0001); //Ethernet 10/100 Mbps
    a->ar_pro = htons(0x0800); //IP protocol
    a->ar_hln = 6;	       //hardware len
    a->ar_pln = 4;	       //protocol len

    a->ar_op = htons(0x0001); //1 : request, 2 : reply

}

//create rawsocket
// ex) rawsocket("eth0")
// return -1 : failure
//	   0 : success
int rawsocket(const char * interface)
{
    int fd = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL));
    if(fd == -1)
    {
		perror("socket create :");
		return -1;
    }

    struct ifreq ifr;
    bzero(&ifr, sizeof(ifr));

    //select network interface ex) "eth0"
    strcpy((char *)ifr.ifr_name, interface);
    if(ioctl(fd, SIOCGIFINDEX, &ifr) == -1)
    {
		perror("error getting interface index\n");
		close(fd);
		return -1;
    }

    struct sockaddr_ll sll;

    bzero(&sll, sizeof(sll));
    sll.sll_family = AF_PACKET;
    sll.sll_ifindex = ifr.ifr_ifindex;
    sll.sll_protocol = htons(ETH_P_ALL);

    if(bind(fd, (struct sockaddr*)&sll, sizeof(sll)) == -1)
    {
		perror("Error binding raw socket to interface\n");
		close(fd);
		return -1;
    }
    
    return fd;
}

void sig_cleanup(int signo)
{
    printf("clean up\n");

    struct eth_hdr * ether = (struct eth_hdr *)g_buf;
    struct arp_hdr * arp = (struct arp_hdr *)(g_buf+ETHERNET_SIZE);

    uchar source_mac[6] = {0, };
    if(g_sock != -1 && ip2mac(g_interface, g_source_ip, source_mac) == 1)
    {

	//set source mac to original mac address
	memcpy(ether->h_source, source_mac, 6);
	memcpy(arp->ar_sha, source_mac, 6);

	int i  = 0;
	for(i = 0; i < 2; ++i)
	{
	    write(g_sock, g_buf, ETHERNET_SIZE+ARP_SIZE);
	    sleep(1);
	}

		close(g_sock);
    }
    exit(0);
}

char* getMyIP(char* interface)
{
	int fd;
	struct ifreq ifr;
	printf("getmyip");
	fd = socket(AF_INET, SOCK_DGRAM, 0);

	strncpy(ifr.ifr_name, interface, IFNAMSIZ-1);
	ioctl(fd, SIOCGIFADDR, &ifr);
	close(fd);
	
	return inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr);
}

void database_update(char* mac_address)
{
	int hour, min;
	/*------------------------------------------*/
	time_t current_time;
	time(&current_time);
	struct tm *t = localtime(&current_time);

	hour = t->tm_hour; 
	min =  t->tm_min;
	/*------------------------------------------*/
	char query_buffer[2048];
	
	sprintf(query_buffer, "SELECT * FROM ARPUserTable WHERE mac_address = '%s'", mac_address);
	printf("query making success\n");
	if(mysql_query(conn, query_buffer))
	{
		printf("fail to sending query");
		exit(1);
	}
	printf("query ok\n");
	res = mysql_store_result(conn);

	if((row = mysql_fetch_row(res)) == NULL)
	{
		printf("no return\n");
		return;
	}
	if(strcmp(row[2], "Ab") == 0)
	{
		//출근처리
		if(hour < 9)
		{
			printf("check attendance\n");
			sprintf(query_buffer, "UPDATE ARPUserTable SET status = 'A'  WHERE mac_address = '%s'", mac_address);
			if(mysql_query(conn, query_buffer))
			{
				printf("update fail");
				exit(1);
			}	
		}
	
		//지각처리
		else
		{
			printf("check come late\n");
			sprintf(query_buffer, "UPDATE ARPUserTable SET status = 'T'");
			if(mysql_query(conn, query_buffer))
			{
				printf("update fail");
				exit(1);
			}		
		}
	}
	sprintf(query_buffer, "UPDATE ARPUserTable SET last_check = CURRENT_TIMESTAMP  WHERE mac_address = '%s'", mac_address);
	if(mysql_query(conn, query_buffer))
	{
		printf("update fail");
		exit(1);
	}

	printf("update compelete\n");

	return;
}

void check_arp_header(const unsigned char *pkt_data)
{
    char mac_addr[20];
    struct arp_hdr *arpop;
    arpop = (struct arp_hdr *)(pkt_data+14);
    unsigned short Arpopcode = ntohs(arpop->ar_op);
	
	//only capturing reply packet
    if(Arpopcode == 0x0002)
    {
		sprintf(mac_addr ,"%02x:%02x:%02x:%02x:%02x:%02x", arpop->ar_sha[0]
						   	, arpop->ar_sha[1]
						    , arpop->ar_sha[2]
						    , arpop->ar_sha[3]
						    , arpop->ar_sha[4]
						    , arpop->ar_sha[5]);
		printf("\n%s\n", mac_addr);
		database_update(mac_addr);	
    }

}

void* Sniff()
{
   int inum, res, i = 0;
   struct pcap_pkthdr *header;
   const unsigned char *pkt_data;
   pcap_if_t* d;
   pcap_t *adhandle;
   char errbuf[PCAP_ERRBUF_SIZE];
   char packet_filter[] = "arp";
   struct bpf_program fcode;

   if((adhandle = pcap_open_live(dev->name, 42, 0, 1000, errbuf))== NULL)
   {
       printf("\nUnable to open the adapter. %s is not supported by WinPcap", d->name);
       exit(1);
   }

    if(pcap_compile(adhandle, &fcode, packet_filter,  1, 0) < 0)
    {
		printf("\nUnable to compile the packet filter.  Check the syntax.\n");
		exit(1);
    }

    if(pcap_setfilter(adhandle, &fcode) < 0)
    {
		printf("\nError setting the filter.\n");
		exit(1);
    }
    
    pcap_freealldevs(alldevs);

    while(res = pcap_next_ex(adhandle, &header, &pkt_data) >= 0)
    {
		if(res == 0)
			continue;

		check_arp_header(pkt_data);
    }
}
/*-------------------------------------------------------------------------------*/
//ip addr 192.168.137.xxx subnetmask 255.255.255.0
//au -e eth0
void* Request()
{
    char target_ip[20];  
    char * subnetmask_ip = "192.168.137.";
	char  myIP[16];
    int i, j = 0;
    int inum, res;
    char errbuf[PCAP_ERRBUF_SIZE];

    g_interface = malloc(sizeof(char)*20);
    sprintf(g_interface, "%s", dev->name);
	sprintf(myIP, "%s", getMyIP(dev->name));
	
	//printf("%s" myIP);
    //strat while loop
    while(1)
    {
		//make ip addr (192.168.137. 1 ~ 192.168.137.254)
		for(i = 1; i < 255; i++)
		{
	    	sprintf(target_ip, "%s%d", subnetmask_ip, i);

			if(strcmp(target_ip, myIP) == 0)
				continue;

	    	g_source_ip = target_ip;

	    	//printf("ARP sending to %s\n", g_source_ip);

	    	bzero(g_buf, sizeof(g_buf));

	    	struct eth_hdr * ether  = (struct eth_hdr *)g_buf;
	    	struct arp_hdr * arp  =  (struct arp_hdr *)(g_buf+ETHERNET_SIZE);

	    	init_packet(ether, arp);
	    
	    	if(interface2mac(g_interface, ether->h_source) == 0 ||
			   ip2mac(g_interface, target_ip, ether->h_dest) == 0 ||
			   str2ip(g_source_ip, arp->ar_sip) == 0 ||
			   str2ip(target_ip, arp->ar_tip) == 0) 
	    	{	
				printf("\npacket making error \n");
	      		exit(1);
	    	}

			//ether->h_source == my mac
			//ether->h_dest == "\xff\xff\xff\xff\xff\xff"
			memset(ether->h_dest, 0xff, 6);

			//arp->ar_sha  == my mac
			memcpy(arp->ar_sha, ether->h_source, sizeof(arp->ar_sha));

			//arp->ar_sip == my ip
			//arp->ar_tip == "\x00\x00\x00\x00\x00x\00x\00"
			memset(arp->ar_tha, 0, 6);
			//arp->ar_tip == target ip
	
			signal(SIGINT, &sig_cleanup);

	    	//create rawsocket
	    	g_sock =  rawsocket(g_interface);

	    	if(g_sock == -1)
	    	{
				printf("error");
				exit(1);	
	    	}

	    	sleep(2);
	    	close(g_sock);	   
		}//end for
    }//end while
    free(g_interface);
    
    close(g_sock);
}


int main(){
	/*------------------------mysql  변수 및 구조체---------------------------*/
	char* server = "220.68.54.132";
	char* user = "kang";
	char* password = "Strong1234%";
	char* db_name = "ARP";
	/*------------------------thread 변수 및 구조체---------------------------*/
    pthread_t request_thread;
    pthread_t capture_thread;
    pthread_t ui_thread;
    int thr_id;
    int menu, error;
    int i, j = 0, input;
    int inum, res, status;
    char errbuf[PCAP_ERRBUF_SIZE];
	pthread_cond_t Cond_A = PTHREAD_COND_INITIALIZER;

    system("clear");
	
	if(!(conn = mysql_init((MYSQL*)NULL)))
	{
		printf("Mysql initialize fail\n");
		exit(1);
	}

	if(!(mysql_real_connect(conn, server, user, password, NULL, 3306, NULL, 0)))
	{
		printf("connect error to mysqlserver\n");
		exit(1);
	}

	if(mysql_select_db(conn, db_name) != 0)
	{
		mysql_close(conn);
		printf("fail to select database\n");
		exit(1);
	}
	if(mysql_query(conn, "UPDATE ARPUserTable SET status = 'Ab'"))
	{
		printf("database initailize fail\nprogram terminate\n");
		exit(1);
	}
	printf("DB init complete\n");
	printf("mysqlserver connection complete\n");

    printf("---------------Select Using Network Interface--------------\n");
	
    if(pcap_findalldevs(&alldevs, errbuf) == -1)
    {
		printf("Error in pcap_findalldevs: %s\n", errbuf);
		exit(1);
    }
    
    for(dev = alldevs; dev; dev = dev->next)
    {
		printf("%d. %s", ++j, dev->name);

		if(dev->description)
	    	printf(" (%s) \n", dev->description);

		else
	    	printf(" (No  description available)\n");
    }
	
    if(j == 0)
    {
		printf("\nNo interface found! Make sure LiPcap is installed.\n");
    }

    printf("Enter the interface number (1-%d) ",j);
    scanf("%d", &inum);

    if(inum < 1 || inum > j)
    {
		printf("\nAdapter number out of range.\n");
		pcap_freealldevs(alldevs);
		return -1;
    }

    for(dev = alldevs, j=0; j < inum-1; dev = dev->next, j++);
	
    thr_id =  pthread_create(&request_thread, NULL,Request, NULL);
    if(thr_id < 0)
    {
		perror("thread create error :");
		exit(0);
    }

    thr_id =  pthread_create(&capture_thread, NULL, Sniff, NULL);
    if(thr_id < 0)
    {
		perror("thread create error :");
		exit(0);
    }
	
    pthread_join(request_thread, (void**)&status);
    pthread_join(capture_thread, (void**)&status);
    return 0;
}

