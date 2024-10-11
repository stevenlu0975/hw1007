package com.systex.lottery.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systex.lottery.exception.MemberException;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.MemberRepository;
import com.systex.lottery.model.RequestWrapper;
import com.systex.lottery.model.Result;
import com.systex.lottery.service.MemberService;

/**
 * 攔截
 * 1. /lottery/login，post方法，登入，request body : (username:String,password:String)
 * 2. /lottery/check_user_name，post方法，查詢帳號，request body :(username:String)
 * 3. /lottery/register，post方法，註冊，request body : (username:String,password:String,confirmPassword:String)
 */
public class LoginAndRegisterFilter  implements Filter {
    private static final LinkedHashSet<String> MemberKeys = new LinkedHashSet(
    		Set.of("username","password","confirmPassword"));
    private static final int PATH_CHECK_NAME = 1;
    private static final int PATH_LOGIN = 2;
    private static final int PATH_REGISTER = 3;

	@Autowired
	MemberService memberService;
	public LoginAndRegisterFilter() {}
	public LoginAndRegisterFilter(MemberService memberService) {
	    this.memberService = memberService;
	    System.out.println("MemberRepository injected: " + (memberService != null));
	}
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code hereW
		System.out.println("getContentType "+request.getContentType());
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
	
		
		//必須傳送json 格式
		if(!request.getContentType().equals("application/json")) {
			httpServletResponse.sendRedirect(httpServletRequest.getRequestURI());
		}
		//將json 轉換成mapper
		RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
		System.out.println("params : "+requestWrapper.getBodyString());
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> paramMap = mapper.readValue(requestWrapper.getBodyString(), Map.class);
		
		//確認是攔截甚麼路徑
		int pathNum = checkPath(httpServletRequest.getRequestURI());
		// 1. 確認json key 合法
		if(!isParamKeysValid(paramMap,pathNum)) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(MemberException.PARAMETER_INVALID));
			return;
		}
		// 2. 確認map 的值不為空
		if(!(isParamsNotNull(paramMap))) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(MemberException.PARAMETER_NULL));
			return;
		}
		// 3-1. 確認 password 合法
		if(pathNum>PATH_CHECK_NAME && !memberService.validateParam(paramMap.get("password"))) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(MemberException.PASSWORD_INVALID));
			return;
		}
		// 3-2. 確認 confirmpassword 合法
		if(pathNum==PATH_REGISTER && !memberService.validateParam(paramMap.get("confirmPassword"))) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(MemberException.CONFIRMPASSWORD_INVALID));
			return;
		}
		// 3-3. 分別處理 login 、register、 check_user_name 
		
		// login 
		if(pathNum==PATH_LOGIN) {

			String token = null;
			try {
				Member memberDB = memberService.login(paramMap.get("username"),paramMap.get("password"));
				 token = memberService.setToken(requestWrapper, httpServletResponse, memberDB);
			}catch (Exception e) {
				// TODO: handle exception
				mapper.writeValue(httpServletResponse.getWriter(), Result.error(e.getMessage()));
			}
			mapper.writeValue(httpServletResponse.getWriter(), Result.success(token));
			return;
		}
		//check_user_name || register
		Optional<Member> member=null;
		try {
			 member =  memberService.queryMemberByName(paramMap.get("username"));
		}catch (Exception e) {
			// TODO: handle exception
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(e.getMessage()));
			return ;
		}

		if(member.isPresent()) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.error(MemberException.USERNAME_EXISTED));
			return;
		}
		//PATH_CHECK_NAME 
		if(pathNum==PATH_CHECK_NAME) {
			mapper.writeValue(httpServletResponse.getWriter(), Result.success());
			return;
		}
		chain.doFilter(requestWrapper, httpServletResponse);
		
		return;
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				fConfig.getServletContext());
	}
	/**
	 * 藉由傳進的path 確認要進行甚麼判斷
	 * */
	private int checkPath(String path) {
		int result = 0;
		switch(path) {
		case "/lottery/check_user_name":
			result=PATH_CHECK_NAME;
			break;
		case "/lottery/login":
			result=PATH_LOGIN;
			break;
		case "/lottery/register":
			result=PATH_REGISTER;
			break;
		
		}
		return result;
	}
	/**
	 * 確認key的數量名、稱正確
	 * */
	private boolean isParamKeysValid(Map<String, String> map,int validRound) {

		if(map.size()!=validRound) {
			return false;
		}
		for(String str: MemberKeys) {
			if(map.containsKey(str)) {
				validRound--;
			}
		}
		return validRound==0?true:false;
	}
	private boolean isParamsNotNull(Map<String, String> map) {
		boolean result = true;
		for(String str: map.keySet()) {
			String value =map.get(str);
			if(value==null || value.trim().isEmpty()) {
				result = false;
				break;
			}
		}
		return result;
	}
}
