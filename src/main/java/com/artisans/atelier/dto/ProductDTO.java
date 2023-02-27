package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@Alias("product")
public class ProductDTO {

    private int proCode;        // 상품코드
    private int proMemCode;        // 회원코드
    private int proBoaCode;     // 게시글 코드
    private int proPrice;       // 상품 최소가격
    private String proName;     // 상품 이름
    private Date proStartDate;    // 경매 시작 시간
    private Date proEndDate;      // 경매 끝나는 시간

}
