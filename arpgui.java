import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
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
    private JTextField textField;
    private JPasswordField passwordField;
    private JPanelTest win;
 
    public Enroll(JPanelTest win) {
        setLayout(null);
        this.win = win;
        
        /*-----------------------------------------------------------*/
        JLabel input_mac = new JLabel("MAC Address: ");
        input_mac.setBounds(23, 53, 90, 15);
        add(input_mac);
 
        textField = new JTextField();
        textField.setBounds(123, 50, 116, 21);
        add(textField);
        textField.setColumns(10);
        /*-----------------------------------------------------------*/
        JLabel input_name = new JLabel("NAME: ");
        input_name.setBounds(68, 91, 67, 15);
        add(input_name);
 
        passwordField = new JPasswordField();
        passwordField.setBounds(123, 88, 116, 21);
        add(passwordField);
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
        btn3.setLocation(100, 10);
        add(btn3);
        btn3.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                win.change("panel01");
       	 	}
        });
    }
 
}
 
 
@SuppressWarnings("serial")
class JPanelTest extends JFrame {
 
    public Browse Browse = null;
    public Enroll Enroll = null;
 
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
 
        JPanelTest win = new JPanelTest();
 
        win.setTitle("Attendance check system");
        win.Browse = new Browse(win);
        win.Enroll = new Enroll(win);
 
        win.add(win.Browse);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setSize(500, 300);
        win.setVisible(true);
    }
}