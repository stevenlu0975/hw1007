package com.systex.lottery.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.jayway.jsonpath.internal.function.text.Length;

import com.systex.lottery.exception.MemberException;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.MemberRepository;
import com.systex.lottery.model.Result;
import com.systex.lottery.utils.JwtUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class MemberService {
	@Resource
	MemberRepository memberRepository;
	/**
	 * 檢查長度是否藉吳5~30，且不包含空格
	 * */
	public boolean validateParam(String... strings) {
		boolean result=true;
	    if (strings != null) {
	        for (String param : strings) {
	            if (param.length()<5 || param.length()>30 ||
	            	param.trim().isEmpty()) {
	            	result =  false;
	            	break;
	            }
	        }
	    }else {
	    	result =false;
	    }

		return result;
	}
	public Optional<Member> queryMemberByName(String username) throws MemberException {
		if(!validateParam(username)) {
			throw new MemberException(MemberException.USERNAME_INVALID);
		}
		return  memberRepository.queryByUserName(username);
	}
	
	public void register(String username,String password) {
		Member member =new Member(username,DigestUtils.md5DigestAsHex(password.getBytes()));
		memberRepository.save(member);
	}
	public Member login(String username,String password) throws MemberException {
		Optional<Member> memberOptional = memberRepository.queryByUserName(username);
		Member memberDB = memberOptional.get();
		
		if(!memberOptional.isPresent()) {
			throw new MemberException(MemberException.USERNAME_NOT_EXISTED);
		}
		String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes());
		if(!encryptPassword.equals(memberDB.getPassword())) {
			throw new MemberException(MemberException.PASSWORD_ERROR);
		}
		
		return memberDB;
	}
	public String setToken(HttpServletRequest request,HttpServletResponse response,Member memberDB) {
			Map<String,Object> claims = new HashMap<>();
			claims.put("USER_ID", memberDB.getId());
			String token = JwtUtil.createJWT(JwtUtil.KEY, 60*60*1000, claims);
			System.out.println(token);
			HttpSession session =request.getSession();
			session.setAttribute("token", token);
			session.setMaxInactiveInterval(60*60);
			return token;
	}
}
