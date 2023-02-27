package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Alias("member")
public class MemberDTO {
    private int memCode; // 회원 코드
    private String memId; // 회원 아이디(이메일)
    private String memPw; // 회원 비밀번호
    private String memName; // 회원 닉네임
    private String memBirth; // 회원 생일
    private String memGender; // 회원 성별
    private String memAddr; // 회원 주소
    private String memAddr1; // 회원 주소 받기 1
    private String memAddr2; // 회원 주소 받기 2
    private String memAddr3; // 회원 주소 받기 3

    private String memPhone; // 회원 전화번호
    private MultipartFile memProfile; // 회원 프로필 사진
    private String memProfileName; // 회원 프로필 사진 이름
    private String memGrade; // 회원 등급
    private String memPayGrade; // 회원 결제 등급
    private String memState; // 회원 계정 상태(미인증/인증/블랙/경고)
    private int memAutCode; // 회원 권한 코드
    private String AutName; // 회원 권한 코드의 이름 - > 직원 검색용
    private int memPoint; // 회원 포인트
    private String memBackGround; // 프로필 백그라운드
    private Date memDate;   // 회원가입일

    private String memIp; // ip 주소

    private String memIntro;    // 회원인트로


}
