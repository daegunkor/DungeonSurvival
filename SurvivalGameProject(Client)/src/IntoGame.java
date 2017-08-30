import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// 게임 실행 메서드
// 키 입력에따른 서버로 메세지를 보내고, 
// msgCatcher로 서버로 메세지를 받을떄 마다 캐릭터 움직임 적용 

public class IntoGame extends JFrame implements KeyListener,Runnable{
	
	//--------------------------- 프레임 변수 ----------------
	// 전체 판넬
	private Container ct;
	// 게임 진행 판낼
	private JPanel	mainGamePanel;
	private JLabel 	gameLabel;
	// 스킬 판넬
	private JPanel	skillPanel;
	private JLabel	skillLabel;
	
	// 입출력 스트림
	private ObjectOutputStream  oos;
	private ObjectInputStream  ois;
	
	// 위치값 세팅
	Dimension screen;
	
	// 이미지를 불러오기위한 툴킷
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	// 화면 가로 세로 크기
		int f_width = 1000;
		int f_height = 1000;
	// 캐릭터 이미지 변수
	Image chaImg;
	// 주먹 이미지 변수
	Image punchImg;
	// 배경 이미지 변수
	Image backGroundImg;
	// 더블 버퍼링용 이미지 변수
	Image buffImage;
	// 더블 버퍼링용 그래픽 변수
	Graphics buffg;
	// 캐릭터 체력바 그리기
	Graphics hpBarg;
	// 체력바 세로크기
	int hpBarHeight= 10;
	// 체력바 가로크기
	int hpBarWidth = 50;
	
	// ----------------------------- 게임 정보 변수 -----------------------
	// 서버로부터 메세지를 받는 쓰래드
	MsgCatcher msgCatcher;
	// 캐릭터 정보 객체
	CharacterInfo chaInfo;
	// 사용자들의 정보 객체
	Vector<CharacterInfo> chaInfoArr = null;
	// 공격 객체
	Vector<Integer> punchInfo;
	// 사용자들의 정보 객체에서 자신의 정보 인덱스
	int				myChaIndex;
	// 게임에 존재하는 캐릭터 위치 정보
	Vector<Integer[]> allChaPos;
	Vector<Integer[]> allChaHp ; 
	// 게임 오브젝트
	GameObject fromServerObj;
	// 키보드 입력 처리를 위한 변수
	boolean keyUp 		= false;
	boolean keyDown 	= false;
	boolean keyLeft 	= false;
	boolean keyRight 	= false;
	boolean keySpace	= false;
	boolean[] upDownLeftRight = {false,false,false,false};
	
	// 쓰레드 정의
	public void run(){
		try{
			while(true){
				// 메세지 케쳐로부터 메세지가 있을경우 
				if(msgCatcher.getSendCl().equals("intoGame") && msgCatcher.getHeardMe() == false){
					fromServerObj = msgCatcher.getServerObj();	// 메세지를 받은 후
					msgCatcher.setHeardMe(true);					// 받았다는 응답을 보낸다.
					applyGameObj(fromServerObj);					// 해당 객체를 적용한다.
					System.out.println(fromServerObj.getMessage()+ "intoGame : accepted");
				}
				System.out.print("");
				keyProcess();
				repaint();
				Thread.sleep(10);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// 프레임 구성
	public IntoGame(ObjectOutputStream oos, ObjectInputStream ois, String chaClassStr, MsgCatcher msgCa){
		
		
		chaInfo = new CharacterInfo(chaClassStr);
		chaImg = tk.getImage("image/testChaImg.png");
		
		backGroundImg = new ImageIcon("image/backGroundImg.png").getImage();
		// 입출력 스트림 저장
		this.oos = oos;
		this.ois = ois;
		
		// 컨테이너 초기화
		setSize(f_width,f_height);
		
		// 위치값 세팅(해상도)
		screen = tk.getScreenSize();
		
		
		/*ct = getContentPane();
		ct.setLayout(new BorderLayout(10,10));
		
		
		// 메인 게임 판넬 설정
		mainGamePanel = new JPanel();
		gameLabel = new JLabel(new ImageIcon("image/gameBackGround.png"));
		
		mainGamePanel.add(gameLabel);
		
		// 스킬 판넬 설정
		skillPanel = new JPanel();
		skillLabel = new JLabel(new ImageIcon("image/skillLabel.png"));
				
		skillPanel.add(skillLabel);
		
		// 컨테이너에 판넬 추가
		ct.add(mainGamePanel, BorderLayout.CENTER);
		ct.add(skillPanel, BorderLayout.SOUTH);*/
		this.setLocationRelativeTo(null);
		setVisible(true);
		
		
		// 초기정보 요청 후 완료까지 대기
		try { 
			oos.writeObject(new GameObject(0, "[giveMeChaInfoArr]", null));
			oos.writeObject(new GameObject(0, "[giveMeAllChaPos]", null));
			oos.writeObject(new GameObject(0, "[giveMeAllChaHp]", null));
		} catch (Exception e) {e.printStackTrace();}
		
		

		System.out.println("캐릭터 배열 호출 완료");
		msgCatcher = msgCa;
		start();
		
	}
	
	public void init(){
		
	}
	public void start(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		new Thread(this).start();
		
	}
	// 키보드를 눌렀을 때 이벤트
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP :
			keyUp = true;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = true;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = true;
			break;
		case KeyEvent.VK_SPACE:
			keySpace = true;
			break;
		}
		
	}
	// 키보드를 누른후 때어졌을때 이벤트
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP :
			keyUp = false;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = false;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = false;
			break;
		case KeyEvent.VK_SPACE:
			keySpace = false;
			break;
		}
		
	}
	// 입력 키 값에 따른 이벤트 발생
	public void keyProcess(){	
		try {
			
			if(keyUp == true){
				oos.writeObject(new GameObject(0,"[myChaMoveUp]",null));
			}
				
			if(keyDown == true){
				oos.writeObject(new GameObject(0,"[myChaMoveDown]",null));
			}
			
			if(keyLeft == true){
				oos.writeObject(new GameObject(0,"[myChaMoveLeft]",null));
			} 
			
			if(keyRight == true){
				oos.writeObject(new GameObject(0,"[myChaMoveRight]",null));
			}
			if(keySpace == true){
				oos.writeObject(new GameObject(0,"[punchAttack]",null));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// 키보드 타이핑 이벤트
	public void keyTyped(KeyEvent e) {
	}
	public void paint(Graphics g){
		// 더블 버퍼링의 버퍼크기 = 화면크기
		buffImage = createImage(f_width, f_height);
		
		// 버퍼의 그래픽 객체
		buffg = buffImage.getGraphics();
		
		update(g);
	}
	
	public void update(Graphics g){
		drawChar();
		
		// 화면에 버퍼의 그림을 그린다
		g.drawImage(buffImage, 0, 0, this);
	}
	public void drawChar(){
		drawBackGround();
		drawAllChaPos();
		drawAllChaHp();
		if(punchInfo != null)
			drawPunch(punchInfo);
	}
	// 배경을 그린다
	public void drawBackGround(){	
		buffg.clearRect(0, 0, f_width, f_height);
		buffg.drawImage(backGroundImg, 0, 0, this);
	}
	// 모든 캐릭터의 위치를 그린다
	public void drawAllChaPos(){
		for(int i = 0; i < allChaPos.size(); i++){
			setChaImg(allChaPos.elementAt(i)[3],allChaPos.elementAt(i)[4]);
			buffg.drawImage(chaImg, allChaPos.elementAt(i)[1], allChaPos.elementAt(i)[2], this);
		}
	}
	// 모든 캐릭터의 체력을 hpBarg에 담고 buffg 에 그린다
	public void drawAllChaHp(){
		for(int i = 0; i < allChaPos.size(); i++){
			float healthScale = allChaHp.elementAt(i)[1]/ allChaHp.elementAt(i)[2];
			buffg.setColor(Color.white);
			buffg.fillRect(allChaPos.elementAt(i)[1] -1 , allChaPos.elementAt(i)[2] - 21, hpBarWidth + 2, hpBarHeight + 2);
			buffg.setColor(Color.green);
			buffg.fillRect(allChaPos.elementAt(i)[1], allChaPos.elementAt(i)[2] - 20, (int) (hpBarWidth * healthScale), hpBarHeight);
			
		}
	}
	//
	public void drawPunch(Vector punchInfo){
		
		if((int)punchInfo.elementAt(0) == 0){
			punchImg = tk.getImage("image/punchUp.png");
		} else if((int)punchInfo.elementAt(0) == 1){
			punchImg = tk.getImage("image/punchDown.png");
		} else if((int)punchInfo.elementAt(0) == 2){
			punchImg = tk.getImage("image/punchLeft.png");
		} else if((int)punchInfo.elementAt(0) == 3){
			punchImg = tk.getImage("image/punchRight.png");
		}
		buffg.drawImage(punchImg, (Integer)punchInfo.elementAt(1), (Integer)punchInfo.elementAt(2),this);
	}
	// 클래스 종류, 캐릭터방향에 따른 이미지 설정 
	public void setChaImg(int chaClass, int chaForward){
		String imgSrc = "image/defaultChaImg.png";
		switch(chaClass){
		case 0: // 파이터 일 경우
			switch(chaForward){
			case 0:	// 윗 시선
				imgSrc = "image/fighterUp.png";
				break;
			case 1:	// 아랫 시선
				imgSrc = "image/fighterDown.png";
				break;
			case 2:	// 왼쪽 시선
				imgSrc = "image/fighterLeft.png";
				break;
			case 3:	// 오른쪽 시선
				imgSrc = "image/fighterRight.png";
				break;
			}
			break;
		case 1:	// 전략가 일 경우
			switch(chaForward){
			case 0:	// 윗 시선
				imgSrc = "image/strategistUp.png";
				break;
			case 1:	// 아랫 시선
				imgSrc = "image/strategistDown.png";
				break;
			case 2:	// 왼쪽 시선
				imgSrc = "image/strategistLeft.png";
				break;
			case 3:	// 오른쪽 시선
				imgSrc = "image/strategistRight.png";
				break;
			}
			break;
		case 2:	// 도둑일 경우
			switch(chaForward){
			case 0:	// 윗 시선
				imgSrc = "image/theifUp.png";
				break;
			case 1:	// 아랫 시선
				imgSrc = "image/theifDown.png";
				break;
			case 2:	// 왼쪽 시선
				imgSrc = "image/theifLeft.png";
				break;
			case 3:	// 오른쪽 시선
				imgSrc = "image/theifRight.png";
				break;
			}
			break;
		}
		chaImg = tk.getImage(imgSrc);
	}
	// 게임 오브젝트에 대한 처리 메서드
	public void applyGameObj(GameObject gameObj){
		
		// 서버에서 모든 캐릭터 배열을 전송받음
		if(gameObj.getMessage().equals("[sendChaInfoArr]") ){
			chaInfoArr = (Vector<CharacterInfo>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println("캐릭터 배열 적용 완료");
			for(int i = 0 ; i < this.chaInfoArr.size(); i ++){
				System.out.println(this.chaInfoArr.elementAt(i).getXPos()+", "+this.chaInfoArr.elementAt(i).getYPos());
				System.out.println(this.chaInfoArr.elementAt(i).getChaClassName());
			}
		}
		// 서버에서 모든 캐릭터의 위치를 전송받음
		else if(gameObj.getMessage().equals("[sendAllChaPos]") ){
			allChaPos = (Vector<Integer[]>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println( allChaPos.size());
			System.out.println( allChaPos.elementAt(0)[1]);
			System.out.println( allChaPos.elementAt(0)[2]);
			System.out.println("캐릭터 위치 적용 완료");
		}
		// 서버에서 모든 캐릭터의 체력을 전송받음 ( 인덱스, 현재체력 나중체력 )
		else if(gameObj.getMessage().equals("[sendAllChaHp]") ){
			allChaHp = (Vector<Integer[]>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println("캐릭터 체력 적용 완료");
		}
		// 누군가가 주먹공격을 하면
		else if(gameObj.getMessage().equals("[useAttack]") ){
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask(){public void run(){
				punchInfo = null;
			}};
			System.out.println("attack 메세지");
			punchInfo = (Vector<Integer>)gameObj.getData();
			
			timer.schedule(timerTask, 500);
			
		}
		
		
	}

}

