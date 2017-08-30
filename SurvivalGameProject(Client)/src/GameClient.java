import java.awt.Frame;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//�缳�� ( OOS,OIS�� GAMECLient ���� �����, ��� �̺�Ʈ ó���� â ��ü�� ���� �����ϴ� �������� �Ѵ�.)
public class GameClient  extends Frame/* implements Runnable, ActionListener*/{
	Socket socket;
	
	
	public GameClient(){
		LoginWindow logWin = new LoginWindow();	// �α��� ȭ�� ��ü
		ReadyRoom rdRoom;						// ���� ��üa
		IntoGame	inGame;						// ���� ���� ��ü
		ObjectOutputStream oos;					//
		ObjectInputStream ois;					//
		MsgCatcher msgCatcher;
		
		while(logWin.getPerformed() == false){System.out.print("");}; // ��ư�� �������� ���� ��ٸ���.
		socket = logWin.getSocket();
		//������ ����� ��Ʈ���� �����ϰ�, logWin�� userName�� ������ �����Ѵ�.
		oos = logWin.getOOS();
		ois = logWin.getOIS();
		msgCatcher = new MsgCatcher(oos, ois);
		rdRoom = new ReadyRoom(socket,oos,ois,msgCatcher);	// ���� ����
	
		
		// ���� ���� ��ɱ��� ��ٸ���.A
		while(rdRoom.getIsOnGame() == false){System.out.print("");};
		
		inGame = new IntoGame(logWin.getOOS(),logWin.getOIS(),rdRoom.getChaClStr(),msgCatcher);
		
		
		
	}
	 

	public static void main(String args[]){
		System.out.println("rd ������");
		new GameClient();
		
	}
	
}
