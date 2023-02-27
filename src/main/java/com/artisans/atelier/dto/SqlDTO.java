package com.artisans.atelier.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("SQLTXT")
public class SqlDTO {
    private String sql;
    private String table;
    private String where;
}
