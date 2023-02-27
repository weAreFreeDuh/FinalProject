package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("index")
@Data
public class IndexDTO {
    private int boardNum;     // 게시판 글 개수
    private int memberNum;      // 회원 수
    private int productNum;     // 상품 개수
    private int memberGradeNum;     // 멤버쉽 가입 회원 수
}
