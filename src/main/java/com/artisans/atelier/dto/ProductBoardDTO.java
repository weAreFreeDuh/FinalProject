package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@Alias("productBoard")
public class ProductBoardDTO {
    private int proCode;        // 상품코드
    private int proBoaCode;     // 게시글 코드
    private int proPrice;       // 상품 최소가격
    private String proName;     // 상품 이름
    private Date proStartDate;    // 경매 시작 시간
    private Date proEndDate;      // 경매 끝나는 시간
    private Date proDate;           // 현재 시간

    private int boaCatecode;
    private int boaWorkcode;
    private String boaTitle;
    private int boaMemcode;
    private String boaContent;  /*텍스트 파일*/
    private int boaHit;
    private Date boaDate;
    private String boaTag="null";
    private String bFileName;
    private String boaBestImg;
    private int likeNum;
    private int comentNum;
    private String category;
    private String memName;
    private String order;

    private String memProfileName;

}
