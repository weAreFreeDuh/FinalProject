package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("literature")
@Data
public class LiteratureDTO {
    private int litWorkCode; //워크스페이스 코드
    private String litMethod;   // 문학워크스페이스 유로화 무료화 
    private int litCount;   // 워크스페이스 몇화부터 무료화 할지 설정
    private int litPrice;   // 가격

    private int litBoaCode; // 보드 시퀀스

    private int memCode; // 회원 코드
    private int memPoint; // 회원 포인트

    private int memCodeSell; // 판매를 한 회원코드
    private int memCodeSellPoint; // 판매한 한 회원 포인트

}
