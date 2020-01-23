package com.kh.sjproject.member.controller;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.sjproject.member.model.service.MemberService;
import com.kh.sjproject.member.model.vo.Member;


@WebServlet("/member/mypage.do")
public class MypageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public MypageServlet() {
        super();
        
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 세션에 저장되어 있는 ID 가져오기
//		String memberId = ((Member)(request.getSession().getAttribute("loginMember"))).getMemberID();
		
		HttpSession session = request.getSession();
		Member loginMember = (Member)session.getAttribute("loginMember");
		String memberId = loginMember.getMemberId();
		
		try {
			// DB에서 아이디가 일치하는 회원 정보 읽어오기 
			Member selectMember = new MemberService().selectMember(memberId);
			System.out.println("selectMember : " + selectMember);
			
			request.setAttribute("member", selectMember);
			
			String path = "/WEB-INF/views/member/mypage.jsp";
			RequestDispatcher view = request.getRequestDispatcher(path);
			view.forward(request, response);
			
			
		}catch (Exception e){
			
			request.setAttribute("errorMsg", "회원정보조회 과정에서 오류가 발생하였습니다.");
			e.printStackTrace();
			
			String path = "/WEB-INF/views/common/errorPage.jsp";
			RequestDispatcher view = request.getRequestDispatcher(path);
			view.forward(request, response);
			
		}
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
}
