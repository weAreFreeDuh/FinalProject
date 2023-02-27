package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("authority")
public class AuthorityDTO {
    private int autCode; // 권한 코드
    private String autName; // 권한 이름
    private int autGive; // 권한 부여 권한
    private int autSecu; // 보안, 차단 관련 권한
    private int autAcco; // 재정 관련 권한
    private int autAChat; // 관리자 문의 관련 권한
    private int autAMail; // 이메일 전송 관련 권한
    private int autCategory; // 카테고리 생성 관련 권한

}
