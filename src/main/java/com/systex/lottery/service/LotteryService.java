package com.systex.lottery.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.systex.lottery.exception.LotteryException;
import com.systex.lottery.model.Member;
import com.systex.lottery.model.Result;
import com.systex.lottery.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Service
public class LotteryService {
	public LotteryService() {}
	public  ArrayList[] getNumbers(int groups,ArrayList<Integer> excludes) {
		
		ArrayList<Integer>[] lists = new ArrayList[groups];
		for(int i=0;i<groups;i++) {
			Set<Integer> set = new TreeSet<>();
			while(set.size()<6) {
				//set.add()
				Integer temp = (int)(Math.random()*49)+1;
				if(excludes!=null && excludes.contains(temp)==true) continue;
				set.add(temp);
			}
			lists[i] = new ArrayList<>(set);
		}
		return lists;
	}
	/**
	 * 較驗excludeNumbersString的內容，並轉換成arraylist
	 * @throws LotteryException 
	 * **/
	public ArrayList<Integer>  convertExcludeList(String excludeNumbersString) throws LotteryException {
		if(excludeNumbersString==null || excludeNumbersString.equals("")) {
			return null;		
		}
		TreeSet<Integer> treeset = new TreeSet<>();
        for (int i = 0; i < excludeNumbersString.length(); i++) {
            char ch = excludeNumbersString.charAt(i);
            if(Character.isWhitespace(ch)) {
            	continue;
            }
            if (!Character.isDigit(ch)) {
                throw new LotteryException(LotteryException.EXCLUDE_STRING_INVALID);
            }
            int number = Character.getNumericValue(ch);
            if(number<1 || number>49) {
            	throw new LotteryException(LotteryException.EXCLUDE_STRING_INVALID);
            }
            treeset.add(number);
        }
		if(treeset.size()>43) {
			throw new LotteryException(LotteryException.EXCLUDE_STRING_OUT_OF_BOUND);
		}
		return new ArrayList<Integer>(treeset);
	}
	/**
	 * 較驗sets的內容，並轉換成arraylist
	 * @throws Exception 
	 * **/
	public int ConvertSets(String sets) throws Exception {
		if(sets==null) {
			throw new LotteryException(LotteryException.SETS_INVALID);
		}
		return Integer.parseInt(sets);
	}

}
