package com.artisans.atelier.service;

import java.util.List;

/*준호의 메소드*/
public class DDAN {

    /*워크스페이스에 게시글 넣기*/
    public String tigBoard(int workCode, int workCateCode, List BoaCode){
        String sql = "update board set boaWorkcode = "+workCode+" ,boaCatecode = "+ workCateCode;
        for (int i=0; i<BoaCode.size(); i++){
            if (i == 0) {
                sql += " where ";
                sql += "boaCode = " + BoaCode.get(i);

            }else {
                sql += " OR boaCode = "+BoaCode.get(i);
            }
        }
        System.out.println("sql == "+sql);

        return sql;
    }

    /*태그 안에 , 없애기*/
    public String tagClean(String tag){
        return tag.replaceAll(",", " ");
    }
}
