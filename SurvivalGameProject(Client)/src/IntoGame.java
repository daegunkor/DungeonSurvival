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

// ���� ���� �޼���
// Ű �Է¿����� ������ �޼����� ������, 
// msgCatcher�� ������ �޼����� ������ ���� ĳ���� ������ ���� 

public class IntoGame extends JFrame implements KeyListener,Runnable{
	
	//--------------------------- ������ ���� ----------------
	// ��ü �ǳ�
	private Container ct;
	// ���� ���� �ǳ�
	private JPanel	mainGamePanel;
	private JLabel 	gameLabel;
	// ��ų �ǳ�
	private JPanel	skillPanel;
	private JLabel	skillLabel;
	
	// ����� ��Ʈ��
	private ObjectOutputStream  oos;
	private ObjectInputStream  ois;
	
	// ��ġ�� ����
	Dimension screen;
	
	// �̹����� �ҷ��������� ��Ŷ
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	// ȭ�� ���� ���� ũ��
		int f_width = 1000;
		int f_height = 1000;
	// ĳ���� �̹��� ����
	Image chaImg;
	// �ָ� �̹��� ����
	Image punchImg;
	// ��� �̹��� ����
	Image backGroundImg;
	// ���� ���۸��� �̹��� ����
	Image buffImage;
	// ���� ���۸��� �׷��� ����
	Graphics buffg;
	// ĳ���� ü�¹� �׸���
	Graphics hpBarg;
	// ü�¹� ����ũ��
	int hpBarHeight= 10;
	// ü�¹� ����ũ��
	int hpBarWidth = 50;
	
	// ----------------------------- ���� ���� ���� -----------------------
	// �����κ��� �޼����� �޴� ������
	MsgCatcher msgCatcher;
	// ĳ���� ���� ��ü
	CharacterInfo chaInfo;
	// ����ڵ��� ���� ��ü
	Vector<CharacterInfo> chaInfoArr = null;
	// ���� ��ü
	Vector<Integer> punchInfo;
	// ����ڵ��� ���� ��ü���� �ڽ��� ���� �ε���
	int				myChaIndex;
	// ���ӿ� �����ϴ� ĳ���� ��ġ ����
	Vector<Integer[]> allChaPos;
	Vector<Integer[]> allChaHp ; 
	// ���� ������Ʈ
	GameObject fromServerObj;
	// Ű���� �Է� ó���� ���� ����
	boolean keyUp 		= false;
	boolean keyDown 	= false;
	boolean keyLeft 	= false;
	boolean keyRight 	= false;
	boolean keySpace	= false;
	boolean[] upDownLeftRight = {false,false,false,false};
	
	// ������ ����
	public void run(){
		try{
			while(true){
				// �޼��� ���ķκ��� �޼����� ������� 
				if(msgCatcher.getSendCl().equals("intoGame") && msgCatcher.getHeardMe() == false){
					fromServerObj = msgCatcher.getServerObj();	// �޼����� ���� ��
					msgCatcher.setHeardMe(true);					// �޾Ҵٴ� ������ ������.
					applyGameObj(fromServerObj);					// �ش� ��ü�� �����Ѵ�.
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
	
	// ������ ����
	public IntoGame(ObjectOutputStream oos, ObjectInputStream ois, String chaClassStr, MsgCatcher msgCa){
		
		
		chaInfo = new CharacterInfo(chaClassStr);
		chaImg = tk.getImage("image/testChaImg.png");
		
		backGroundImg = new ImageIcon("image/backGroundImg.png").getImage();
		// ����� ��Ʈ�� ����
		this.oos = oos;
		this.ois = ois;
		
		// �����̳� �ʱ�ȭ
		setSize(f_width,f_height);
		
		// ��ġ�� ����(�ػ�)
		screen = tk.getScreenSize();
		
		
		/*ct = getContentPane();
		ct.setLayout(new BorderLayout(10,10));
		
		
		// ���� ���� �ǳ� ����
		mainGamePanel = new JPanel();
		gameLabel = new JLabel(new ImageIcon("image/gameBackGround.png"));
		
		mainGamePanel.add(gameLabel);
		
		// ��ų �ǳ� ����
		skillPanel = new JPanel();
		skillLabel = new JLabel(new ImageIcon("image/skillLabel.png"));
				
		skillPanel.add(skillLabel);
		
		// �����̳ʿ� �ǳ� �߰�
		ct.add(mainGamePanel, BorderLayout.CENTER);
		ct.add(skillPanel, BorderLayout.SOUTH);*/
		this.setLocationRelativeTo(null);
		setVisible(true);
		
		
		// �ʱ����� ��û �� �Ϸ���� ���
		try { 
			oos.writeObject(new GameObject(0, "[giveMeChaInfoArr]", null));
			oos.writeObject(new GameObject(0, "[giveMeAllChaPos]", null));
			oos.writeObject(new GameObject(0, "[giveMeAllChaHp]", null));
		} catch (Exception e) {e.printStackTrace();}
		
		

		System.out.println("ĳ���� �迭 ȣ�� �Ϸ�");
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
	// Ű���带 ������ �� �̺�Ʈ
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
	// Ű���带 ������ ���������� �̺�Ʈ
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
	// �Է� Ű ���� ���� �̺�Ʈ �߻�
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
	// Ű���� Ÿ���� �̺�Ʈ
	public void keyTyped(KeyEvent e) {
	}
	public void paint(Graphics g){
		// ���� ���۸��� ����ũ�� = ȭ��ũ��
		buffImage = createImage(f_width, f_height);
		
		// ������ �׷��� ��ü
		buffg = buffImage.getGraphics();
		
		update(g);
	}
	
	public void update(Graphics g){
		drawChar();
		
		// ȭ�鿡 ������ �׸��� �׸���
		g.drawImage(buffImage, 0, 0, this);
	}
	public void drawChar(){
		drawBackGround();
		drawAllChaPos();
		drawAllChaHp();
		if(punchInfo != null)
			drawPunch(punchInfo);
	}
	// ����� �׸���
	public void drawBackGround(){	
		buffg.clearRect(0, 0, f_width, f_height);
		buffg.drawImage(backGroundImg, 0, 0, this);
	}
	// ��� ĳ������ ��ġ�� �׸���
	public void drawAllChaPos(){
		for(int i = 0; i < allChaPos.size(); i++){
			setChaImg(allChaPos.elementAt(i)[3],allChaPos.elementAt(i)[4]);
			buffg.drawImage(chaImg, allChaPos.elementAt(i)[1], allChaPos.elementAt(i)[2], this);
		}
	}
	// ��� ĳ������ ü���� hpBarg�� ��� buffg �� �׸���
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
	// Ŭ���� ����, ĳ���͹��⿡ ���� �̹��� ���� 
	public void setChaImg(int chaClass, int chaForward){
		String imgSrc = "image/defaultChaImg.png";
		switch(chaClass){
		case 0: // ������ �� ���
			switch(chaForward){
			case 0:	// �� �ü�
				imgSrc = "image/fighterUp.png";
				break;
			case 1:	// �Ʒ� �ü�
				imgSrc = "image/fighterDown.png";
				break;
			case 2:	// ���� �ü�
				imgSrc = "image/fighterLeft.png";
				break;
			case 3:	// ������ �ü�
				imgSrc = "image/fighterRight.png";
				break;
			}
			break;
		case 1:	// ������ �� ���
			switch(chaForward){
			case 0:	// �� �ü�
				imgSrc = "image/strategistUp.png";
				break;
			case 1:	// �Ʒ� �ü�
				imgSrc = "image/strategistDown.png";
				break;
			case 2:	// ���� �ü�
				imgSrc = "image/strategistLeft.png";
				break;
			case 3:	// ������ �ü�
				imgSrc = "image/strategistRight.png";
				break;
			}
			break;
		case 2:	// ������ ���
			switch(chaForward){
			case 0:	// �� �ü�
				imgSrc = "image/theifUp.png";
				break;
			case 1:	// �Ʒ� �ü�
				imgSrc = "image/theifDown.png";
				break;
			case 2:	// ���� �ü�
				imgSrc = "image/theifLeft.png";
				break;
			case 3:	// ������ �ü�
				imgSrc = "image/theifRight.png";
				break;
			}
			break;
		}
		chaImg = tk.getImage(imgSrc);
	}
	// ���� ������Ʈ�� ���� ó�� �޼���
	public void applyGameObj(GameObject gameObj){
		
		// �������� ��� ĳ���� �迭�� ���۹���
		if(gameObj.getMessage().equals("[sendChaInfoArr]") ){
			chaInfoArr = (Vector<CharacterInfo>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println("ĳ���� �迭 ���� �Ϸ�");
			for(int i = 0 ; i < this.chaInfoArr.size(); i ++){
				System.out.println(this.chaInfoArr.elementAt(i).getXPos()+", "+this.chaInfoArr.elementAt(i).getYPos());
				System.out.println(this.chaInfoArr.elementAt(i).getChaClassName());
			}
		}
		// �������� ��� ĳ������ ��ġ�� ���۹���
		else if(gameObj.getMessage().equals("[sendAllChaPos]") ){
			allChaPos = (Vector<Integer[]>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println( allChaPos.size());
			System.out.println( allChaPos.elementAt(0)[1]);
			System.out.println( allChaPos.elementAt(0)[2]);
			System.out.println("ĳ���� ��ġ ���� �Ϸ�");
		}
		// �������� ��� ĳ������ ü���� ���۹��� ( �ε���, ����ü�� ����ü�� )
		else if(gameObj.getMessage().equals("[sendAllChaHp]") ){
			allChaHp = (Vector<Integer[]>) gameObj.getData();
			myChaIndex = gameObj.getUserCode();
			System.out.println("ĳ���� ü�� ���� �Ϸ�");
		}
		// �������� �ָ԰����� �ϸ�
		else if(gameObj.getMessage().equals("[useAttack]") ){
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask(){public void run(){
				punchInfo = null;
			}};
			System.out.println("attack �޼���");
			punchInfo = (Vector<Integer>)gameObj.getData();
			
			timer.schedule(timerTask, 500);
			
		}
		
		
	}

}

