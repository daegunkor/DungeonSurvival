import java.io.Serializable;

import ChaClass.ChaClass;
import ChaClass.FighterClass;
import ChaClass.StrategistClass;
import ChaClass.TheifClass;

public class CharacterInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean 	isOnGame = false;		// ���ӿ� ���ִ°� 
	private int			maxHealthPoint;			// ü�� �ִ�ġ[Ŭ������ ���� �ٸ�]
	private int 		curHealthPoint;			// ���� ü��
	
	private ChaClass	chaClass;				// ĳ������ Ŭ���� ( ĳ���� Ŭ����_Ŭ���� )
	private String 		chaClassName = "���� ��";	// Ŭ���� ��[Ŭ������ ���� �ٸ�]
	private int			position_x = 200;				// x ��ġ
	private int 		position_y	= 200;			// y ��ġ
	
	private int 		speed;					// ĳ���� �ӵ� [Ŭ������ ���� �ٸ�]
	
	private int			width	= 50;					// ���� ĳ������ ũ��
	private int			height	= 50;					// ���� ĳ������ ũ��
		
	// ���콺 ������ ����( ĳ���� ����  )
	// ���콺 ������ ����
	
	//------------------------ ������(Ŭ��������) -----------------------------
	public CharacterInfo(String argClassNameStr){	// Ŭ���� �������� Ŭ���� ����
		chaClassName = argClassNameStr;
		setStatByClassStr();
		
	}	
	public CharacterInfo(){	// �ƹ��͵� �������� �ʴ� ������
		
	}

	//------------------------ ���� ���� �޼��� -------------------------------
	// forward(left, right, up, down ���ڿ��� ���� �̵�)
	public void move(String forward){
		if(forward.equals("left"))
			position_x -= this.speed;
		else if(forward.equals("right"))
			position_x += this.speed;
		else if(forward.equals("up"))
			position_y -= this.speed;
		else if(forward.equals("down"))
			position_y += this.speed;
	}
	//------------------------ ���� ���� �޼��� -------------------------------
	public String getChaClassName(){
		return chaClassName;
	}
	public void setChaClassName(String chaClassName){
		this.chaClassName = chaClassName;
		System.out.println(chaClassName);
		setStatByClassStr();
	}
	public void setStatByClassStr(){
		if(chaClassName.equals("fighter") || chaClassName.equals("������")){
			System.out.println("�����Ͱ� �����Ǿ���");
			this.chaClass = new FighterClass();
		}
		if(chaClassName.equals("strategist") || chaClassName.equals("������"))
			this.chaClass = new StrategistClass();
		if(chaClassName.equals("theif") || chaClassName.equals("����"))
			this.chaClass = new TheifClass();
		
		System.out.println(chaClassName);
		this.chaClassName = chaClass.getClassName();	// Ŭ������ ���� 
		System.out.println(chaClassName);
		this.maxHealthPoint = chaClass.getMaxHP();		// �⺻ ü�� ����
		this.curHealthPoint = this.maxHealthPoint;		// ���� ü���� �ִ�ü������ ����
		this.speed = chaClass.getDefaultSpeed();		// �̵��ӵ� ����
	}
	// x��ǥ ����
	public int getXPos(){
		return this.position_x;
	}
	public int getYPos(){
		return this.position_y;
	}
	public boolean getIsOnGame(){
		return isOnGame;
	}
	public void setIsOnGame(boolean arg){
		isOnGame = arg;
	}
	
	
}
