package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("bocategory")
public class BoCategoryDTO {
    private int cateCode;   // 게시판 카테고리 시퀀스
    private String category;    // 카테고리 중복가능
    private String subCategory;     // 서브 카테고리(중복가능)
}
