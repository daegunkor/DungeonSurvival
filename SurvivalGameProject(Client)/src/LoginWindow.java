import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginWindow extends JFrame implements ActionListener{
	
	
	
	
	private JTextField 	nameJtf;
	private JTextField 	IPJtf;
	private JButton 	setClientInfoButton;
	private JPanel		buttonPanel;
	private JLabel		confirmMsgLabel;
	private Socket 		socket;
	private Container 	ct ;
	
	ObjectOutputStream oos;
	ObjectInputStream ois; 
	
	private Boolean		isItPerformed = false;
	
	

	public LoginWindow(){
		nameJtf = new JTextField(10);
		IPJtf 	= new JTextField(10);
		setClientInfoButton = new JButton("입장하기");
		buttonPanel 	= new JPanel();
		confirmMsgLabel = new JLabel();
		
		ct = getContentPane();
		ct.setLayout(new FlowLayout(FlowLayout.RIGHT,5,20));
		ct.add(new Label("닉네임"));
		ct.add(nameJtf);
		ct.add(new Label("I P"));
		ct.add(IPJtf);
		
		buttonPanel.add(setClientInfoButton,BorderLayout.CENTER);
		buttonPanel.add(confirmMsgLabel,BorderLayout.SOUTH);
		
		ct.add(buttonPanel);
		
		
		
		setClientInfoButton.addActionListener(this);
		
		setTitle("Survival Game - login");
		setSize(200,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource() == setClientInfoButton){
			try{ 					// 접속에 성공하였을 경우
				socket = new Socket(IPJtf.getText(), 7777);
				confirmMsgLabel.setText("접속 성공");
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				oos.writeObject(new GameObject(0,"[userName]",getName()));
				isItPerformed = true;
				
			} catch (Exception e){	// 접속에 실패하였을 경우
				confirmMsgLabel.setText("접속 실패");
			}
		}
			
	}
	
	// 소켓 정보를 가져옵니다
	public Socket getSocket(){
		return socket;
	}
	public boolean getPerformed(){
		return isItPerformed;
	}
	public JButton getSetClientInfoButton(){
		return setClientInfoButton;
	}
	// nameJtf필드 안의 값을 가져온다.
	public String getName(){
		return nameJtf.getText();
	}
	
	public ObjectInputStream getOIS(){
		return ois;
	}
	public ObjectOutputStream getOOS(){
		return oos;
	}
	

}
