#include<mysql/mysql.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

int main(){
	MYSQL *conn;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int query_stat;

	char* server = "220.68.54.132";
	char* user = "kang";
	char* password = "Strong1234%";
	char* db_name = "ARP";
	char query_buffer[2048];
	char update_query[2048];
	char mac_address[20];

	if(!(conn = mysql_init((MYSQL*)NULL)))
	{
		printf("init fail\n");
		exit(1);
	}

	printf("mysql_init success\n");

	if(!(mysql_real_connect(conn, server, user, password, db_name ,3306, NULL, 0)))
	{
		printf("connect error\n");
		exit(1);
	}

	printf("mysql connect success\n");

	if(mysql_select_db(conn, db_name) != 0)
	{
		mysql_close(conn);
		printf("select_db fail\n");
	}
	
	printf("select mydb success\n");

	//mysql_query(conn, "UPDATE ARPUserTable SET attendance = 1 WHERE name=/'강현우/'");	
	
	sprintf(query_buffer, "SELECT * FROM ARPUserTable WHERE mac_address = '%s'", "a8:2b:b9:07:cc:72");

//	sprintf(string, "UPDATE ARPUserTable SET  attendance = 1 WHERE mac_address = '%s'", mac_address);
	//if(mysql_query(conn, "SELECT * FROM ARPUserTable WHERE name = '%s'", mysql_real_escape_string($name)))
	if(mysql_query(conn, query_buffer))
	{
		printf("query fail\n");
		exit(1);
	}
	
	printf("query succses\n");

	res = mysql_store_result(conn);
	printf("res sucess\n");

	if((row = mysql_fetch_row(res)) == NULL)
	{
		printf("anything\n");
		exit(1);
	}

	printf("%s %s %s\n", row[0], row[1], row[2]);
	sprintf(mac_address, "%s", "a8:2b:b9:07:cc:72");
	sprintf(update_query, "UPDATE ARPUserTable SET  status = '媛' WHERE name  = 'test1'");


	if(mysql_query(conn, update_query))
	{
		printf("update fail");
		exit(1);
	}
	printf("update success\n");
	if(mysql_query(conn, query_buffer))
	{
		printf("query fail");
		exit(1);
	}
	printf("query succses\n");

	res = mysql_store_result(conn);
	printf("res sucess\n");

	if((row = mysql_fetch_row(res)) == NULL)
	{
		printf("anything\n");
		exit(1);
	}
	printf("%s %s %s\n", row[0], row[1], row[2]);

	//if(strcmp(row[1], "a8:2b:b9:07:cc:72") == 0)
	//if(strcmp(row[0], "강현우"))
		//printf("compare success");*/

	mysql_close(conn);
}
