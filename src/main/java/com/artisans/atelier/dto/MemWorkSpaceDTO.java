package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Alias("memWorkspace")
public class MemWorkSpaceDTO {
    private int workCode;   // 워크스페이스(시권스) PK
    private int workCateCode;   // 카테고리 코드 FK
    private int workMemCode;    // 회원정보 코드 FK
    private String workTitle;   //워크 스페이스 제목
    private String workIntro;   //워크 스페이스 설명

    private MultipartFile workImg; // 워크스페이스 이미지 파일.
    private String workImgName;     // 워크스페이스 대표이미지 파일 이름

    ///////////////////////////////////////

    private String workTag;     // 워크 스페이스 태그

    private int memCode; // 회원 코드
    private String memId; // 회원 아이디(이메일)
    private String memName; // 회원 아이디(이메일)

    private MultipartFile memProfile; // 회원 프로필 사진
    private String memProfileName; // 회원 프로필 사진 이름

    private String memGrade; // 회원 등급
    private String memPayGrade; // 회원 결제 등급

    //////////////////////////////////////////

    private String category;    // 카테고리
    private String subCategory;     // 서브 카테고리

    ///////////////////// 게시글 연결 변수

    private List boaCodeList; // 게시글 연결 리스트
    /*private List workTagList;     // 워크 스페이스 태그*/

    ///////////////////////////// test

   /* private TagListDTO tagListDTO;    // 태그 연결 DTO*/

    ////////////////////// sql 문 실행용

    private String sql; // sql 문 실행문

    ///////////////////////////////////////////

    private int likeNum;        // 연결된 게시글 총 좋아요 갯수
    private int comentNum;      // 연결된 게식르 총 댓글 갯수
    private int HitNum;     // 연결된 게시글 조회 수
    private int BoardNum;       // 연결된 게시글 게시글 수

    ///////////////////////////////////////////

    private int litWorkCode; //워크스페이스 코드
    private String litMethod;   // 문학워크스페이스 유로화 무료화
    private int litCount;   // 워크스페이스 몇화부터 무료화 할지 설정
    private int litPrice;   // 가격

    private int boaCode;    // 게시글 코드
    private int payMemCode;     //게시글 결제 관련 회원코드
}
