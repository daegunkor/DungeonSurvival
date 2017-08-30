package ChaClass;

import java.io.Serializable;

public class TheifClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="theif";	// 해당 클래스 식별 문자
	private int 	MAXHP = 300;			// 기본 설정 체력
	private int 	DEFAULTSPEED = 12 ;	// 기본 이동 속도
	private String[] 	IMGSRC = { "image/theifUp.png",
									"image/theifDown.png",
									"image/theifLeft.png",
									"image/theifRight.png"};

	
	public TheifClass(){
		super.className = CLASSNAME;
		super.maxHP = MAXHP;
		super.defaultSpeed = DEFAULTSPEED;
		super.imgSrc = IMGSRC;
	}
}
