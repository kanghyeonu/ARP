import java.awt.event.*;
import java.sql.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
 
class MysqlConnecter{
	private Connection conn = null;
	private String query;
	MysqlConnecter(){
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP?useUnicode=true&characterEncoding=utf8","kang","Strong1234%");
		} catch(SQLException ex)
        { 
			System.out.println("SQLException:"+ex);
		} catch(Exception ex)
		{ 
			System.out.println("Exception:"+ex); 
		} finally
		{ 
			try
			{ 
				if ( conn != null)
				{ 
					conn.close(); 
				} 
			}catch(Exception e){}
		}
	}
		
	public ResultSet BrowseAll() {
		try 
		{
			Statement state = conn.createStatement();
			
			query = "select * from ARPUserTable";
			ResultSet result = state.executeQuery(query);
			
			return result;
			
		}catch(SQLException ex)
        { 
			System.out.println("SQLException:"+ex);
		} catch(Exception ex)
		{ 
			System.out.println("Exception:"+ex); 
		} finally
		{ 
			try
			{ 
				if ( conn != null)
				{ 
					conn.close(); 
				} 
			}catch(Exception e){}
		}
		
		return null;
	}
}

class Browse extends JPanel { 
 
    private JButton jButton1, jButton2;
    private JTable jtable;
    private JScrollPane scrollpane;
    //private JPanelTest win;
    DefaultTableModel model;
    
    String combo_status[] = {"선택없음", "출근","지각","결근", "등록", }; 
    String mapping[] = { "", "'A'", "'T'", "'Ab'", "'S'"};
    String select = "선택없음";
    int index = 0;
    
    public Connection conn = null;
    
    public Browse(JPanelTest win) {
    	String columns[] = {"MAC 주소", "이름", "출결 상태", "마지막 확인 시간"};    	
    	String [][] rows = new String[0][4];
    	
    	model = new DefaultTableModel(rows, columns);    	
        try
        { 
        	Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP?useUnicode=true&characterEncoding=utf8","kang","Strong1234%");
			
			Statement state = conn.createStatement();
			String query;
			
			query = "select * from ARPUserTable";
			ResultSet result = state.executeQuery(query);
			//출력부분
			while( result.next())
			{
				String mac = result.getString("mac_address");
				String name = result.getString("name");
				String status = result.getString("status");
				String last_check = result.getString("last_check");
				
				switch(status)
				{
					case "A":
						status = "출근";
						break;
					case "Ab":
						status = "결근";
						break;
					case "T":
						status = "지각";
						break;
					case "S":
						status = "등록";
						break;
						
				}
					
				
				String[] row = {mac, name, status, last_check};
				
				model.addRow(row);
				//System.out.println(mac + "\t" + name + "\t" + status + "\t" + last_check);
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
			{ 
				if ( conn != null)
				{ 
					conn.close(); 
				} 
			}catch(Exception e){} 
		} 
    	
        //this.win = win;
        setLayout(null);
 
        jButton1 = new JButton("Sign Up");
        jButton1.setSize(90, 20);
        jButton1.setLocation(10, 10);
        add(jButton1);
        
        jButton2 = new JButton("Browse");
        jButton2.setSize(90, 20);
        jButton2.setLocation(100, 10);
        add(jButton2);
        
        jtable = new JTable(model);
        scrollpane = new JScrollPane(jtable);
        scrollpane.setSize(500, 200);
        scrollpane.setLocation(10, 40);
        add(scrollpane);
        
        //기본 수동 조회
        JButton btn3 = new JButton("Refresh");
        btn3.setSize(100, 20);
        btn3.setLocation(520, 50);
        add(btn3);
        btn3.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		try
        		{
        			model.setNumRows(0);	//row 비움
	        		
	            	Class.forName("com.mysql.cj.jdbc.Driver");
	    			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP?useUnicode=true&characterEncoding=utf8","kang","Strong1234%"); 
	    			
	    			Statement state = conn.createStatement();
	    			String query;
	    			
	    			if(index == 0)
	    				query = "select * from ARPUserTable";
	    			
	    			else
	    				query = "select * from ARPUserTable where status = " + select;
	    			
	    			ResultSet result = state.executeQuery(query);
	    			
	    			while( result.next())
	    			{
	    				String mac = result.getString("mac_address");
	    				String name = result.getString("name");
	    				String status = result.getString("status");
	    				String last_check = result.getString("last_check");
	    				
	    				switch(status)
	    				{
	    					case "A":
	    						status = "출근";
	    						break;
	    					case "Ab":
	    						status = "결근";
	    						break;
	    					case "T":
	    						status = "지각";
	    						break;
	    					case "S":
	    						status = "등록";
	    						break;
	    						
	    				}
	    					
	    				
	    				String[] row = {mac, name, status, last_check};
	    				
	    				model.addRow(row);

        				//System.out.println(mac + "\t" + name + "\t" + status + "\t" + last_check);
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
        			{ 
        				if ( conn != null)
        				{ 
        					conn.close(); 
        				} 
        			}catch(Exception E){} 
        		} 
        	}
        });
        
        JLabel Jlabel = new JLabel("Search Option");
        Jlabel.setSize(100, 20);
        Jlabel.setLocation(520, 80);
        add(Jlabel);
        
        JComboBox<String> combo = new JComboBox<String>(combo_status);
        combo.setLocation(520, 100);
        combo.setSize(100, 20);
        add(combo);
        combo.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		try
                { 
	        		JComboBox<?> cb = (JComboBox<?>)e.getSource();
	        		index = cb.getSelectedIndex();
	        		select = mapping[index];
	        		
	        		model.setNumRows(0);	//row 비움
	        		
	            	Class.forName("com.mysql.cj.jdbc.Driver");
	    			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP?useUnicode=true&characterEncoding=utf8","kang","Strong1234%"); 
	    			
	    			Statement state = conn.createStatement();
	    			String query;
	    			
	    			if(index == 0)
	    				query = "select * from ARPUserTable";
	    			
	    			else
	    				query = "select * from ARPUserTable where status = " + select;
	    			
	    			ResultSet result = state.executeQuery(query);
	    			
	    			while( result.next())
	    			{
	    				String mac = result.getString("mac_address");
	    				String name = result.getString("name");
	    				String status = result.getString("status");
	    				String last_check = result.getString("last_check");
	    				
	    				switch(status)
	    				{
	    					case "A":
	    						status = "출근";
	    						break;
	    					case "Ab":
	    						status = "결근";
	    						break;
	    					case "T":
	    						status = "지각";
	    						break;
	    					case "S":
	    						status = "등록";
	    						break;
	    						
	    				}
	    					
	    				
	    				String[] row = {mac, name, status, last_check};
	    				
	    				model.addRow(row);
	    				//System.out.println(mac + "\t" + name + "\t" + status + "\t" + last_check);
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
	    			{
	    				if ( conn != null)
	    				{ 
	    					conn.close(); 
	    				} 
	    			}catch(Exception E){} 
	    		} 
        	}
        });
        
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


class SignUp extends JPanel { 
    private JTextField macField;
    private JTextField nameField;
    //private JPanelTest win;
 
    public SignUp(JPanelTest win) {
        setLayout(null);
       // this.win = win;
        
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
        			Class.forName("com.mysql.cj.jdbc.Driver");
    				conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP?useUnicode=true&characterEncoding=utf8","kang","Strong1234%"); 
    				/*----------------포맷확인------------------*/
    				mac = macField.getText();
    				
    				String[] arr = mac.split(":");
    				if(arr.length != 6)
    				{
    					new SendFrame(0, "입력포맷오류");
    					return;
    				}
    				for(int i = 0; i < 6; i++)
    				{
    					if(arr[i].length() != 2)
    					{
    						new SendFrame(0, "입력포맷오류");
    						return;

    					}
    				}
    				name = nameField.getText();
    				if(name.length() > 10)
    				{
    					new SendFrame(0, "입력포맷오류");
    					
    					return;
    				}
    				/*--------------------------------------------*/
    				
    				/*-------------------쿼리 보냄--------------------*/
    				Statement state = conn.createStatement();
    				String query;
    				
    				query = "INSERT INTO ARPUserTable(mac_address, name) VALUES ('"+ mac + "','" + name+"')";
    				state.executeUpdate(query);
    				
    				new SendFrame(1, "전송 성공");
    				
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
        			{
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
class SendFrame extends JDialog{
	JLabel ok_label = new JLabel();
	public SendFrame(int success, String str) {
		if(success > 0)
			ok_label.setText(str);
		else
			ok_label.setText(str);
		ok_label.setLocation(100, 50);
		
		getContentPane().add(ok_label);
		
		this.setSize(200, 100);
		this.setModal(true);
		this.setVisible(true);
	}
}
 
class JPanelTest extends JFrame {
 
    public Browse Browse = null;
    public SignUp Signup = null;
    
    JPanelTest(){
    	this.setTitle("Attendance check system");
        this.Browse = new Browse(this);
        this.Signup = new SignUp(this);
 
        this.add(this.Browse);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(650, 300);
        this.setVisible(true);
    }
    
    public void change(String panelName) { 
 
        if (panelName.equals("panel01")) 
        {
            getContentPane().removeAll();
            getContentPane().add(Browse);
            revalidate();
            repaint();
        } 
        else 
        {
            getContentPane().removeAll();
            getContentPane().add(Signup);
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
        	Class.forName("com.mysql.cj.jdbc.Driver"); //MySQL ������ �����մϴ�. 
			conn = DriverManager.getConnection("jdbc:mysql://220.68.54.132:3306/ARP","kang","Strong1234%"); 
			System.out.println("������ ���̽� ������ �����߽��ϴ�."); 
			
			Statement state = conn.createStatement();
			String query;
			
			query = "select * from ARPUserTable";
			ResultSet result = state.executeQuery(query);
			
			while( result.next())
			{
				String mac = result.getString("mac_address");
				String name = result.getString("name");
				String status = result.getString("status");
				String last_check = result.getString("last_check");
				
				System.out.println(mac + "\t" + name + "\t" + status + "\t" + last_check);
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
			{ //�����ͺ��̽� Close ���ֱ� 
				if ( conn != null)
				{ 
					conn.close(); 
				} 
			}catch(Exception e){} 
		} */
    }
}