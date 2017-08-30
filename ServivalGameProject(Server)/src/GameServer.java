
/*
 		방 번호가 1인 방 하나에서 모두가 준비, 채팅 후 게임시작
 */
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import SkillClass.Skill;
import SkillClass.FighterSkillClass.DashAttack;
import SkillClass.FighterSkillClass.MoveUp;
import SkillClass.FighterSkillClass.SurroundAttack;
import SkillClass.StrategistSkillClass.BlackHall;
import SkillClass.StrategistSkillClass.Cure;
import SkillClass.StrategistSkillClass.EnergyBall;
import SkillClass.TheifSkillClass.Accessinate;
import SkillClass.TheifSkillClass.ThrowDagger;
import SkillClass.TheifSkillClass.Trap;


// 상수 사용을 위한 NumOfDefine
public class GameServer implements NumOfDefine{
	private ServerSocket server;
	private BManager bMan=new BManager();   // 메시지 방송자

  
	void startServer(){                         // 서버를 실행한다.
		try {
			server=new ServerSocket(7777);
			System.out.println("서버소켓이 생성되었습니다.");
			while(true){
				Socket socket = 	server.accept();
				GameThread gt =		new GameThread(socket);
				gt.start();
				bMan.add(gt);
				System.out.println("접속자 수: "+bMan.size());
			}	
		}catch(Exception e){
			System.out.println(e);
		}
	}	

	public static void main(String[] args){
		GameServer server = new GameServer();
		server.startServer();
	}
	// 클라이언트와 통신하는 스레드 클래스

	class GameThread extends Thread{

		private String 	userName		= null;      				// 사용자 이름
		private Socket 	socket;              		    			// 소켓
		private CharacterInfo chaInfo = new CharacterInfo();		// 게임 캐릭터에대한 정보( 게임 시작 시 생성 )
		private Skill[] skillSet = new Skill[3]; 					// 스킬에 대한 정보 처리
		private punchAttack punch = new punchAttack();				// 주먹 공격에 대한 정보
		Vector<Integer> punchInfo;
    
		// 게임 준비 여부, true이면 게임을 시작할 준비가 되었음을 의미한다.
		private	boolean				isOnGame	= false;	// 게임에 접속해 있는가
		private OutputStream		os;						// 출력 스트림
		private ObjectOutputStream 	oos;					// 객체 출력 스트림
		private InputStream			is;   					// 입력 스트림
		private ObjectInputStream	ois;					// 객체 입력 스트림
		
		// 캐릭터 방향키 입력
		boolean[] upDownLeftRight = {false,false,false,false};
		

    
		GameThread(Socket socket){     // 생성자
			this.socket=socket;
		}
		Socket getSocket(){               // 소켓을 반환한다.
			return socket;
		}
		
		ObjectOutputStream getObjectOutputStream(){
			return oos;
		}

		String getUserName(){             // 사용자 이름을 반환한다.
			return userName;
		}

		boolean isOnGame(){                 // 게임을 진행중인가 반환.
			return isOnGame;
		}
		CharacterInfo getCharacterInfo(){
			return chaInfo;
		}
		void setSkillSetByClassName(){
			if( chaInfo.getChaClassName().equals("fighter") ){
				skillSet[0] = new MoveUp();
				skillSet[1] = new DashAttack();
				skillSet[2] = new SurroundAttack();
			} else if( chaInfo.getChaClassName().equals("strategist") ){
				skillSet[0] = new EnergyBall();
				skillSet[1] = new BlackHall();
				skillSet[2] = new Cure();
			} else if( chaInfo.getChaClassName().equals("strategist") ){
				skillSet[0] = new ThrowDagger();
				skillSet[1] = new Trap();
				skillSet[2] = new Accessinate();
			}
		}

		public void run(){
			try{
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				is = socket.getInputStream();
				ois = new ObjectInputStream(is);


				GameObject fromClientObj;                     // 클라이언트로 부터 전달 객체
        
				// 유저가 연결된 상태라면
				while((fromClientObj = (GameObject) ois.readObject()) != null){
		
					// msg가 "userName" 이면 
					if(fromClientObj.getMessage().equals("[userName]")){
						userName=(String)fromClientObj.getData();          // userName을 정한다.
					}
					// 클라이언트로부터 채팅 메소드를 보내면
					else if(fromClientObj.getMessage().equals("[chatToServer]") ){
						// 게임중이 아닌 플레이어에게 해당 메세지를 보낸다
						bMan.sendNotGame(new GameObject(IS_SERVER,"[chatFromServer]",
								getUserName() + " : " + (String)fromClientObj.getData()));
					}
					// 한명의 클라이언트로부터 플레이어 리스트 요청이 오면 == 접속을 하면
					else if(fromClientObj.getMessage().equals("[requestPlayerList]")) {
						
						// 모든 클라이언트에게 플레이어 리스트를 보낸다
						for(int i = 0; i < bMan.size(); i++)
						bMan.sendTo(i, new GameObject(IS_SERVER,"[sendPlayerList]",
								bMan.getAllPlayerList(i)));
					}
					// 클래스 명 지정( 클래스 지정 )
					else if(fromClientObj.getMessage().equals("[setMyClassName]")){
						chaInfo.setChaClassName((String)fromClientObj.getData());
					}
					// 자신이 게임중이라고 알리면
					else if(fromClientObj.getMessage().equals("[setMeOnGame]")){
						// 플레이어를 게임중으로 바꾼다.
						isOnGame = true;
						chaInfo.setIsOnGame(true);
					}
					// 플레이어가 캐릭터 정보 배열을 오쳥하면
					else if(fromClientObj.getMessage().equals("[giveMeChaInfoArr]")){
						// 플레이어에게 인덱스와 배열을 전송한다.
						oos.writeObject(new GameObject(bMan.getIndexOf(this),"[sendChaInfoArr]",bMan.getChaInfoArray()));
					}
					// 플레이어가 캐릭터 위치 배열을 요청하면
					else if(fromClientObj.getMessage().equals("[giveMeAllChaPos]")){
						// 캐릭터 위치 배열을 보낸다.
						oos.writeObject(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					// 플레이어가 캐릭터 체력 배열을 요청하면
					else if(fromClientObj.getMessage().equals("[giveMeAllChaHp]")){
						// 캐릭터 체력 배열을 보낸다
						bMan.sendChaHpToAll();
		
					}
					// 플레이어가 기본 공격을 요청하면 
					else if(fromClientObj.getMessage().equals("[punchAttack]")){
						/*// 주먹 활성 / 비활성화 타이머
						Timer timer = new Timer();
						// 시간 후 다시 주먹을 비활성화한다.
						TimerTask timerTask = new TimerTask(){public void run(){
							punch.setOnOff(false);
							bMan.sendChaHpToAll();
						}};
						
						punch.setOnOff(true);*/
						// 해당 플레이어의 펀치 정보를 보낸다.
						// 주먹의 위치를 설정 
						punch.setAttackPos(chaInfo.getChaForward(), chaInfo.getXPos(), chaInfo.getYPos(),
								chaInfo.getWidth(), chaInfo.getHeight());
						// 주먹의 공격 대상 설정
						punch.setAllChaInfo(bMan.getIndexOf(this), bMan.getChaInfoArray());
						System.out.println("punch set true");
						punchInfo = new Vector<Integer>();
						if(punch.getForward() == "up") punchInfo.add(0);
						else if (punch.getForward() == "down") punchInfo.add(1);
						else if (punch.getForward() == "left") punchInfo.add(2);
						else if (punch.getForward() == "right") punchInfo.add(3);
						
						punchInfo.add(punch.getPosX());
						punchInfo.add(punch.getPosY());
						bMan.sendOnGame(new GameObject(bMan.getIndexOf(this), "[useAttack]", punchInfo));
						/*// 펀치가 true일 경우에 자신이아닌 누군가가 충돌할 경우 체력 감소
						timer.schedule(timerTask, 1000);*/
					}
					
							
					
					
					
				
					
					//----------------------- 캐릭터 행동처리 -------------------------
					// 위 키를 눌렀을 경우
					else if(fromClientObj.getMessage().equals("[myChaMoveUp]")){
						chaInfo.move("up");
						bMan.sendOnGame(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					else if(fromClientObj.getMessage().equals("[myChaMoveDown]")){
						chaInfo.move("down");
						bMan.sendOnGame(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					else if(fromClientObj.getMessage().equals("[myChaMoveLeft]")){
						chaInfo.move("left");
						bMan.sendOnGame(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					else if(fromClientObj.getMessage().equals("[myChaMoveRight]")){
						chaInfo.move("right");
						bMan.sendOnGame(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					
					
					
					
					
					// 메세지확인
					System.out.println(getUserName() + " : " +fromClientObj.getMessage());
					
				}

			}catch(Exception e){
				e.printStackTrace();
			}finally{

				try{
        	
        	
					bMan.remove(this);
					if(ois!=null) ois.close();
					if(oos!=null) oos.close();
					if(socket!=null) socket.close();

					ois 	= 	null;
					oos 	= 	null;
					socket	=	null;

					System.out.println(userName+"님이 접속을 끊었습니다.");
					
					System.out.println("접속자 수: "+bMan.size());
					
					// 모든 클라이언트에게 플레이어 리스트를 보낸다
					for(int i = 0; i < bMan.size(); i++)
					bMan.sendTo(i, new GameObject(IS_SERVER,"[sendPlayerList]",
							bMan.getAllPlayerList(i)));

				}catch(Exception e){}
			}
		}
	
	}

	class BManager extends Vector{      	 // 게임 객체를 전달하는 클래스
		Vector<CharacterInfo> chaInfoArr = new Vector<CharacterInfo>();
		BManager(){}
    
		// ------------------------백터 조작 기본 메서드 --------------------------
		void add(GameThread gt){         		 // 스레드를 추가한다.
			super.add(gt);
		}
		
		
		void remove(GameThread gt){       		// 스레드를 제거한다.
			super.remove(gt);	
		}

		// ------------------------ 객체 정보, 소켓 관련 메서드 -------------------
		GameThread getOT(int i){            	// i번째 스레드를 반환한다.
			return (GameThread)elementAt(i);
		}

		Socket getSocket(int i){             	 // i번째 스레드의 소켓을 반환한다.
			return getOT(i).getSocket();
		}
		ObjectOutputStream get_i_ObjectOutputStream(int i){
			return getOT(i).getObjectOutputStream();
		}
		
		// ------------------------ 객체 송신 기본 메서드 ---------------------------
		// i번째 스레드의 클라이언트에게 게임 오브젝트를 전송
		void sendTo(int i, GameObject gameObj){
			try{
				ObjectOutputStream oos = 
						get_i_ObjectOutputStream(i);
				oos.writeObject(gameObj);
			}catch(Exception e){}  
		}
		
		// 해당 게임쓰래드의 인덱스를 추출한다.
				int getIndexOf(GameThread gt){
					for(int i = 0; i < this.size(); i++){
						if(gt == getOT(i)){
							return i;
						}
					}
					return 100;
				}
				
		// 게임을 실행중인 플레이어에게 게임 오브젝트를 보낸다.
		void sendOnGame(GameObject gameObj){
			for(int i = 0; i < size(); i++)
				if(getOT(i).isOnGame())
					sendTo(i, gameObj);
		}
		
		// 게임을 진행중이 아닌 플레이어게 게임 오브젝트를 보낸다.
		void sendNotGame(GameObject gameObj){
			for(int i = 0; i < size(); i++)
				if(getOT(i).isOnGame() == false)
					sendTo(i, gameObj);
		}
		
		//모든 플레이어게 메세지를 보낸다
		void sendToAll(GameObject gameObj){
			for(int i = 0; i <size();i++){
				sendTo(i, gameObj);
			}
		}
		
		// [플레이어명, 클래스, 게임 실행 여부]
		// 모든 플레이어의 리스트와 정보를 생성하되, 
		// 해당 플레이어 번호의의 클래스 열은 '[selectClass]'로 채워넣는다.
		Object[][] getAllPlayerList(int playerNum){
			Object[][] playerList = new Object[size()][];
			
			for(int i = 0 ; i < size(); i ++) {
				playerList[i] = new Object[3];
				playerList[i][0] = getOT(i).getUserName();
				
					playerList[i][1] = getOT(i).getCharacterInfo().getChaClassName();
				
				// 해당 플레이어가 게임 중일 경우
				if(getOT(i).isOnGame())
					playerList[i][2] = "게임 중";
				else
					playerList[i][2] = "대기실";
			}
			// 반환
			return playerList;
		}
		// 게임에 들어가있는 모든 캐릭터의 위치정보를 얻는다
		// 단, 게임에 들어가있는 플레이어의 순서는 실제 접속 플레이어의 순서와 다르므로,
		// chaPos[0]에 접속 플레이어 인덱스를 넣어준다.
		// chaPos[1] : x_pos, chaPos[2] : y_pos
		// chaPos[3] : 0 파이터, 1 전략가, 2 도둑
		// chaPos[4] : 0 up 1 down 2 left 4 right
		Vector<Integer[]> getAllChaPos(){
			Vector<Integer[]> allChaPos = new Vector<Integer[]>();
			Integer[] chaPos;
			for(int i = 0; i < bMan.size(); i++){
				if(bMan.getOT(i).isOnGame == true){
					chaPos = new Integer[5];
					chaPos[0] = i;										// 해당 플레이어의 인덱스
					chaPos[1] = getOT(i).getCharacterInfo().getXPos();
					chaPos[2] = getOT(i).getCharacterInfo().getYPos();
					
					if(getOT(i).getCharacterInfo().getChaClassName().equals("fighter"))
						chaPos[3] = 0;
					else if(getOT(i).getCharacterInfo().getChaClassName().equals("strategist"))
						chaPos[3] = 1;
					else if(getOT(i).getCharacterInfo().getChaClassName().equals("theif"))
						chaPos[3] = 2;
					
					if(getOT(i).getCharacterInfo().getChaForward().equals("up"))
						chaPos[4] = 0;
					else if(getOT(i).getCharacterInfo().getChaForward().equals("down"))
						chaPos[4] = 1;
					else if(getOT(i).getCharacterInfo().getChaForward().equals("left"))
						chaPos[4] = 2;
					else if(getOT(i).getCharacterInfo().getChaForward().equals("right"))
						chaPos[4] = 3;
					
					allChaPos.add(chaPos);
				}
						
			}
			return allChaPos;
		}
		
		// 게임에 들어가있는 모든 캐릭터의 위치정보를 얻는다
				// 단, 게임에 들어가있는 플레이어의 순서는 실제 접속 플레이어의 순서와 다르므로,
				// chaPos[0]에 접속 플레이어 인덱스를 넣어준다.
				// chaPos[1] : 현재 HP
				// chaPos[2] : 최대 HP
				Vector<Integer[]> getAllChaHp(){
					Vector<Integer[]> allChaHp = new Vector<Integer[]>();
					Integer[] chaHp;
					for(int i = 0; i < bMan.size(); i++){
						if(bMan.getOT(i).isOnGame == true){
							chaHp = new Integer[3];
							chaHp[0] = i;
							chaHp[1] = getOT(i).getCharacterInfo().getCurHp();
							chaHp[2] = getOT(i).getCharacterInfo().getMaxHp();
							allChaHp.add(chaHp);
						}
					}
						
					return allChaHp;
				}
	

		// 모든 게임중 플레이어에게 모든플레이어의 캐릭터 정보배열과 플레이어의 해당 숫자를 보낸다.
		void sendChaInfoToAll(){
			for(int i = 0; i < size(); i++)// 주의! 해당 클라이언트측에서는 오브젝트의 ongame true 만 적용
				if(getOT(i).isOnGame() == true)
					sendChaInfo(i);		
		}
		void sendChaHpToAll(){
			for(int i = 0; i < size(); i ++)
			{
				if(getOT(i).isOnGame() == true)
					sendTo(i, new GameObject(i, "[sendAllChaHp]", getAllChaHp()));
			}
		}
		// i번 쓰래드의 플레이어에게 해당 플레이어의 인덱스번호와 모든 플레이어의 캐릭터 정보배열 전송
		void sendChaInfo(int i){
			sendTo(i, new GameObject(i, "[sendChaInfoArr]", getChaInfoArray()));
		}
		
		
		// 모든 플레이어의 캐릭터 정보배열을 가져온다.
		Vector<CharacterInfo> getChaInfoArray(){
			chaInfoArr.removeAllElements();
			for(int i = 0; i < this.size(); i++){
				chaInfoArr.add(getOT(i).getCharacterInfo());
			}
			return chaInfoArr;
		}
		
		

	}
}
