package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("sales")
public class SalesDTO {
    private int salCode;
    private int salMemCode;
    private String salName;
    private int salPrice;
    private String salDate;
    private String salContent;
    private int salPoint;

}
