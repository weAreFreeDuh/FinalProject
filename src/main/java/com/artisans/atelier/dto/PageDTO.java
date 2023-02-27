package com.artisans.atelier.dto;


import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("paging")
public class PageDTO {

    private int page;			// 현재페이지
    private int maxpage;		// 최대페이지
    private int startpage; 		// 시작페이지
    private int endpage;		// 마지막페이지
    private int startRow;		// 페이지 시작 게시글 번호
    private int endRow;			// 페이지 마지막 게시글번호
    private int limit;			// 한 페이지에 출력할 게시글 개수

    /////////////////////////////////////////

    private String category; // 리스트 처리를 위한 category
    private int cateCode; // 리스트 처리를 위한 cateCode
    
    //////////////////////////////////////////
    
    private String orderby; // 정렬을 위한 값

    /////////////////////////////////////////// 성호 합침


    private String sql;         // 페이징에 조건 부여시 사용할 sql문

    private String search; // 검색

    /////////////////////////////////////////

    private int workCode; // 워크스페이스 시퀀스ㄴ
}
