package com.kh.sjproject.board.model.vo;

import java.sql.Timestamp;

public class Reply {
	private int replyNo;
	private String replyContent;
	private int boardId;
	private String replyWriter;
	private Timestamp replyCreateDate;
	private Timestamp replyModifyDate;
	private String status;
	
	public Reply() {}

	public Reply(String replyContent, int boardId) {
		super();
		this.replyContent = replyContent;
		this.boardId = boardId;
	}
	
	

	public Reply(int replyNo, String replyContent, int boardId, String repalyWriter, Timestamp replyModifyDate) {
		super();
		this.replyNo = replyNo;
		this.replyContent = replyContent;
		this.boardId = boardId;
		this.replyWriter = repalyWriter;
		this.replyModifyDate = replyModifyDate;
	}

	public Reply(int replyNo, String replyContent, int boardId, String repalyWriter, Timestamp replyCreateDate,
			Timestamp replyModifyDate, String status) {
		super();
		this.replyNo = replyNo;
		this.replyContent = replyContent;
		this.boardId = boardId;
		this.replyWriter = repalyWriter;
		this.replyCreateDate = replyCreateDate;
		this.replyModifyDate = replyModifyDate;
		this.status = status;
	}

	public int getReplyNo() {
		return replyNo;
	}

	public void setReplyNo(int replyNo) {
		this.replyNo = replyNo;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public String getRepalyWriter() {
		return replyWriter;
	}

	public void setRepalyWriter(String repalyWriter) {
		this.replyWriter = repalyWriter;
	}

	public Timestamp getReplyCreateDate() {
		return replyCreateDate;
	}

	public void setReplyCreateDate(Timestamp replyCreateDate) {
		this.replyCreateDate = replyCreateDate;
	}

	public Timestamp getReplyModifyDate() {
		return replyModifyDate;
	}

	public void setReplyModifyDate(Timestamp replyModifyDate) {
		this.replyModifyDate = replyModifyDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Reply [replyNo=" + replyNo + ", replyContent=" + replyContent + ", boardId=" + boardId
				+ ", repalyWriter=" + replyWriter + ", replyCreateDate=" + replyCreateDate + ", replyModifyDate="
				+ replyModifyDate + ", status=" + status + "]";
	}

	
}
