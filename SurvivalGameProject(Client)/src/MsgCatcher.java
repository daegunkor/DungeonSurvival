import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// 메세지를 잡는 쓰래드
public class MsgCatcher implements Runnable{

	private ObjectOutputStream 	oos;
	private ObjectInputStream 	ois;
	
	private GameObject fromServerObj;
	private String[][]	acceptableMsg = {
			// ReadyRoom 메세지
			{"[chatFromServer]","readyRoom"},
			{"[sendPlayerList]","readyRoom"},
			
			// IntoGame 메세지
			{"[sendChaInfoArr]","intoGame"},
			{"[sendAllChaPos]","intoGame"},
			{"[sendAllChaHp]","intoGame"},
			{"[useAttack]","intoGame"}
			
	};
	
	private String		sendCl = "";	// acceptableMsg 에 해당하는 경우, true값 
	private boolean 	heardMe = false;	// IntoGame 클래스에서 받았을경우 확인 값
	private int			msgTestCount = 0;
	public void run() {
		try { 
			// 서버로부터 매세지를 받는다
			while((fromServerObj = (GameObject) ois.readObject() ) != null){
				for(int i = 0; i < acceptableMsg.length; i++){
					if(fromServerObj.getMessage().equals(acceptableMsg[i][0])){	// 해당하는 메세지가 있다면
						sendCl = acceptableMsg[i][1];	// 메세지 대상 선정
						
						while(heardMe == false){		// 대상 클래스에서 응답을 기다린다
							if(msgTestCount == 0){
								System.out.println("waiting"+ fromServerObj.getMessage() + sendCl);
								msgTestCount++;
							}
							else
								System.out.print("");
						}
						heardMe = false;	
						sendCl = "";
					
					}
				}
			}
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public MsgCatcher(ObjectOutputStream oos,ObjectInputStream ois){
		this.oos = oos;
		this.ois = ois;
		
		new Thread(this).start();
	}
	
	// 받은 객체를 리턴한다
	public GameObject getServerObj(){
		return fromServerObj;
	}
	
	// intoGame으로부터 응답을 확인한다.
	public void setHeardMe(boolean arg){
		heardMe = arg;
	}
	
	// 메세지 캐쳐의 상태를 확인한다.
	public String getSendCl(){
		return sendCl;
	}
	
	// 신호를 받았는지 확인
	public boolean getHeardMe(){
		return heardMe;
	}

}

