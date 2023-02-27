package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Date;

@Data
@Alias("loginHistory")
public class LHDTO {
    private int LHMemCode; // 회원코드
    private Date LHDate; // 날짜
    private String LHIp; // 접속 아이피
}
