package ChaClass;

import java.io.Serializable;

public class TheifClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="theif";	// �ش� Ŭ���� �ĺ� ����
	private int 	MAXHP = 300;			// �⺻ ���� ü��
	private int 	DEFAULTSPEED = 12 ;	// �⺻ �̵� �ӵ�
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
