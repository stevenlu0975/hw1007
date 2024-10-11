package com.systex.lottery.filter;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systex.lottery.exception.MemberException;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.MemberRepository;
import com.systex.lottery.model.Result;
import com.systex.lottery.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//@WebFilter(urlPatterns = {"/*"}) // 适用所有 URL
//@Component
public class ExcludePathFilter implements Filter{

	private static final int TOKEN_IS_VALID=0;
	private static final int TOKEN_IS_INVALID=1;
	private static final int TOKEN_IS_EXPIRED=2;
	private static final TreeSet<String> excludePathSet = new TreeSet<>(Set.of("/lottery/login",
			"/lottery/index.html","/lottery/login.html","/lottery/register.html",
			"/lottery/style/mycss.css","/lottery/js/lottery.js",
			"/lottery/register","/lottery/logout","/lottery/check_user_name",
			"/lottery/js/index.js","/lottery/js/login.js","/lottery/js/register.js"
			));
	private static final TreeSet<String> protectedPathSet = new TreeSet<>(Set.of("/lottery/lotteryController.do"));
	@Autowired 
	MemberRepository memberRepository;
	@Autowired
	ObjectMapper mapper;
	/**
	 * lottery/index.jsp
	 * lottery/style/mycss.css
	 * lottery/main
	 * /lottery/login
	 * */
	public ExcludePathFilter() {}
	public ExcludePathFilter(MemberRepository memberRepository) {
	    this.memberRepository = memberRepository;
	    System.out.println("MemberRepository injected: " + (memberRepository != null));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
			      filterConfig.getServletContext());
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		 HttpServletRequest req = (HttpServletRequest) request;
         HttpServletResponse resp = (HttpServletResponse) response;

         String uri = req.getRequestURI();      
         System.out.println(uri);
         // 是否在白名單中
         if(excludePathSet.contains(uri)) {
        	 chain.doFilter(request, response);
        	 return;
         }
         
         //是否在白名單中
         if(!protectedPathSet.contains(uri)) {
        	 resp.sendRedirect("/lottery/index.html");
        	 return;
         }
//         ObjectMapper mapper = new ObjectMapper();
         //格式錯誤
         if(!request.getContentType().equals("application/json")) {
         	resp.sendRedirect("/lottery/index.html");
         }

         //token 是否有效
         String errorMsg = validateToken(req,resp);
         if(errorMsg!=null) {
        	 mapper.writeValue(resp.getWriter(), Result.error(errorMsg));
        	 return;
         }
         
         chain.doFilter(request, response);
         
	}
	/**
	 * 0 失敗 1 成功 2 過期
	 * return error message
	 * **/
	private String validateToken(HttpServletRequest req,HttpServletResponse resp) throws IOException {
	
 		String  token =  (String) req.getHeader("Authorization");
 		System.out.println(token);
 		try {
 	 		if(token==null) {
 	 			throw new Exception("no token");
 	 		}
 	 		Claims claims = JwtUtil.parseJWT(JwtUtil.KEY, token);
 	 		String sessionToken = (String) req.getSession().getAttribute("token");

 	 		if(sessionToken==null || !sessionToken.equals(token)) {
 	 			throw new Exception("token expired");
 	 		}
		}
 		catch (ExpiredJwtException e) {
 			System.out.println(e.getMessage());
            return "token expired";
 		}
 		catch (Exception e) { // SignatureException ExpiredJwtException
			// TODO: handle exception
 			e.printStackTrace();
 			System.out.println(e.getMessage());
 			return e.getMessage();
		} 
 		return null;
	}

}
