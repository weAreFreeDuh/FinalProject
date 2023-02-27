package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("profile")
public class ProFileDTO {
    private String follow; // 자신이 팔로우한 갯수
    private String follower;   // 자신을 팔로우한 사람수
    private int boardPosts; // 올린 게시글 갯수
    private int memCode;    // 회원번호
    private String memName; // 닉네임
    private String memGrade;    // 회원 등급
    private String memPayGrade; // 페이등급
    private String memProfileName;  // 회원 프로필 사진
    private String memBackGround;   // 프로필 백그라운드 사진
    private String memIntro;    // 회원인트로
}
