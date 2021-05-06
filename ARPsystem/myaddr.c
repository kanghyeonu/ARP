#include <stdio.h>
#include <sys/socket.h>
#include <string.h>
#include <netdb.h>
#include <sys/utsname.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <sys/ioctl.h>

char * getIfToIP(char *ifName)
{

 int fd;
 struct ifreq ifr;

 fd = socket(AF_INET, SOCK_DGRAM, 0);

 /* I want to get an IPv4 IP address */
// ifr.ifr_addr.sa_family = AF_INET;

 /* I want IP address attached to interfaceName */
 strncpy(ifr.ifr_name, ifName, IFNAMSIZ-1);
 ioctl(fd, SIOCGIFADDR, &ifr);
 close(fd);

 return  inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr);
}

char **my_addrs(int *addrtype)
{
    struct hostent  *hptr;
    struct utsname  myname;


    if (uname(&myname) < 0)
        return NULL;

    if ((hptr = gethostbyname(myname.nodename)) == NULL)
        return NULL;

    *addrtype = hptr->h_addrtype;

    return (hptr->h_addr_list);
}

int main(){
	struct in_addr in;
    char **addr;
	char test[20];
   	int type;

	sprintf(test ,"%s", getIfToIP("wlxd03745decb1c"));

	printf("%d", strcmp(test, "192.168.137.159"));



	/*addr=my_addrs(&type);

   	memcpy(&in.s_addr, *addr, sizeof (in.s_addr));
   	printf("%s\n", inet_ntoa(in));*/

   	return 0;
}
