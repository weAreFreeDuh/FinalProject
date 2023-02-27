package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Alias("workspace")
public class WorkSpaceDTO {
    private int workCode;   // 워크스페이스(시권스) PK
    private int workCateCode;   // 카테고리 코드 FK
    private int workMemCode;    // 회원정보 코드 FK
    private String workTitle;   //워크 스페이스 제목
    private String workIntro;   //워크 스페이스 설명

    private MultipartFile workImg; // 워크스페이스 이미지 파일.
    private String workImgName;     // 워크스페이스 대표이미지 파일 이름

    private String workTag;     // 워크 스페이스 태그
    
    /////////////////// 카테고리
    
    private String category;    // 카테고리
    private String subCategory;     // 서브 카테고리

    ///////////////////// 게시글 연결 변수

    private List boaCodeList; // 게시글 연결 리스트

    ////////////////////// sql 문 실행용

    private String sql; // sql 문 실행문



}
