package com.artisans.atelier.dto;


import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

@Data
@Alias("board")
public class BoardDTO {
    private int boaCode;
    private int boaCatecode;
    private int boaWorkcode;
    private String boaTitle;
    private int boaMemcode;
    private String boaContent;  /*텍스트 파일*/
    private int boaHit;
    private Date boaDate;
    private String boaTag="null";
    private List imgname;   /*저장x*/

    private MultipartFile bFile;    /*첨부파일 예)mp3*/
    private String bFileName;


    private MultipartFile boaBestFile;  /*대표이미지*/
    private String boaBestImg;


}
