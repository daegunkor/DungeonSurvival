

import java.awt.Image;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.ImageIcon;


public class punchAttack implements Runnable{
	private Image 		punchImg = new ImageIcon("image/punch_1.png").getImage();
	boolean punchOnOff = false;
	String 	punchForward = "";
	int 	power = 10;
	
	int 	pos_x = 0;	
	int 	pos_y = 0;
	
	int		punchWidth = punchImg.getWidth(null);	// 주먹의 너비
	int		punchHeight = punchImg.getHeight(null);	// 주먹의 높이
	
	int 	punchLength = 30; // 캐릭터와의 거리 
	
	
	Vector<CharacterInfo> allChaInfoArr;
	int				myIndex;
	ObjectOutputStream oos;
	
	public void run(){
		while(true){
			if(punchOnOff == true){
				System.out.println(myIndex + "번 플레이어가 공격시전 ");
				for(int i = 0 ; i < allChaInfoArr.size(); i++){
					// 내가 아닌 플레이어와 주먹이 충돌 했다면
					if(i != myIndex && Crash(pos_x, pos_y, allChaInfoArr.elementAt(i).getXPos(),allChaInfoArr.elementAt(i).getYPos(),
							punchWidth, punchHeight, allChaInfoArr.elementAt(i).getWidth(),allChaInfoArr.elementAt(i).getHeight())){
						// 체력에서 power만큼 뺀다
						System.out.println(myIndex + "번 플레이어가 " + i + "플레이어 공격");
						allChaInfoArr.elementAt(i).setCurHp(allChaInfoArr.elementAt(i).getCurHp() - power);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else
				System.out.print("");
		}
	}
	
	public punchAttack(){
		new Thread(this).start();
	}
	
	// 캐릭터의 방향, 좌표에 따른 공격의 위치 설정
	// 주의 캐릭터의 위치는 왼쪽 상단
	public void setAttackPos(String chaForward, int chaPos_x, int chaPos_y, int chaWidth, int chaHeight){
		punchForward = chaForward;
		
		if(chaForward.equals("up")){
			pos_x = chaPos_x + (chaWidth/2 + punchWidth/2);
			pos_y = chaPos_y - (punchLength + punchHeight);
		} else if(chaForward.equals("down")){
			pos_x = chaPos_x + (chaWidth/2 - punchWidth/2);
			pos_y = chaPos_y + (chaHeight + punchLength);
		} else if(chaForward.equals("left")){
			pos_x = chaPos_x - (punchLength + punchHeight);
			pos_y = chaPos_y + (chaHeight/2 - punchWidth/2);
		} else if(chaForward.equals("right")){
			pos_x = chaPos_x + (chaWidth + punchLength);
			pos_y = chaPos_y + (chaHeight/2 - punchWidth/2);
		}
	}
	public void setAllChaInfo(int myIndex, Vector<CharacterInfo> allChaInfoArr){
		this.myIndex = myIndex;
		this.allChaInfoArr = allChaInfoArr;
	}
	public void setOnOff(boolean arg){
		punchOnOff = arg;
	}
	public int getPosX(){
		return pos_x;
	}
	public int getPosY(){
		return pos_y;
	}
	public String getForward(){
		return punchForward;
	}
	// 충돌판정
	public boolean Crash(int x1, int y1, int x2, int y2, int w1, int h1, int w2, int h2){
		boolean check = false;
		if ( Math.abs( ( x1 + w1 / 2 )  - ( x2 + w2 / 2 ))  <  ( w2 / 2 + w1 / 2 )  
		&& Math.abs( ( y1 + h1 / 2 )  - ( y2 + h2 / 2 ))  <  ( h2 / 2 + h1/ 2 ) ){
		check = true;
		}else{ check = false;}
		
		return check; 
	}



}
