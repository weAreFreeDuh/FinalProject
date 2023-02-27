package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Date;

@Data
@Alias("coment")
public class COMENT {
	private int comCode;				// 댓글번호
	private int ComBoaCode;				// 게시글번호
	private int comMemcode;		// 댓글작성자
	private String coMent;		// 댓글내용
	private Date comDate;			// 댓글작성일
	private String memName;			// 댓글작성자이름
	private int coModi;			// 댓글수정햿는지
	private String memProfileName;			// 댓글수정햿는지
}
