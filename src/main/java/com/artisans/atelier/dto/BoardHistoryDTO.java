package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("boardhistory")
public class BoardHistoryDTO {
    
    private String bhBoaCode; /*게시글 코드*/
    private String bhMemCode;   /*회원코드*/
    private String bhDate;  /*날짜*/

}
