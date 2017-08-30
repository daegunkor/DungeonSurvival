package ChaClass;

import java.io.Serializable;

// ������ ���� Ŭ����
public class StrategistClass extends ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	CLASSNAME ="strategist";	// �ش� Ŭ���� �ĺ� ����
	private int 	MAXHP = 300;			// �⺻ ���� ü��
	private int 	DEFAULTSPEED = 6;	// �⺻ �̵� �ӵ�
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
