package main;

import java.util.HashMap;

import org.json.simple.JSONObject;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public class SendAuthNum {
	private final String api_key = "NCS1YR5S5JOOAQAF";
	private final String api_secret = "I0CHQXOWPZMAQUIQO80AXPFRXNG8W5IN";
	private Message coolSms = new Message(api_key, api_secret);
	
	// 회원가입에 사용됨
	public int send(String recipient_phoneNum) {
		int authNum = (int)(Math.random() * (999999 - 100000 + 1)) + 100000;
		String msg = "Bookle 서비스를 이용해주셔서 감사합니다 :)\n인증번호는 [" + authNum + "]입니다.";
		
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("to", recipient_phoneNum);
	    params.put("from", "01057796682");
	    params.put("type", "SMS");
	    params.put("text", msg);
	    // params.put("app_version", "test app 1.2"); // application name and version

	    try {
	      JSONObject obj = (JSONObject)coolSms.send(params); // 인증번호 전송
	      System.out.println(obj.toString());
	    }catch(CoolsmsException e) { // 인증번호 전송 실패
	      System.out.println(e.getMessage());
	      System.out.println(e.getCode());
	    }
	    
	    return authNum;
	}
	
	
	// 비밀번호 찾기에 사용됨
	public int send(String userName, String phoneNum) {
		int authNum = (int)(Math.random() * (999999 - 100000 + 1)) + 100000;
		String msg = userName + "님! Bookle 서비스를 이용해주셔서 감사합니다 :)\n인증번호는 [" + authNum + "] 입니다.";
		
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("to", phoneNum);
	    params.put("from", "01057796682");
	    params.put("type", "SMS");
	    params.put("text", msg);
	    // params.put("app_version", "test app 1.2"); // application name and version

	    try {
	      JSONObject obj = (JSONObject)coolSms.send(params); // 인증번호 전송
	      System.out.println(obj.toString());
	    }catch (CoolsmsException e) { // 인증번호 전송 실패
	      System.out.println(e.getMessage());
	      System.out.println(e.getCode());
	    }
	    
	    return authNum;
	}
}