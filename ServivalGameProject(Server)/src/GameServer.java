
/*
 		�� ��ȣ�� 1�� �� �ϳ����� ��ΰ� �غ�, ä�� �� ���ӽ���
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


// ��� ����� ���� NumOfDefine
public class GameServer implements NumOfDefine{
	private ServerSocket server;
	private BManager bMan=new BManager();   // �޽��� �����

  
	void startServer(){                         // ������ �����Ѵ�.
		try {
			server=new ServerSocket(7777);
			System.out.println("���������� �����Ǿ����ϴ�.");
			while(true){
				Socket socket = 	server.accept();
				GameThread gt =		new GameThread(socket);
				gt.start();
				bMan.add(gt);
				System.out.println("������ ��: "+bMan.size());
			}	
		}catch(Exception e){
			System.out.println(e);
		}
	}	

	public static void main(String[] args){
		GameServer server = new GameServer();
		server.startServer();
	}
	// Ŭ���̾�Ʈ�� ����ϴ� ������ Ŭ����

	class GameThread extends Thread{

		private String 	userName		= null;      				// ����� �̸�
		private Socket 	socket;              		    			// ����
		private CharacterInfo chaInfo = new CharacterInfo();		// ���� ĳ���Ϳ����� ����( ���� ���� �� ���� )
		private Skill[] skillSet = new Skill[3]; 					// ��ų�� ���� ���� ó��
		private punchAttack punch = new punchAttack();				// �ָ� ���ݿ� ���� ����
		Vector<Integer> punchInfo;
    
		// ���� �غ� ����, true�̸� ������ ������ �غ� �Ǿ����� �ǹ��Ѵ�.
		private	boolean				isOnGame	= false;	// ���ӿ� ������ �ִ°�
		private OutputStream		os;						// ��� ��Ʈ��
		private ObjectOutputStream 	oos;					// ��ü ��� ��Ʈ��
		private InputStream			is;   					// �Է� ��Ʈ��
		private ObjectInputStream	ois;					// ��ü �Է� ��Ʈ��
		
		// ĳ���� ����Ű �Է�
		boolean[] upDownLeftRight = {false,false,false,false};
		

    
		GameThread(Socket socket){     // ������
			this.socket=socket;
		}
		Socket getSocket(){               // ������ ��ȯ�Ѵ�.
			return socket;
		}
		
		ObjectOutputStream getObjectOutputStream(){
			return oos;
		}

		String getUserName(){             // ����� �̸��� ��ȯ�Ѵ�.
			return userName;
		}

		boolean isOnGame(){                 // ������ �������ΰ� ��ȯ.
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


				GameObject fromClientObj;                     // Ŭ���̾�Ʈ�� ���� ���� ��ü
        
				// ������ ����� ���¶��
				while((fromClientObj = (GameObject) ois.readObject()) != null){
		
					// msg�� "userName" �̸� 
					if(fromClientObj.getMessage().equals("[userName]")){
						userName=(String)fromClientObj.getData();          // userName�� ���Ѵ�.
					}
					// Ŭ���̾�Ʈ�κ��� ä�� �޼ҵ带 ������
					else if(fromClientObj.getMessage().equals("[chatToServer]") ){
						// �������� �ƴ� �÷��̾�� �ش� �޼����� ������
						bMan.sendNotGame(new GameObject(IS_SERVER,"[chatFromServer]",
								getUserName() + " : " + (String)fromClientObj.getData()));
					}
					// �Ѹ��� Ŭ���̾�Ʈ�κ��� �÷��̾� ����Ʈ ��û�� ���� == ������ �ϸ�
					else if(fromClientObj.getMessage().equals("[requestPlayerList]")) {
						
						// ��� Ŭ���̾�Ʈ���� �÷��̾� ����Ʈ�� ������
						for(int i = 0; i < bMan.size(); i++)
						bMan.sendTo(i, new GameObject(IS_SERVER,"[sendPlayerList]",
								bMan.getAllPlayerList(i)));
					}
					// Ŭ���� �� ����( Ŭ���� ���� )
					else if(fromClientObj.getMessage().equals("[setMyClassName]")){
						chaInfo.setChaClassName((String)fromClientObj.getData());
					}
					// �ڽ��� �������̶�� �˸���
					else if(fromClientObj.getMessage().equals("[setMeOnGame]")){
						// �÷��̾ ���������� �ٲ۴�.
						isOnGame = true;
						chaInfo.setIsOnGame(true);
					}
					// �÷��̾ ĳ���� ���� �迭�� �����ϸ�
					else if(fromClientObj.getMessage().equals("[giveMeChaInfoArr]")){
						// �÷��̾�� �ε����� �迭�� �����Ѵ�.
						oos.writeObject(new GameObject(bMan.getIndexOf(this),"[sendChaInfoArr]",bMan.getChaInfoArray()));
					}
					// �÷��̾ ĳ���� ��ġ �迭�� ��û�ϸ�
					else if(fromClientObj.getMessage().equals("[giveMeAllChaPos]")){
						// ĳ���� ��ġ �迭�� ������.
						oos.writeObject(new GameObject(bMan.getIndexOf(this),"[sendAllChaPos]",bMan.getAllChaPos()));
					}
					// �÷��̾ ĳ���� ü�� �迭�� ��û�ϸ�
					else if(fromClientObj.getMessage().equals("[giveMeAllChaHp]")){
						// ĳ���� ü�� �迭�� ������
						bMan.sendChaHpToAll();
		
					}
					// �÷��̾ �⺻ ������ ��û�ϸ� 
					else if(fromClientObj.getMessage().equals("[punchAttack]")){
						/*// �ָ� Ȱ�� / ��Ȱ��ȭ Ÿ�̸�
						Timer timer = new Timer();
						// �ð� �� �ٽ� �ָ��� ��Ȱ��ȭ�Ѵ�.
						TimerTask timerTask = new TimerTask(){public void run(){
							punch.setOnOff(false);
							bMan.sendChaHpToAll();
						}};
						
						punch.setOnOff(true);*/
						// �ش� �÷��̾��� ��ġ ������ ������.
						// �ָ��� ��ġ�� ���� 
						punch.setAttackPos(chaInfo.getChaForward(), chaInfo.getXPos(), chaInfo.getYPos(),
								chaInfo.getWidth(), chaInfo.getHeight());
						// �ָ��� ���� ��� ����
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
						/*// ��ġ�� true�� ��쿡 �ڽ��̾ƴ� �������� �浹�� ��� ü�� ����
						timer.schedule(timerTask, 1000);*/
					}
					
							
					
					
					
				
					
					//----------------------- ĳ���� �ൿó�� -------------------------
					// �� Ű�� ������ ���
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
					
					
					
					
					
					// �޼���Ȯ��
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

					System.out.println(userName+"���� ������ �������ϴ�.");
					
					System.out.println("������ ��: "+bMan.size());
					
					// ��� Ŭ���̾�Ʈ���� �÷��̾� ����Ʈ�� ������
					for(int i = 0; i < bMan.size(); i++)
					bMan.sendTo(i, new GameObject(IS_SERVER,"[sendPlayerList]",
							bMan.getAllPlayerList(i)));

				}catch(Exception e){}
			}
		}
	
	}

	class BManager extends Vector{      	 // ���� ��ü�� �����ϴ� Ŭ����
		Vector<CharacterInfo> chaInfoArr = new Vector<CharacterInfo>();
		BManager(){}
    
		// ------------------------���� ���� �⺻ �޼��� --------------------------
		void add(GameThread gt){         		 // �����带 �߰��Ѵ�.
			super.add(gt);
		}
		
		
		void remove(GameThread gt){       		// �����带 �����Ѵ�.
			super.remove(gt);	
		}

		// ------------------------ ��ü ����, ���� ���� �޼��� -------------------
		GameThread getOT(int i){            	// i��° �����带 ��ȯ�Ѵ�.
			return (GameThread)elementAt(i);
		}

		Socket getSocket(int i){             	 // i��° �������� ������ ��ȯ�Ѵ�.
			return getOT(i).getSocket();
		}
		ObjectOutputStream get_i_ObjectOutputStream(int i){
			return getOT(i).getObjectOutputStream();
		}
		
		// ------------------------ ��ü �۽� �⺻ �޼��� ---------------------------
		// i��° �������� Ŭ���̾�Ʈ���� ���� ������Ʈ�� ����
		void sendTo(int i, GameObject gameObj){
			try{
				ObjectOutputStream oos = 
						get_i_ObjectOutputStream(i);
				oos.writeObject(gameObj);
			}catch(Exception e){}  
		}
		
		// �ش� ���Ӿ������� �ε����� �����Ѵ�.
				int getIndexOf(GameThread gt){
					for(int i = 0; i < this.size(); i++){
						if(gt == getOT(i)){
							return i;
						}
					}
					return 100;
				}
				
		// ������ �������� �÷��̾�� ���� ������Ʈ�� ������.
		void sendOnGame(GameObject gameObj){
			for(int i = 0; i < size(); i++)
				if(getOT(i).isOnGame())
					sendTo(i, gameObj);
		}
		
		// ������ �������� �ƴ� �÷��̾�� ���� ������Ʈ�� ������.
		void sendNotGame(GameObject gameObj){
			for(int i = 0; i < size(); i++)
				if(getOT(i).isOnGame() == false)
					sendTo(i, gameObj);
		}
		
		//��� �÷��̾�� �޼����� ������
		void sendToAll(GameObject gameObj){
			for(int i = 0; i <size();i++){
				sendTo(i, gameObj);
			}
		}
		
		// [�÷��̾��, Ŭ����, ���� ���� ����]
		// ��� �÷��̾��� ����Ʈ�� ������ �����ϵ�, 
		// �ش� �÷��̾� ��ȣ���� Ŭ���� ���� '[selectClass]'�� ä���ִ´�.
		Object[][] getAllPlayerList(int playerNum){
			Object[][] playerList = new Object[size()][];
			
			for(int i = 0 ; i < size(); i ++) {
				playerList[i] = new Object[3];
				playerList[i][0] = getOT(i).getUserName();
				
					playerList[i][1] = getOT(i).getCharacterInfo().getChaClassName();
				
				// �ش� �÷��̾ ���� ���� ���
				if(getOT(i).isOnGame())
					playerList[i][2] = "���� ��";
				else
					playerList[i][2] = "����";
			}
			// ��ȯ
			return playerList;
		}
		// ���ӿ� ���ִ� ��� ĳ������ ��ġ������ ��´�
		// ��, ���ӿ� ���ִ� �÷��̾��� ������ ���� ���� �÷��̾��� ������ �ٸ��Ƿ�,
		// chaPos[0]�� ���� �÷��̾� �ε����� �־��ش�.
		// chaPos[1] : x_pos, chaPos[2] : y_pos
		// chaPos[3] : 0 ������, 1 ������, 2 ����
		// chaPos[4] : 0 up 1 down 2 left 4 right
		Vector<Integer[]> getAllChaPos(){
			Vector<Integer[]> allChaPos = new Vector<Integer[]>();
			Integer[] chaPos;
			for(int i = 0; i < bMan.size(); i++){
				if(bMan.getOT(i).isOnGame == true){
					chaPos = new Integer[5];
					chaPos[0] = i;										// �ش� �÷��̾��� �ε���
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
		
		// ���ӿ� ���ִ� ��� ĳ������ ��ġ������ ��´�
				// ��, ���ӿ� ���ִ� �÷��̾��� ������ ���� ���� �÷��̾��� ������ �ٸ��Ƿ�,
				// chaPos[0]�� ���� �÷��̾� �ε����� �־��ش�.
				// chaPos[1] : ���� HP
				// chaPos[2] : �ִ� HP
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
	

		// ��� ������ �÷��̾�� ����÷��̾��� ĳ���� �����迭�� �÷��̾��� �ش� ���ڸ� ������.
		void sendChaInfoToAll(){
			for(int i = 0; i < size(); i++)// ����! �ش� Ŭ���̾�Ʈ�������� ������Ʈ�� ongame true �� ����
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
		// i�� �������� �÷��̾�� �ش� �÷��̾��� �ε�����ȣ�� ��� �÷��̾��� ĳ���� �����迭 ����
		void sendChaInfo(int i){
			sendTo(i, new GameObject(i, "[sendChaInfoArr]", getChaInfoArray()));
		}
		
		
		// ��� �÷��̾��� ĳ���� �����迭�� �����´�.
		Vector<CharacterInfo> getChaInfoArray(){
			chaInfoArr.removeAllElements();
			for(int i = 0; i < this.size(); i++){
				chaInfoArr.add(getOT(i).getCharacterInfo());
			}
			return chaInfoArr;
		}
		
		

	}
}
