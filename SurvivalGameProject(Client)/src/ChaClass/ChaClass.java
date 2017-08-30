package ChaClass;

import java.io.Serializable;


//3가지 클래스에 대한 일반화 클래스
//격투가, 전략가, 도둑
public class ChaClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String className;		// 해당 클래스 식별 문자
	
	protected int maxHP;			// 기본 설정 체력
	
	protected int defaultSpeed;		// 기본 이동 속도
	
	protected String[] imgSrc;		// 보는 방향별 이미지 주소
	
	
	
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
