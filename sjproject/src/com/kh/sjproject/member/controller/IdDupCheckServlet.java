package com.kh.sjproject.member.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.sjproject.member.model.service.MemberService;

@WebServlet("/member/idDupCheck.do")
public class IdDupCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public IdDupCheckServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// POST 방식 문자인코딩 변경 이유 -> 한글 때문에
		// ID는 영어 + 숫자이기 때문에 별도의 문자인코딩 필요 없음.
		String id = request.getParameter("id");
		
		try {
			int result = new MemberService().idDupCheck(id);
			
			/*request.setAttribute("result", result);
			request.setAttribute("id", id);
			
			RequestDispatcher view = request.getRequestDispatcher("idDupForm.do");
			
			view.forward(request, response);*/
			
			PrintWriter out = response.getWriter();
			if(result>0) out.append("no");
			else 		 out.append("yes");	
			
			
			
			
			
			
			
		}catch(Exception e) {
			request.setAttribute("errorMsg", "아이디 중복 확인 과정에서 오류가 발생하였습니다.");
			e.printStackTrace();
			
			String path = "/WEB-INF/views/common/errorPage.jsp";
			RequestDispatcher view = request.getRequestDispatcher(path);
			view.forward(request, response);
			
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
		doGet(request, response);
	}

}
