package SkillClass;

import java.io.Serializable;


// ��ų�� ���� ������ �Ѵ�
public class Skill implements Serializable{
	// ��ų�� �ߵ� ���� / on / off
	boolean onOff = false;
	// ��ų�� ���� [ �߻�����ų ] [ ������ ��ų ]
	String style = "";
	// ��ų ��ũ
	String imgSrc = "";
	// [�߻��� ��ų] ��ų ��ü ( �̹���, ��ǥ, �ӵ� )
	Object[] skillObject;
	// [������ ��ų] ��ų�� ȿ��
	// ��Ÿ�� ( Ÿ�̸� ) ( ���Ľ�Ƽ�� ȸ�� ���� ) ( ä���� �ϴ� ��Ÿ��, ���� ��Ÿ�� )
	int coolTimeLimit;
	int coolTimeCur;
	
	/*public Skill(int myChaIndex, CharacterInfo[] allChaInfo){
		
	}*/
	
	// ��ų �ߵ� �ż���
	void use(){
		
	}
	
	// �ǰݽ� �ߵ� �޼���
	void hit(){
		
	}
	
	
	// ĳ���� ���� ��������
	// �߻� ���� : ĳ���� �ü�
	// ��ų �ǰ� ���� 
}
