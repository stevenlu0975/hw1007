package com.systex.lottery.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.jayway.jsonpath.internal.function.text.Length;

import com.systex.lottery.exception.MemberException;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.MemberRepository;

import jakarta.annotation.Resource;

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
	public Member login(Member memberdto) throws MemberException {
		Optional<Member> memberOptional = memberRepository.queryByUserName(memberdto.getUsername());
		Member memberDB = memberOptional.get();
		
		String encryptPassword = DigestUtils.md5DigestAsHex(memberdto.getPassword().getBytes());
		if(!encryptPassword.equals(memberDB.getPassword())) {
			throw new MemberException(MemberException.PASSWORD_ERROR);
		}
		return memberDB;
	}
}
