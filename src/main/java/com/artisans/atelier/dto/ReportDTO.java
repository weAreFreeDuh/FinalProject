package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

@Data
@Alias("report")
public class ReportDTO {
    private int repCode;    // 신고코드
    private String repState; // 신고 상태
    private String repTitle; // 신고 제목
    private int repMemCode; // 신고자 회원코드
    private String repContent; // 신고내용
    private MultipartFile repFile; // 사진파일
    private String repFileName; // 사진이름
}
