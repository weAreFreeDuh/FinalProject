package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("auction")
public class AuctionDTO {

    private int AucProCode;     // 상품 코드
    private int AucMemCode;     // 구매 희망하는 사람
    private int AucPrice;       // 구매 희망가격

}
