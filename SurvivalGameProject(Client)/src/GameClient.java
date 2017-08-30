import java.awt.Frame;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//재설계 ( OOS,OIS를 GAMECLient 에서 만들고, 모든 이벤트 처리를 창 객체에 값을 전달하는 형식으로 한다.)
public class GameClient  extends Frame/* implements Runnable, ActionListener*/{
	Socket socket;
	
	
	public GameClient(){
		LoginWindow logWin = new LoginWindow();	// 로그인 화면 객체
		ReadyRoom rdRoom;						// 대기방 객체a
		IntoGame	inGame;						// 게임 실행 객체
		ObjectOutputStream oos;					//
		ObjectInputStream ois;					//
		MsgCatcher msgCatcher;
		
		while(logWin.getPerformed() == false){System.out.print("");}; // 버튼이 눌려질때 까지 기다린다.
		socket = logWin.getSocket();
		//가져와 입출력 스트림을 생성하고, logWin의 userName을 서버로 전송한다.
		oos = logWin.getOOS();
		ois = logWin.getOIS();
		msgCatcher = new MsgCatcher(oos, ois);
		rdRoom = new ReadyRoom(socket,oos,ois,msgCatcher);	// 대기방 입장
	
		
		// 게임 실행 명령까지 기다린다.A
		while(rdRoom.getIsOnGame() == false){System.out.print("");};
		
		inGame = new IntoGame(logWin.getOOS(),logWin.getOIS(),rdRoom.getChaClStr(),msgCatcher);
		
		
		
	}
	 

	public static void main(String args[]){
		System.out.println("rd 실행전");
		new GameClient();
		
	}
	
}
