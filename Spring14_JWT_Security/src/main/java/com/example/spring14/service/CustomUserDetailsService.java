package com.example.spring14.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring14.dto.UserDto;

/*
 *  Spring Security 가 로그인 처리시 호출하는 메소드를 가지고 있는 서비스 클래스 정의하기 
 */

@Service //bean 으로 만들기 위한 어노테이션
public class CustomUserDetailsService implements UserDetailsService{
	
	
	// username 을 전달하면 해당 user 의 자세한 정보를 리턴하는 메소드 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//원래는 DB 에서 dao 를 이용해 username 에 해당하는 사용자정보(UserDto) 를 얻어와야 한다.
		//지금은 sample 데이터를 만들어서 사용하기
		
		// 실제 DB 에는 ROLE_XXX 형식으로 저장이 되어 있어야한다 
		String role="";
		if(username.equals("kimgura")) {
			role="ROLE_USER";
		}else if(username.equals("batman")) {
			role="ROLE_STAFF";
		}else if(username.equals("superman")){
			role="ROLE_ADMIN";
		}
		
		//비밀번호를 암호화 하기 위한 객체 
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		//DB 에는 암호화된 비밀번호가 저장되어 있다고 가정하자 (암호화 하기 이전의 비밀번호가 1234 라고 가정)
		String encodedPwd=encoder.encode("1234");
		
		//Sample UserDto 객체 만들기(원래는 DB 에서 읽어온 데이터)
		UserDto dto=UserDto.builder()
					.userName(username)
					.password(encodedPwd)
					.email("aaa@naver.com")
					.role(role)
					.build();
		/*
		 * 	Spring Security 에서 role 과 authority 는 약간의 차이가 있지만 편의상 같다라고 생각하자
		 * 
		 *  ROLE_USER 를 예를 들면 
		 *  
		 *  해당 USER 의 role 은 "USER" 
		 *  해당 USER 의 authority 는 "ROLE_USER" 임을 명심하자...  
		 *  
		 */
		//권한 목록을 List 에 담아서  (지금은 1개 이지만)
		List<GrantedAuthority> authList=new ArrayList<>();
		// authoryty 객체를 생성하는것이기 때문에 생성자에  "ROLE_XXX" 형식의 문자열을 넣어줘야 한다. 
		authList.add(new SimpleGrantedAuthority(dto.getRole()));
		
		//UserDetails 객체를 생성해서 
		UserDetails ud=new User(dto.getUserName(), dto.getPassword(), authList);
		//리턴해준다.
		return ud;
	}

}







