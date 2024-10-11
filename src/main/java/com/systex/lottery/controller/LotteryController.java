package com.systex.lottery.controller;

import java.io.UnsupportedEncodingException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.MemberRepository;
import com.systex.lottery.model.Result;
import com.systex.lottery.service.LotteryService;
import com.systex.lottery.service.MemberService;
import com.systex.lottery.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LotteryController {
	@Resource
	LotteryService lotteryService;
	@Autowired
	MemberService memberService;
//	@GetMapping("/main")
//	public String mainPage() {
//		return "main";
//	}
	@PostMapping("/lotteryController.do")
	@ResponseBody
	public Result generateLottery(@RequestBody Map<String, String> payload) {
		Result result=null;
		String sets = payload.get("sets");
		String excludeNumbersString = payload.get("excludeNumbersString");
		try {

			int set = lotteryService.ConvertSets(sets);
			ArrayList<Integer> excludeList = lotteryService.convertExcludeList(excludeNumbersString);
			ArrayList<Integer>[]resultList = lotteryService.getNumbers(set, excludeList);			
			result = Result.success(resultList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result = Result.error(e.getMessage());
		}
		
		return result;
	}
//	@GetMapping("/login")
//	public String loginPage(String username,String password) {
//		return "login";
//	}
	/**
	 * 如果登入成功就，就往session中存入token
	 * */
	@PostMapping("/login")
	@ResponseBody
	public Result login(@RequestBody Member member,HttpServletRequest request,HttpServletResponse response) {

		Result result=null;

		try {

			Member memberDB = memberService.login(member);
			Map<String,Object> claims = new HashMap<>();
			claims.put("USER_ID", memberDB.getId());
			String token = JwtUtil.createJWT(JwtUtil.KEY, 60*60*1000, claims);
			System.out.println(token);
			HttpSession session =request.getSession();
			session.setAttribute("token", token);
			session.setMaxInactiveInterval(60*60);
			result = Result.success(token);
			
		}catch(Exception e) {		
			System.out.println(e.getMessage());
			result = Result.error(e.getMessage());
		}

		return result;
	}
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/index.html";
	}
//	@GetMapping("/register")
//	public String registerPage() {
//		return "register";
//	}
//	@PostMapping("/check_user_name")
//	@ResponseBody()
//	public Result registerName(@RequestBody Map<String, String> payload) {
//		String username = payload.get("username");
//		System.out.println("username: " + username);
//		Optional<Member> memberOptional;
//		
//		try {
//			memberOptional =  memberService.queryMemberByName(username);
//			if(memberOptional.isPresent()) {
//				throw new RegisterException("帳號已存在");
//			}
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			System.out.println(e.getMessage());
//			return Result.error(e.getMessage()); 
//		}
//		return Result.success();
//	}
	/**
	 * 判斷 是否 帳號重複、密碼錯誤、密碼格式錯誤
	 * 為問題就往sql新增，最後倒回都入頁面
	 * */
	@PostMapping("/register")
	@ResponseBody()
	public Result register(@RequestBody Map<String, String> payload) {
		String username =payload.get("username");
		String password =payload.get("password");
	
		try {
			memberService.register(username, password);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return Result.error(e.getMessage()); 
		}
		return Result.success();
	}



	
}
