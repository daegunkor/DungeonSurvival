import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ReadyRoom  extends JFrame implements ActionListener, Runnable{
	// ���� writer, reader
	private ObjectOutputStream 	oos;			// ��ü ��� ��Ʈ��
	private ObjectInputStream	ois;			// ��ü �Է� ��Ʈ��
	
	// �޼��� ĳó
	private MsgCatcher msgCatcher;
	
	private Container 	ct;

	// �� �г�
	private JPanel 		mapChoicePanel;
	private ImageIcon	mapImage;
	private JButton		mapButton;
	// �Ŵ� �г�
	private JPanel		menuPanel;
	private JButton		getReadyButton;
	private JButton		gameStartButton;
	
	// ä�� �г�
	private JPanel			chatPanel;
	private	JScrollPane		chatAreaPanel;
	private JPanel			sendTextPanel;	
	private JTextArea		outputTextArea;
	private JTextField		inputTextField;
	private JButton			textSendButton;
	
	// �÷��̾� ����Ʈ �г�
	private JScrollPane	playerListPanel;
	private JComboBox<String> 	playerClassBox = new JComboBox();
	private String[] 	header	= {"�÷��̾� ��","���� Ŭ����","���� ����"};
	private Object[][]	playerListContent = { // �÷��̾� ����Ʈ �� ����
			{"a","b","c"}
	};		
	private String[]			charaClass = {"������","������","����"};	// �÷��̾� Ŭ����
	private DefaultTableModel 	playerTableModel = new DefaultTableModel(playerListContent, header);
	private JTable				playerTable;	// �÷��̾� ���̺�
	
	private boolean isSeletedChaClass = false;	// Ŭ������ ���� �Ǿ��°�
	
	private boolean isOnGame			= false;
	
	private String chaClStr    = "";	// ���õ� Ŭ���� ���ڿ�( ���� )

	
	public void run(){
		GameObject fromServerObj;		// ������ ������ ��ü
		textSendButton.addActionListener(this);
		inputTextField.addActionListener(this);
		playerClassBox.addActionListener(this);
		gameStartButton.addActionListener(this);
		
			// �����κ��� �ż����� �޴´� 
			while(true){
				if(msgCatcher.getSendCl().equals("readyRoom") && msgCatcher.getHeardMe() == false){
					fromServerObj = msgCatcher.getServerObj();	// �޼����� ���� ��
					msgCatcher.setHeardMe(true);					// �޾Ҵٴ� ������ ������.
					applyGameObj(fromServerObj);					// �ش� ��ü�� �����Ѵ�.
					System.out.println(fromServerObj.getMessage());
				}
				System.out.print("");
			}
	}
		
	
	
	public ReadyRoom(Socket socket,ObjectOutputStream oos, ObjectInputStream ois,MsgCatcher msgCatcher){
		this.oos = oos;
		this.ois = ois;
		this.msgCatcher = msgCatcher;
		ct = getContentPane();
		ct.setLayout(new BorderLayout(10,10));
		
	

		// �÷��̾� ����Ʈ ������ ��û�Ѵ�
		try {
			oos.writeObject(new GameObject(0,"[requestPlayerList]", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
		playerTable = new JTable(playerListContent, header);
		playerListPanel = new JScrollPane(playerTable);
		playerTable.setFont(new Font("Serif", Font.BOLD, 30)); 
		playerTable.setRowHeight(30);
		
		// �޴� �ǳ� ������
		menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout());
		getReadyButton = new JButton("�غ��ϱ�");
		getReadyButton.setPreferredSize(new Dimension(100, 40));
		gameStartButton = new JButton("�����ϱ�");
		gameStartButton.setPreferredSize(new Dimension(100, 40));
		menuPanel.add(playerClassBox);
		menuPanel.add(gameStartButton);
		
		// �� ���� �ǳ� ������
		mapChoicePanel	= new JPanel(new BorderLayout());
		mapImage = new ImageIcon("map.jpg");
		mapButton = new JButton(mapImage);
		mapChoicePanel.add(mapButton, BorderLayout.CENTER);
		mapChoicePanel.add(menuPanel, BorderLayout.SOUTH);
		
		
		// ä�� �ǳ� ������
		chatPanel = new JPanel(new BorderLayout());
		
		outputTextArea = new JTextArea(10,8);
		chatAreaPanel = new JScrollPane(outputTextArea);
		
		
		
		sendTextPanel = new JPanel();
		inputTextField = new JTextField(50);
		textSendButton = new JButton("������");
		sendTextPanel.add(inputTextField);
		sendTextPanel.add(textSendButton);
		
		chatPanel.add(chatAreaPanel,BorderLayout.CENTER);
		chatPanel.add(sendTextPanel, BorderLayout.SOUTH);
		
		// �÷��̾� ���� �ǳ� ������
		for(int i = 0; i < charaClass.length; i++)	
			playerClassBox.addItem(charaClass[i]);	// Ŭ���� �޺��ڽ� ����
		ct.add(playerListPanel, BorderLayout.CENTER);
		ct.add(mapChoicePanel, BorderLayout.EAST);
		ct.add(chatPanel, BorderLayout.SOUTH);
		
		setTitle("Survival Game - login");
		setSize(700,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		setVisible(true);
		new Thread(this).start();
	}
	public void actionPerformed(ActionEvent ae){
		// ä�� �����⸦ �Է��� ���
		if(ae.getSource() == textSendButton || ae.getSource() == inputTextField){
			// �ڽ��� ���� ���� ��� ������ �޼����� ������.
			if(inputTextField.getText() != ""){
				try {
					oos.writeObject(new GameObject(0,"[chatToServer]",getChatString()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputTextField.setText("");
			}
		}
		// �÷��̾� Ŭ������ ������ ���
		else if(ae.getSource() == playerClassBox ){
			try {
				oos.writeObject(new GameObject(0,"[setMyClassName]",(String)playerClassBox.getSelectedItem()));
				oos.writeObject(new GameObject(0,"[requestPlayerList]", null));
				setChaClStr((String)playerClassBox.getSelectedItem());
				isSeletedChaClass = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		// �����ϱ⸦ ������ ���
		else if(ae.getSource() == gameStartButton){
			// ĳ���� Ŭ������ �������� �ʾҴٸ�
			if(getIsSelectedChaClass() == false)
				JOptionPane.showMessageDialog(null, "Ŭ������ �������ּ���.");
			
			// ĳ���� Ŭ������ �����ߴٸ�
			else{
				// ���ӻ��¸� ON
				setIsOnGame(true);
				new Thread(this).stop();
				// �������� �޼���
				try {
					oos.writeObject(new GameObject(0,"[setMeOnGame]", null));
					oos.writeObject(new GameObject(0,"[requestPlayerList]", null));
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	public String getChatString(){
		return inputTextField.getText();
	}
	
	public boolean getIsSelectedChaClass(){
		return isSeletedChaClass;
	}
	
	public boolean getIsOnGame(){
		return isOnGame;
	}
	public void setIsOnGame(boolean isOnGame){
		this.isOnGame = isOnGame;
	}
	
	// Ŭ���� ���ڿ� ����
	public void setChaClStr(String selClass){
		if(selClass.equals("������"))
			this.chaClStr = "fighter";
		else if(selClass.equals("������"))
			this.chaClStr = "strategist";
		else if(selClass.equals("����"))
			this.chaClStr = "theif";
	}
	
	// Ŭ���� ���ڿ� ��ȯ
	public String getChaClStr(){
		return chaClStr;
	}
	
	// ���� ������Ʈ�� ���� ó��
	public void applyGameObj(GameObject gameObj){
		// �����κ��� ä�� �޼����� ���� 
		if(gameObj.getMessage().equals("[chatFromServer]") ){
			String temp = (String) gameObj.getData();
			// ä�� ���� ���̸�� �߰�
			outputTextArea.append(temp + "\n");
			// ä�� ��ũ�� ���� �Ʒ���
			chatAreaPanel.getVerticalScrollBar().setValue(chatAreaPanel.getVerticalScrollBar().getMaximum());
		}
		
		//�����κ��� �÷��̾� ������ ����
		else if(gameObj.getMessage().equals("[sendPlayerList]")){
			// �÷��̾� ����Ʈ ������ ����
			playerListContent = (Object[][])gameObj.getData();
			
			
			// ���̺� ����
			playerTableModel = new DefaultTableModel (playerListContent, header);
			playerTable.setModel(playerTableModel);
			

		}
		
	
	
	}
}
