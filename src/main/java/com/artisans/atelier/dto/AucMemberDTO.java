package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("aucMember")
public class AucMemberDTO {
    private int AucProCode;     // 상품 코드
    private int AucMemCode;     // 구매 희망하는 사람
    private int AucPrice;       // 구매 희망가격
    private String memId; // 회원 아이디(이메일)
    private String memName; // 회원 닉네임
    private String memBirth; // 회원 생일
    private String memGender; // 회원 성별
    private String memAddr; // 회원 주소
    private String memPhone; // 회원 전화번호
}
