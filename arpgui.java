import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
@SuppressWarnings("serial")
class Browse extends JPanel { // 1번째 패널
 
    private JButton jButton1, jButton2;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JPanelTest win;
 
    public Browse(JPanelTest win) {
        this.win = win;
        setLayout(null);
 
        jButton1 = new JButton("Sign Up");
        jButton1.setSize(90, 20);
        jButton1.setLocation(10, 10);
        add(jButton1);
        
        jButton2 = new JButton("Browse");
        jButton2.setSize(90, 20);
        jButton2.setLocation(100, 10);
        add(jButton2);
        
        jTextArea1 = new JTextArea();
 
        jScrollPane1 = new JScrollPane(jTextArea1);
        jScrollPane1.setSize(200, 150);
        jScrollPane1.setLocation(10, 40);
        add(jScrollPane1);
 
        jButton1.addActionListener(new ActionListener(){
        	 @Override
             public void actionPerformed(ActionEvent e) {
                 win.change("panel02");
        	 }
        });
        
        jButton2.addActionListener(new ActionListener(){
       	 @Override
            public void actionPerformed(ActionEvent e) {
                win.change("panel01");
       	 }
       });
    }
}
 
@SuppressWarnings("serial")
class Enroll extends JPanel { // 2번째 패널
    private JTextField macField;
    private JTextField nameField;
    private JPanelTest win;
 
    public Enroll(JPanelTest win) {
        setLayout(null);
        this.win = win;
        
        /*-----------------------------------------------------------*/
        JLabel input_mac = new JLabel("MAC Address: ");
        input_mac.setBounds(23, 53, 90, 15);
        add(input_mac);
 
        macField = new JTextField();
        macField.setBounds(123, 50, 116, 21);
        add(macField);
        macField.setColumns(10);
        /*-----------------------------------------------------------*/
        JLabel input_name = new JLabel("NAME: ");
        input_name.setBounds(68, 91, 67, 15);
        add(input_name);
 
        nameField = new JTextField();
        nameField.setBounds(123, 88, 116, 21);
        add(nameField);
        /*-----------------------------------------------------------*/
        JButton btn1 = new JButton("Sign Up");
        btn1.setSize(90, 20);
        btn1.setLocation(10, 10);
        add(btn1);
        btn1.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                win.change("panel02");
       	 	}
        });
       
        JButton btn2 = new JButton("Browse");
        btn2.setSize(90, 20);
        btn2.setLocation(100, 10);
        add(btn2);
        btn2.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                win.change("panel01");
       	 	}
        });
        
        JButton btn3 = new JButton("Send");
        btn3.setSize(90, 20);
        btn3.setLocation(130, 150);
        add(btn3);
        btn3.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		String mac, name;
        		Connection conn = null; 
        		try
        		{
        			Class.forName("com.mysql.cj.jdbc.Driver"); //MySQL 서버를 설정합니다. 
    				conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP","kang","Strong1234%"); 
    				//System.out.println("데이터 베이스 접속이 성공했습니다.");
    				/*-----------------입력형식 체크-----------------*/
    				mac = macField.getText();
    				
    				String[] arr = mac.split(":");
    				if(arr.length != 6)
    				{
    					//입력 포맷 오류
    					System.out.println("입력포맷오류");
    					return;
    				}
    				for(int i = 0; i < 6; i++)
    				{
    					if(arr[i].length() != 2)
    					{
    						System.out.println("입력포맷오류");
    						return;
    						//입력 포맷 오류
    					}
    				}
    				name = nameField.getText();
    				if(name.length() > 10)
    				{
    					System.out.println("입력포맷오류");
    					return;
    					//입력포맷 오류
    				}
    				/*--------------------------------------------*/
    				
    				/*-------------------쿼리전송--------------------*/
    				Statement state = conn.createStatement();
    				String query;
    				
    				query = "INSERT INTO ARPUserTable(mac_address, name) VALUES ("+ mac + "," + name+")";
    				state.executeQuery(query);
    				
        		} catch(SQLException ex)
        		{
        			System.out.println("SQLException:"+ex);
        			System.out.println("SQLException:"+ex);
        		}catch(Exception ex)
        		{ 
        			System.out.println("Exception:"+ex); 
        		} finally
        		{ 
        			try
        			{ //데이터베이스 Close 해주기 
        				if ( conn != null)
        				{ 
        					conn.close(); 
        				} 
        			}catch(Exception E){} 
        		} 
        		
       	 	}
        });
    }
 
}
 
 
@SuppressWarnings("serial")
class JPanelTest extends JFrame {
 
    public Browse Browse = null;
    public Enroll Enroll = null;
    
    JPanelTest(){
    	this.setTitle("Attendance check system");
        this.Browse = new Browse(this);
        this.Enroll = new Enroll(this);
 
        this.add(this.Browse);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500, 300);
        this.setVisible(true);
    }
    
    public void change(String panelName) { // 패널 1번과 2번 변경 후 재설정
 
        if (panelName.equals("panel01")) {
            getContentPane().removeAll();
            getContentPane().add(Browse);
            revalidate();
            repaint();
        } else {
            getContentPane().removeAll();
            getContentPane().add(Enroll);
            revalidate();
            repaint();
        }
    }
    
    
 
}
 
public class Main {
    public static void main(String[] args) {
   
    	new JPanelTest();
   	  
        /*Connection conn = null; 
        try
        { 
        	Class.forName("com.mysql.cj.jdbc.Driver"); //MySQL 서버를 설정합니다. 
			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP","kang","Strong1234%"); 
			System.out.println("데이터 베이스 접속이 성공했습니다."); 
			
			Statement state = conn.createStatement();
			String query;
			
			query = "select * from ARPUserTable";
			ResultSet result = state.executeQuery(query);
			
			while( result.next())
			{
				String mac = result.getString("mac_address");
				String name = result.getString("name");
				String attendance = result.getString("attendance");
				
				System.out.println(mac + "\t" + name + "\t" + attendance);
			}
			
		} catch(SQLException ex)
        { 
			System.out.println("SQLException:"+ex);
		} catch(Exception ex)
		{ 
					System.out.println("Exception:"+ex); 
		} finally
		{ 
			try
			{ //데이터베이스 Close 해주기 
				if ( conn != null)
				{ 
					conn.close(); 
				} 
			}catch(Exception e){} 

		} */
    }
}