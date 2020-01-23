<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List, com.kh.sjproject.notice.model.vo.Notice" %>	

<%

	List<Notice> list = (List<Notice>)request.getAttribute("list");

	// 이전 페이지 요청 (request)에 있는 파라미터를 얻어옴.
	String searchKey = request.getParameter("searchKey");
	String searchValue = request.getParameter("searchValue");

%>	
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지사항</title>
    <style>
    	.pagination {
            justify-content: center;
        }
        #searchForm{
            position: relative;
        }

        #searchForm>*{
            top : 0;
        }
	</style>
</head>
<body>
	<div class="container">
		<%@ include file="../common/header.jsp"%>
		<%@ include file="../common/nav.jsp"%>

		<div class="container">
	        <div>
	            <table class="table table-hover table-striped" id="list-table">
	                <thead>
	                    <tr>
	                        <th>글번호 </th>
	                        <th>제목</th>
	                        <th>작성자</th>
	                        <th>조회수</th>
	                        <th>작성일</th>
	                    </tr>
	                </thead>
	                <tbody>
	                	<% if(list.isEmpty()) {%>
	                	<tr>
							<td colspan="5">존재하는 공지사항이 없습니다.</td>	                	
	                	</tr>
	                	<%}else{ %>
	                		<% for(Notice notice : list){ %>
	                		<tr>
	                			<td><%= notice.getNoticeNo() %></td>
	                			<td><%= notice.getNoticeTitle() %></td>
	                			<td><%= notice.getNoticeWriter() %></td>
	                			<td><%= notice.getNoticeCount() %></td>
	                			<td><%= notice.getNoticeModifyDate() %></td>
	                		</tr>
	                		<%} %>
	                	<%} %>
	                </tbody>
	            </table>
	        </div>
	
	        <hr>
	        <%-- 로그인된 계정이 관리자 등급인 경우에만 글쓰기 버튼 노출 --%>
	        <% if(loginMember != null && loginMember.getMemberGrade().equals("A")){%>
	        <button type="button" class="btn btn-primary float-right" onclick="location.href='insertForm'">글쓰기</button>
	        <%} %>
	        <div style="clear: both;">
	            <ul class="pagination">
	                <li>
	                    <a class="page-link" href="#">Previous</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">1</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">2</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">3</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">4</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">5</a>
	                </li>
	                <li>
	                    <a class="page-link" href="#">Next</a>
	                </li>
	            </ul>
	        </div>
	        <div>
	            <form method="GET" action="search" class="text-center" id="searchForm">
	                <select name="searchKey" class="form-control" style="width:100px; display: inline-block;">
	                    <option value=title>글제목</option>
	                    <option value=content>내용</option>
	                    <option value=titcont>제목+내용</option>
	                </select>
	                <input type="text" name="searchValue" class="form-control" style="width:25%; display: inline-block;">
	                <button class="form-control btn btn-primary" style="width:100px; display: inline-block;">검색</button>
	            </form>
	            
	            <script>
	            
	            $(funcfion(){
	            	var searchKey = "<%= searchKey %>";
	            	var searchValue= "<%= searchValue %>";
	            	
	            	if(searchKey != null && searchValue != "null"){
	            		$.each((select[name=seachkey]>option),funtion)
	            	
	            	if($(item).val() == searchKey){
	            		// $(item) : 현재 접근 요소
	            		$(item).prop("selected", "true");
	            	}
	            	 
	            	$("input[name=searchValue]").val(searchValue);
	            	
	            });
	            
	            </script>
	            
	            
	            
	            
	            
	        </div>
    	</div>
		<%@ include file="../common/footer.jsp"%>
	</div>
	
	<script>
		// 공지사항 상세조회 기능
		$(function(){
			$("#list-table td").on("click",function(){
				var noticeNo = $(this).parent().children().eq(0).text();
				// 이벤트가 발생한 td의 부모 tr의 자손의 0번재 인덱스의 텍스트를 가져옴
				
				// 쿼리스트링을 이용하여 GET 방식으로 글 번호를 server로 전달
				location.href="<%= request.getContextPath()%>/notice/detail?no="+noticeNo;
				
				
			}).on("mouseenter",function(){
				$(this).parent().css("cursor","pointer");
			});	
		});
	</script>
	
	
	
</body>
</html>


































































