package ChaClass;

import java.io.Serializable;


//3���� Ŭ������ ���� �Ϲ�ȭ Ŭ����
//������, ������, ����
public class ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String className;		// �ش� Ŭ���� �ĺ� ����
	
	protected int maxHP;			// �⺻ ���� ü��
	
	protected int defaultSpeed;		// �⺻ �̵� �ӵ�
	
	protected String[] imgSrc;		// ���� ���⺰ �̹��� �ּ�
	
	
	
	public String getClassName(){
		return this.className;
	}
	
	public int getMaxHP(){
		return maxHP;
	}
	public int getDefaultSpeed(){
		return defaultSpeed;
	}
	
	
}
