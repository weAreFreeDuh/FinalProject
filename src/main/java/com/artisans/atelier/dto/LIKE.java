package com.artisans.atelier.dto;

import lombok.Data;

@Data
public class LIKE {

	private int boaCode; // 게시물코드
	private int boaMemcode; // 사용자 아이디
	private int MLDo; // 전에 좋아요 눌렀나?
	private int MLCount;	// 좋아요가 몇개 달렸나?
}
