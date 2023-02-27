package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Alias("memboard")
public class MemBoardDTO {

    private int boaCode;        // 게시글 시퀀스
    private int boaCatecode;    // 카테고리 코드
    private int boaWorkcode;    //워크스페이스 상속 코드
    private String boaTitle;       // 제목
    private int boaMemcode;     // 회원코드
    private String boaContent;     // 파일 이름과 내용
    private String boaContentname;     // 파일 이름
    private int boaHit;     // 조회수
    private String boaDate;    // 작성날짜
    private String boaTag; // 태그

    private MultipartFile boaFile;    /*첨부파일 예)mp3*/
    private String boaFileName;   /*첨부파일 이름*/

    private MultipartFile boaBestFile;  /*대표이미지*/
    private String boaBestImg;  // 대표 이미지 파일 이름
    private List imgname;

    //////////////////////////////////////////
    
    private String memId; // 회원 아이디
    private String memName; // 회원 이름

    private MultipartFile memProfile; // 회원 프로필 사진
    private String memProfileName; // 회원 프로필 사진 이름

    private String memGrade; // 회원 등급
    private String memPayGrade; // 회원 결제 등급

    /////////////////////////////////////////////

    private String category;    // 카테고리
    private String subCategory;     // 서브 카테고리

    //////////////////////////////////////////////

    private String sql; // sql문 작성용

    //////////////////////////////////////////////
    private int likeNum;        // 연결된 게시글 총 좋아요 갯수
    private int comentNum;      // 연결된 게식르 총 댓글 갯수
}
