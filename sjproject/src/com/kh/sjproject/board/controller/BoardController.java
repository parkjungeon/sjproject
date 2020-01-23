package com.kh.sjproject.board.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.sjproject.board.model.service.BoardService;
import com.kh.sjproject.board.model.vo.Attachment;
import com.kh.sjproject.board.model.vo.Board;
import com.kh.sjproject.board.model.vo.PageInfo;
import com.kh.sjproject.board.model.vo.Reply;
import com.kh.sjproject.common.ExceptionForward;
import com.kh.sjproject.common.MyFileRenamePolicy;
import com.kh.sjproject.member.model.vo.Member;
import com.oreilly.servlet.MultipartRequest;


@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public BoardController() {
        super();
      
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = uri.substring((contextPath+"/board").length());
		
		String msg = null;
		String path = null;
		RequestDispatcher view = null;
		BoardService boardService = new BoardService();
		
		// 게시글 목록 조회용 Controller
		if(command.equals("/list")) {
			try {
			// 페이징 처리(pagination)
			// 눈에 보이는 게시판에 일정 개수의 개시글만 노출되고
			// 나머지는 페이지로 구분하여 숫자 형태로 보여주게 하는 방법
			
			// 현재 게시글 전체 수
			int listCount = boardService.getListCount();
			
			int limit = 5;									// 한 페이지에 보여질 게시글의 수
			
			int pagingBarsize = 10;							// 보여질 페이징바의 페이지 개수
			
			int currentPage = 0;							// 현재 페이지 번호를 표시할 변수
			
			int maxPage = 0;								// 전체 페이지의 수(== 마지막 페이지)
			
			int startPage = 0;								// 페이징바 시작 페이지 번호
			
			int endPage = 0;								// 페이징바 끝 페이지 번호         
			
			// currentPage - 현재 페이지 번호를 표시할 변수
			// 처음 게시판 목록으로 화면이 전환이 되면 1페이지가 보여야 함
			if(request.getParameter("currentPage")==null) {
				currentPage = 1;
			}else {
				// 전달 받은 값이 있을 경우 해당 번호를 저장
				
				currentPage = Integer.parseInt(request.getParameter("currentPage")); 
			}
			
			// maxPage - 총 페이지 수( == 마지막 페이지)
			// limit == 5일 경우
			// 게시글의 개수가 50개일 경우 : 10페이지 
			// 게시글의 개수가 51개일 경우 : 11페이지
			maxPage = (int)Math.ceil(((double)listCount / limit)); 
			
			// startPage - 페이징바의 시작 페이지 번호
			// 페이징바에 수가 10개씩 보여질 경우
			// 시작 번호는 1, 11, 21, .....
			startPage = (currentPage - 1) / pagingBarsize * pagingBarsize  + 1;
			
			// endPage - 페이징바 끝 페이지 번호
			endPage = startPage + pagingBarsize - 1;
			if(maxPage <= endPage) {
				endPage = maxPage;
			}
			
			PageInfo pInf = new PageInfo(listCount, limit, pagingBarsize, currentPage, maxPage, startPage, endPage);
			
			System.out.println(pInf);
			
			List<Board> bList = boardService.selectList(currentPage, limit);
			
			List<Attachment> fList = boardService.selectFilelist(currentPage, limit);
			
			path = "/WEB-INF/views/board/boardList.jsp";
			request.setAttribute("bList", bList);
			request.setAttribute("pInf", pInf);
			request.setAttribute("fList", fList);
			
			view = request.getRequestDispatcher(path);
			view.forward(request, response);
			
			
			} catch(Exception e) {
				ExceptionForward.errorPage(request, response, "게시판 목록 조회", e);
			}
			
		}
		
	     // 게시글 작성 화면 출력용 Controller
	      else if(command.equals("/insertForm")) {
	         path = "/WEB-INF/views/board/boardInsert.jsp";
	         view = request.getRequestDispatcher(path);
	         view.forward(request, response);
	      }
		
		
		// 게시글 등록용 Controller
		else if(command.equals("/insert")) {
			
			// form 전송 시 multipart/form-data로 전송하는 경우
			// 기존처럼 request.getParameter()를 이용하여
			// 입력값을 전달 받을 수 없음!
			// -> MultipartRequest를 사용해야 함. 
			//   -> cos.jar 라이브러리 추가 필요
			//    http://www.servlets.com/
			
			try {
				// 요청(request)가 multipart/form-data가 포함되어 있냐?
				if(ServletFileUpload.isMultipartContent(request)) {
					// 1_1. 전송 파일의 용량 제한 : 10MB로 제한
					// B -> KB -> GB -> TB 
					int maxSize = 10 * 1024 * 1024;
					
					// 1-2. 웹 서버 컨테이너 경로 추출
					//		 -> WebContent/ 경로 추출
					String root = request.getSession().getServletContext().getRealPath("/");
					
					// 1-3. 업로드 된 파일이 저장될 경로 지정
					String savePath = root + "resources/uploadImages/";
					
					// 2. 파일명 변환 작업
					// 파일명중복으로 인한 기존의 데이터 손실 예방
					// 	+ 파일명에 한글 또는 특수만자가 포함되면 서버 종류에 따라 문제가 발생할 수 있다.
					
					// cos.jar -> DefaultFileRenamePolicy를 제공해 주지만
					// 나만의 파일명 규칙을 적용하기 위해서 
					// 별도의 MyFileRenamePolicy 작성
					
					// 3. MultipartRequest 객체 생성
					//		-> 객체가 생성되는 순간에 
					//		-> request, 파일경로, 최대 파일 크기 지정(maxSize)
					//		       문자인코딩 지정
					//		-> * 변경된 파일명으로 지정된 경로 파일이 저장됨.
					MultipartRequest multiRequest = new MultipartRequest(request, savePath, maxSize, "UTF-8", new MyFileRenamePolicy()); 
					
					// 4. 서버에 저장된 파일의 원래 파일명, 이전 파일명을 
					//	  DB에 전달하기 위한 ArrayList 생성
					
					// 저장된 파일(변경된 파일명)
					ArrayList<String> saveFiles = new ArrayList<String>();
					
					// 원본 파일명
					ArrayList<String> originFiles = new ArrayList<String>();
					
					// 전달된 요청(multiRequest)에서 파일 리스트 정보를 얻어와 
					// 저장된 파일명, 원래 파일명을 각 ArrayList에 추가함.
					
					Enumeration<String> files = multiRequest.getFileNames();
					
					while(files.hasMoreElements()){
						
						// 업로드된 파일은 역순으로 전달됨. 
						String name = files.nextElement();
						
						if(multiRequest.getFilesystemName(name) != null) {
							// getFilesystemName(key) : rename된 파일명 얻어오기
							saveFiles.add(multiRequest.getFilesystemName(name));
							
							originFiles.add(multiRequest.getOriginalFileName(name));
							
							
						}
						
					}

					
					// 5. 파일 외에 나머지 게시글 입력값 얻어오기 
					String boardTitle = multiRequest.getParameter("title");
					String boardContent = multiRequest.getParameter("content");
					String boardCategory = multiRequest.getParameter("category");
					
					Board board = new Board(boardTitle, boardContent, boardCategory);
					
					// 회원 번호를 session에서 얻어옴
					// session은 request에서만 얻어올 수 있다!
					Member loginMember 
							= (Member)request.getSession().getAttribute("loginMember");
					
					int boardWriter = loginMember.getMemberNo();
					
					// 6. Attachment VO를 생성 후
					//    Attachment들을 저장할 List를 생성하여
					//    파일 경로, 파일 원본명, 변경할 파일명을 세팅
					ArrayList<Attachment> fList = new ArrayList<Attachment>();
					
					// 파일정보는 역순으로 저장됨으로, 반복문을 역으로 수행하여 원래 순서대로 저장
					for(int i=originFiles.size()-1; i >= 0; i--) {
						Attachment file = new Attachment();
						file.setFilePath(savePath);
						file.setFileOriginName(originFiles.get(i));
						file.setFileChangeName(saveFiles.get(i));
						
						// 썸네일 이미지는 fileLevel 0으로 
						// 나머지 이미지에는 fileLevel 1 부여
						if((i == originFiles.size()-1)
								&& multiRequest.getFilesystemName("img1") != null) {
							file.setFileLevel1(0);
						}else {
							file.setFileLevel1(1);
						}
						
						fList.add(file);
					}
					
					
					int result = boardService.insertBoard(board, boardWriter, fList);
					
					if(result > 0) msg = "게시글 등록 성공";
					else		   msg = "게시글 등록 실패";
					
					request.getSession().setAttribute("msg", msg);
					response.sendRedirect("list");
								
				
					
				}
				
			}catch(Exception e) {
				ExceptionForward.errorPage(request, response, "게시글 등록", e);
			}
			
		}
		
		// 게시글 상세 조회 Controller
		else if(command.contentEquals("/detail")) {
			
			int boardNo = Integer.parseInt(request.getParameter("no"));
			
			try {
				Board board = boardService.selectBoard(boardNo);
				
				if(board != null) {
					
					List<Attachment> files = boardService.selectFiles(boardNo);
					
					if(!files.isEmpty()) {
						request.setAttribute("files", files);
					}
					
					
					path = "/WEB-INF/views/board/boardDetail.jsp";
					request.setAttribute("board", board);
					view = request.getRequestDispatcher(path);
					view.forward(request, response);
				}else {
					request.getSession().setAttribute("msg", "게시글 상세 조회 실패");
					response.sendRedirect("list");
				}
				
				
			}catch(Exception e) {
				ExceptionForward.errorPage(request, response, "게시글 상세 조회", e);
			}
			
		}
		
		
		// 이미지 다운로드용 Controller
		else if(command.equals("/download")) {
			int fNo = Integer.parseInt(request.getParameter("fNo"));
			
			try {
				Attachment file = new BoardService().selectFile(fNo);
				
				if(file != null) {
					
					// 클라이언트로  파일을 내보낼 출력 스트림 연결(Servlet 기본 제공 클래스) OutputStream -> 웹 페이지로 내보낼 때 쓰임
					ServletOutputStream downOut = response.getOutputStream();
					
					// 스트림을 통해 내보낼 파일 객체 생성
					// File 객체를 통하여 해당 파일에 접근 가능
					File downloadFile = new File(file.getFilePath() + file.getFileChangeName());
					
					// 폴더에서 파일을 읽을 스트림 생성
					// 파일을 바이트 단위로 읽어들여 버퍼에 담아 스트림을 통해 내보냄
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(downloadFile));
					
					
					// 파일명을 changeName이 아닌 originName으로 다운받을 수 있도록 처리
					/*
					 * Content-Disposition 
					 * - 파일 다운로드를 처리하는 HTTP 헤더
					 * - 웹 서버 응답에 이 헤더를 포함하면 해당 파일 데이터를 다운로드 받도록 설정할 수 있음. 
					 *
					 * attachment; 
					 * - 파일을 다운로드 받을 수 있게 해주는 속성
					 *  
					 * filename="파일명"
					 * - 다운로드를 받는 사용자에게 보여질 파일명 
					 * */
					response.setHeader("Content-Disposition", "attachment; filename=\"" 
							+ new String(file.getFileOriginName().getBytes("UTF-8"), "ISO-8859-1") + "\"");
					// 파일명 문자 인코딩 변환
					// originName의 문자 인코딩 : UTF-8
					// 웹 브라우저에서 다운로드시 파일명 문자 인코딩 : ISO-8859-1

					// 스트림을 통해 내보낼 파일의 크기 지정
					response.setContentLength((int)downloadFile.length());
					
					int readBytes = 0;
					while((readBytes = bis.read()) != -1) {
						downOut.write(readBytes);
					}
					
					// 사용 스트림 반환
					bis.close();
					downOut.close();
				}
			
			}catch (Exception e) {
				ExceptionForward.errorPage(request, response, "이미지 다운로드", e);
			}
		}
		
		// 댓글 등록용 Controller
		else if(command.equals("/insertReply")) {
			int replyWriter = Integer.parseInt(request.getParameter("writer"));
			int boardId = Integer.parseInt(request.getParameter("boardNo"));
			String replyContent = request.getParameter("content");
			
			Reply reply = new Reply(replyContent, boardId);
			
			try {
				int result = boardService.insertReply(reply, replyWriter);
				
				// 문자를 내보낼 수 있는 스트링. 삽입결과를 내보내겠다.
				response.getWriter().print(result); 
				
			}catch(Exception e) {
				ExceptionForward.errorPage(request, response, "댓글 등록", e);
			}
			
		}
		
		// 댓글 리스트 출력용 Controller
		else if(command.equals("/selectReplyList")) {
			
			int boardId = Integer.parseInt(request.getParameter("boardNo"));
			
			try {
				List<Reply> rList = boardService.selectReplyList(boardId);
				
				response.setCharacterEncoding("UTF-8");
				
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				
				gson.toJson(rList, response.getWriter());
				
			}catch(Exception e) {
				ExceptionForward.errorPage(request, response, "댓글 조회", e);
			}
			
		}
		
		
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}

















































