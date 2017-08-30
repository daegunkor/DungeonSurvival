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
	// 서버 writer, reader
	private ObjectOutputStream 	oos;			// 객체 출력 스트림
	private ObjectInputStream	ois;			// 객체 입력 스트림
	
	// 메세지 캐처
	private MsgCatcher msgCatcher;
	
	private Container 	ct;

	// 맵 패널
	private JPanel 		mapChoicePanel;
	private ImageIcon	mapImage;
	private JButton		mapButton;
	// 매뉴 패널
	private JPanel		menuPanel;
	private JButton		getReadyButton;
	private JButton		gameStartButton;
	
	// 채팅 패널
	private JPanel			chatPanel;
	private	JScrollPane		chatAreaPanel;
	private JPanel			sendTextPanel;	
	private JTextArea		outputTextArea;
	private JTextField		inputTextField;
	private JButton			textSendButton;
	
	// 플레이어 리스트 패널
	private JScrollPane	playerListPanel;
	private JComboBox<String> 	playerClassBox = new JComboBox();
	private String[] 	header	= {"플레이어 명","선택 클래스","실행 여부"};
	private Object[][]	playerListContent = { // 플레이어 리스트 및 정보
			{"a","b","c"}
	};		
	private String[]			charaClass = {"격투가","전략가","도둑"};	// 플레이어 클래스
	private DefaultTableModel 	playerTableModel = new DefaultTableModel(playerListContent, header);
	private JTable				playerTable;	// 플레이어 테이블
	
	private boolean isSeletedChaClass = false;	// 클래스가 선택 되었는가
	
	private boolean isOnGame			= false;
	
	private String chaClStr    = "";	// 선택된 클래스 문자열( 영문 )

	
	public void run(){
		GameObject fromServerObj;		// 서버로 부터의 객체
		textSendButton.addActionListener(this);
		inputTextField.addActionListener(this);
		playerClassBox.addActionListener(this);
		gameStartButton.addActionListener(this);
		
			// 서버로부터 매세지를 받는다 
			while(true){
				if(msgCatcher.getSendCl().equals("readyRoom") && msgCatcher.getHeardMe() == false){
					fromServerObj = msgCatcher.getServerObj();	// 메세지를 받은 후
					msgCatcher.setHeardMe(true);					// 받았다는 응답을 보낸다.
					applyGameObj(fromServerObj);					// 해당 객체를 적용한다.
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
		
	

		// 플레이어 리스트 정보를 요청한다
		try {
			oos.writeObject(new GameObject(0,"[requestPlayerList]", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
		playerTable = new JTable(playerListContent, header);
		playerListPanel = new JScrollPane(playerTable);
		playerTable.setFont(new Font("Serif", Font.BOLD, 30)); 
		playerTable.setRowHeight(30);
		
		// 메뉴 판넬 디자인
		menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout());
		getReadyButton = new JButton("준비하기");
		getReadyButton.setPreferredSize(new Dimension(100, 40));
		gameStartButton = new JButton("시작하기");
		gameStartButton.setPreferredSize(new Dimension(100, 40));
		menuPanel.add(playerClassBox);
		menuPanel.add(gameStartButton);
		
		// 맵 선택 판넬 디자인
		mapChoicePanel	= new JPanel(new BorderLayout());
		mapImage = new ImageIcon("map.jpg");
		mapButton = new JButton(mapImage);
		mapChoicePanel.add(mapButton, BorderLayout.CENTER);
		mapChoicePanel.add(menuPanel, BorderLayout.SOUTH);
		
		
		// 채팅 판넬 디자인
		chatPanel = new JPanel(new BorderLayout());
		
		outputTextArea = new JTextArea(10,8);
		chatAreaPanel = new JScrollPane(outputTextArea);
		
		
		
		sendTextPanel = new JPanel();
		inputTextField = new JTextField(50);
		textSendButton = new JButton("보내기");
		sendTextPanel.add(inputTextField);
		sendTextPanel.add(textSendButton);
		
		chatPanel.add(chatAreaPanel,BorderLayout.CENTER);
		chatPanel.add(sendTextPanel, BorderLayout.SOUTH);
		
		// 플레이어 정보 판낼 디자인
		for(int i = 0; i < charaClass.length; i++)	
			playerClassBox.addItem(charaClass[i]);	// 클래스 콤보박스 설정
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
		// 채팅 보내기를 입력한 경우
		if(ae.getSource() == textSendButton || ae.getSource() == inputTextField){
			// 박스에 글이 있을 경우 서버로 메세지를 보낸다.
			if(inputTextField.getText() != ""){
				try {
					oos.writeObject(new GameObject(0,"[chatToServer]",getChatString()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputTextField.setText("");
			}
		}
		// 플레이어 클래스를 선택한 경우
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
		
		// 시작하기를 선택한 경우
		else if(ae.getSource() == gameStartButton){
			// 캐릭터 클래스를 선택하지 않았다면
			if(getIsSelectedChaClass() == false)
				JOptionPane.showMessageDialog(null, "클래스를 선택해주세요.");
			
			// 캐릭터 클래스를 선택했다면
			else{
				// 게임상태를 ON
				setIsOnGame(true);
				new Thread(this).stop();
				// 서버에도 메세지
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
	
	// 클래스 문자열 설정
	public void setChaClStr(String selClass){
		if(selClass.equals("격투가"))
			this.chaClStr = "fighter";
		else if(selClass.equals("전략가"))
			this.chaClStr = "strategist";
		else if(selClass.equals("도둑"))
			this.chaClStr = "theif";
	}
	
	// 클래스 문자열 반환
	public String getChaClStr(){
		return chaClStr;
	}
	
	// 게임 오브젝트에 대한 처리
	public void applyGameObj(GameObject gameObj){
		// 서버로부터 채팅 메세지가 오면 
		if(gameObj.getMessage().equals("[chatFromServer]") ){
			String temp = (String) gameObj.getData();
			// 채팅 내용 에이리어에 추가
			outputTextArea.append(temp + "\n");
			// 채팅 스크롤 가장 아래로
			chatAreaPanel.getVerticalScrollBar().setValue(chatAreaPanel.getVerticalScrollBar().getMaximum());
		}
		
		//서버로부터 플레이어 정보가 오면
		else if(gameObj.getMessage().equals("[sendPlayerList]")){
			// 플레이어 리스트 정보를 저장
			playerListContent = (Object[][])gameObj.getData();
			
			
			// 테이블에 적용
			playerTableModel = new DefaultTableModel (playerListContent, header);
			playerTable.setModel(playerTableModel);
			

		}
		
	
	
	}
}
