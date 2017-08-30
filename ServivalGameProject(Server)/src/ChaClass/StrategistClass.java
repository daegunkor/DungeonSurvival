package ChaClass;

import java.io.Serializable;

// 전략가 정의 클래스
public class StrategistClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="strategist";	// 해당 클래스 식별 문자
	private int 	MAXHP = 300;			// 기본 설정 체력
	private int 	DEFAULTSPEED = 6;	// 기본 이동 속도
	private String[] 	IMGSRC	= { "image/strategistUp.png",
								"image/strategistDown.png",
								"image/strategistLeft.png",
								"image/strategistRight.png"};

	
	public StrategistClass(){
		super.className = CLASSNAME;
		super.maxHP = MAXHP;
		super.defaultSpeed = DEFAULTSPEED;
		super.imgSrc = IMGSRC;
	}
}
