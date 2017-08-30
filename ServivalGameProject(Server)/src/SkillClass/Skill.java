package SkillClass;

import java.io.Serializable;


// 스킬에 대한 적용을 한다
public class Skill implements Serializable{
	// 스킬의 발동 상태 / on / off
	boolean onOff = false;
	// 스킬의 종류 [ 발사형스킬 ] [ 적용형 스킬 ]
	String style = "";
	// 스킬 마크
	String imgSrc = "";
	// [발사형 스킬] 스킬 객체 ( 이미지, 좌표, 속도 )
	Object[] skillObject;
	// [적용형 스킬] 스킬의 효과
	// 쿨타임 ( 타이머 ) ( 오파시티값 회색 상자 ) ( 채워야 하는 쿨타임, 현재 쿨타임 )
	int coolTimeLimit;
	int coolTimeCur;
	
	/*public Skill(int myChaIndex, CharacterInfo[] allChaInfo){
		
	}*/
	
	// 스킬 발동 매서드
	void use(){
		
	}
	
	// 피격시 발동 메서드
	void hit(){
		
	}
	
	
	// 캐릭터 정보 가져오기
	// 발사 방향 : 캐릭터 시선
	// 스킬 피격 판정 
}
