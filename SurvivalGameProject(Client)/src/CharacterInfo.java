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
	private boolean 	isOnGame = false;		// 게임에 들어가있는가 
	private int			maxHealthPoint;			// 체력 최대치[클래스에 따라 다름]
	private int 		curHealthPoint;			// 현재 체력
	
	private ChaClass	chaClass;				// 캐릭터의 클래스 ( 캐릭터 클래스_클래스 )
	private String 		chaClassName = "선택 전";	// 클래스 명[클래스에 따라 다름]
	private int			position_x = 200;				// x 위치
	private int 		position_y	= 200;			// y 위치
	
	private int 		speed;					// 캐릭터 속도 [클래스에 따라 다름]
	
	private int			width	= 50;					// 가로 캐릭터의 크기
	private int			height	= 50;					// 세로 캐릭터의 크기
		
	// 마우스 포인터 방향( 캐릭터 기준  )
	// 마우스 포인터 길이
	
	//------------------------ 생성자(클래스설정) -----------------------------
	public CharacterInfo(String argClassNameStr){	// 클래스 값에따른 클래스 생성
		chaClassName = argClassNameStr;
		setStatByClassStr();
		
	}	
	public CharacterInfo(){	// 아무것도 설정하지 않는 생성자
		
	}

	//------------------------ 정보 조작 메서드 -------------------------------
	// forward(left, right, up, down 문자열에 따라 이동)
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
	//------------------------ 게터 세터 메서드 -------------------------------
	public String getChaClassName(){
		return chaClassName;
	}
	public void setChaClassName(String chaClassName){
		this.chaClassName = chaClassName;
		System.out.println(chaClassName);
		setStatByClassStr();
	}
	public void setStatByClassStr(){
		if(chaClassName.equals("fighter") || chaClassName.equals("격투가")){
			System.out.println("파이터가 설정되었다");
			this.chaClass = new FighterClass();
		}
		if(chaClassName.equals("strategist") || chaClassName.equals("전략가"))
			this.chaClass = new StrategistClass();
		if(chaClassName.equals("theif") || chaClassName.equals("도둑"))
			this.chaClass = new TheifClass();
		
		System.out.println(chaClassName);
		this.chaClassName = chaClass.getClassName();	// 클래스명 설정 
		System.out.println(chaClassName);
		this.maxHealthPoint = chaClass.getMaxHP();		// 기본 체력 설정
		this.curHealthPoint = this.maxHealthPoint;		// 현재 체력을 최대체력으로 설정
		this.speed = chaClass.getDefaultSpeed();		// 이동속도 설정
	}
	// x좌표 게터
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
