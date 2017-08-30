package ChaClass;

import java.io.Serializable;


// 격투가 정의 클래스
public class FighterClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="fighter";	// 해당 클래스 식별 문자
	private int 	MAXHP = 300;			// 기본 설정 체력
	private int 	DEFAULTSPEED = 8;	// 기본 이동 속도
	private String[] 	IMGSRC	= { "image/fighterUp.png",
									"image/fighterDown.png",
									"image/fighterLeft.png",
									"image/fighterRight.png"};

	
	
	public FighterClass(){
		super.className = CLASSNAME;
		super.maxHP = MAXHP;
		super.defaultSpeed = DEFAULTSPEED;
		super.imgSrc = IMGSRC;
	}

	
}
