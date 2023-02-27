package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("literaturepay")
@Data
public class LiteraturePayDTO {
    private int litBoaCode;    //결제한 게시글
    private int litMemCode;     // 결제하는 회원
    private String litDate;     // 결제한 날짜

}
