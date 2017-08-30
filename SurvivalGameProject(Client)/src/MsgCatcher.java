import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// �޼����� ��� ������
public class MsgCatcher implements Runnable{

	private ObjectOutputStream 	oos;
	private ObjectInputStream 	ois;
	
	private GameObject fromServerObj;
	private String[][]	acceptableMsg = {
			// ReadyRoom �޼���
			{"[chatFromServer]","readyRoom"},
			{"[sendPlayerList]","readyRoom"},
			
			// IntoGame �޼���
			{"[sendChaInfoArr]","intoGame"},
			{"[sendAllChaPos]","intoGame"},
			{"[sendAllChaHp]","intoGame"},
			{"[useAttack]","intoGame"}
			
	};
	
	private String		sendCl = "";	// acceptableMsg �� �ش��ϴ� ���, true�� 
	private boolean 	heardMe = false;	// IntoGame Ŭ�������� �޾������ Ȯ�� ��
	private int			msgTestCount = 0;
	public void run() {
		try { 
			// �����κ��� �ż����� �޴´�
			while((fromServerObj = (GameObject) ois.readObject() ) != null){
				for(int i = 0; i < acceptableMsg.length; i++){
					if(fromServerObj.getMessage().equals(acceptableMsg[i][0])){	// �ش��ϴ� �޼����� �ִٸ�
						sendCl = acceptableMsg[i][1];	// �޼��� ��� ����
						
						while(heardMe == false){		// ��� Ŭ�������� ������ ��ٸ���
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
	
	// ���� ��ü�� �����Ѵ�
	public GameObject getServerObj(){
		return fromServerObj;
	}
	
	// intoGame���κ��� ������ Ȯ���Ѵ�.
	public void setHeardMe(boolean arg){
		heardMe = arg;
	}
	
	// �޼��� ĳ���� ���¸� Ȯ���Ѵ�.
	public String getSendCl(){
		return sendCl;
	}
	
	// ��ȣ�� �޾Ҵ��� Ȯ��
	public boolean getHeardMe(){
		return heardMe;
	}

}

