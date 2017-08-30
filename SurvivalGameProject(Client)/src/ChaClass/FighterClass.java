package ChaClass;

import java.io.Serializable;


// ������ ���� Ŭ����
public class FighterClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="fighter";	// �ش� Ŭ���� �ĺ� ����
	private int 	MAXHP = 300;			// �⺻ ���� ü��
	private int 	DEFAULTSPEED = 8;	// �⺻ �̵� �ӵ�
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
