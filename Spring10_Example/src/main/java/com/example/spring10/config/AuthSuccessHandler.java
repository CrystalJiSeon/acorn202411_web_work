package com.example.spring10.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 *  로그인 성공후에 호출될 메소드를 가지고 있는 클래스 정의하기 
 */

public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	//1. 요청캐쉬 객체를 직접 생성해서 
    private RequestCache requestCache = new HttpSessionRequestCache();
    
    //2. 생성자에서 부모객체에 전달
    public AuthSuccessHandler() {
        super.setRequestCache(requestCache);
    }
	
	//입력한 username 과 password 가 일치해서 로그인이 성공하면 이 메소드가 호출된다 
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		//세션 유지 시간 설정
    	HttpSession session=request.getSession();
        session.setMaxInactiveInterval(60*20);//초단위로 설정
        
        //Authentication 객체의 메소드를 이용해서 지금 로그인된 사용자에 대한 자세한 정보를 얻어낼수 있다.
        String userName=authentication.getName();
        System.out.println("로그인된 사용자:"+userName);
        
        // 로그인된 userName 을 session 영역에 담아놓으면 jsp or thymeleaf 에서 편리하게 사용할수 있다.
        // jsp => ${sessionScope.userName}  or ${userName}
        // thymeleaf =>  ${session.userName}
        // 로 userName 을 추출할수 있다.
        session.setAttribute("userName", userName);
		
		//3. 로그인 성공이후 미리 저장된 요청이 있었는지 읽어와서
    	SavedRequest cashed=requestCache.getRequest(request, response);
    	
    	//4. 만일 미리 저장된 요청이 없다면 (로그인 하지 않은 상태로 인증이 필요한 경로를 요청하지 않았다면)
        if(cashed==null) {
        	//5. 로그인 환영 페이지로 foward 이동 (POST 방식 요청임에 주의!!!)
        	RequestDispatcher rd=request.getRequestDispatcher("/user/login-success");
        	rd.forward(request, response);
        }else {
        	//6. 원래 가려던 목적지 경로로 리다일렉트 이동 시킨다 (GET 방식 요청 파라미터도 자동으로 같이 가지고 간다)
        	super.onAuthenticationSuccess(request, response, authentication);
        }
	}
}








