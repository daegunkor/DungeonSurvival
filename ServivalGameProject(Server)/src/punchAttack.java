

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
	
	int		punchWidth = punchImg.getWidth(null);	// �ָ��� �ʺ�
	int		punchHeight = punchImg.getHeight(null);	// �ָ��� ����
	
	int 	punchLength = 30; // ĳ���Ϳ��� �Ÿ� 
	
	
	Vector<CharacterInfo> allChaInfoArr;
	int				myIndex;
	ObjectOutputStream oos;
	
	public void run(){
		while(true){
			if(punchOnOff == true){
				System.out.println(myIndex + "�� �÷��̾ ���ݽ��� ");
				for(int i = 0 ; i < allChaInfoArr.size(); i++){
					// ���� �ƴ� �÷��̾�� �ָ��� �浹 �ߴٸ�
					if(i != myIndex && Crash(pos_x, pos_y, allChaInfoArr.elementAt(i).getXPos(),allChaInfoArr.elementAt(i).getYPos(),
							punchWidth, punchHeight, allChaInfoArr.elementAt(i).getWidth(),allChaInfoArr.elementAt(i).getHeight())){
						// ü�¿��� power��ŭ ����
						System.out.println(myIndex + "�� �÷��̾ " + i + "�÷��̾� ����");
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
	
	// ĳ������ ����, ��ǥ�� ���� ������ ��ġ ����
	// ���� ĳ������ ��ġ�� ���� ���
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
	// �浹����
	public boolean Crash(int x1, int y1, int x2, int y2, int w1, int h1, int w2, int h2){
		boolean check = false;
		if ( Math.abs( ( x1 + w1 / 2 )  - ( x2 + w2 / 2 ))  <  ( w2 / 2 + w1 / 2 )  
		&& Math.abs( ( y1 + h1 / 2 )  - ( y2 + h2 / 2 ))  <  ( h2 / 2 + h1/ 2 ) ){
		check = true;
		}else{ check = false;}
		
		return check; 
	}



}
