package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("login")
public class LoginDTO {
    private int memCode; // 회원 코드
    private String memName; // 회원 닉네임
    private String memProfileName; // 회원 프로필 사진 이름
    private String memState; // 회원 계정 상태(미인증/인증/블랙/경고)
    private int autCode; // 권한 번호
    private int autGive; // 권한 부여 권한
    private int autSecu; // 보안, 차단 관련 권한
    private int autAcco; // 재정 관련 권한
    private int autChat; // 관리자 문의 관련 권한
    private int autAMail; // 이메일 전송 관련 권한
    private int autCategory; // 카테고리 생성 관련 권한
    private int memPoint;   // 회원 포인트
    private String memGrade;    // 회원 등급
    private String memPayGrade; // 멤버쉽 등급


}
