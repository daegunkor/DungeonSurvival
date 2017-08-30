import java.io.Serializable;

public class GameObject implements NumOfDefine, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int 	userCode	= NOT_CONNECTED;	// 보내는 유저의 번호
	private String 	message		= null;				// 데이터의 종류
	private Object 	data		= null;				// 송신 객체
	
	
	// 아무것도 없는 객체
	public GameObject(){
		this(NOT_CONNECTED,null,null);
	}
	
	public GameObject(int userCode, String message, Object data){
		this.userCode 	= userCode;
		this.message 	= message;
		this.data		= data;
	}
	
	public int getUserCode(){
		return userCode;
	}
	public void setUserCode(int userCode){
		this.userCode = userCode;
	}
	
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}
	
	public Object getData(){
		return data;
	}
	public void setData(Object data){
		this.data = data;
	}
	
}

