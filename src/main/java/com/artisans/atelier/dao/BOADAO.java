package com.artisans.atelier.dao;


import com.artisans.atelier.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BOADAO {
    List<BoCategoryDTO> ajaxcategory(String category);

    int bowrite(MemBoardDTO board);


    List<MemWorkSpaceDTO> DRA003_1();

    List<MemBoardDTO> DRA003();

    MemBoardDTO DRA004(int boaCode);

    List<MemWorkSpaceDTO> workspacelist(int workMemCode);

    int DRA005_1(LIKE like);

    void DRA005_2(LIKE like);

    void DRA005_3(LIKE like);

    int DRA005(int boaCode);

    List<COMENT> DRA006(int boaCode);

    void DRA006_1(COMENT coment);

    void DRA006_2(COMENT coment);

    int DRA006_3(COMENT coment);

    List<MemBoardDTO> DRA007(PageDTO paging);

    int bCount(String sql);

    void viewCountUp(int boaCode);

    List<MemWorkSpaceDTO> DRA007_1(PageDTO paging);

    List<MemWorkSpaceDTO> DRA008_1(PageDTO paging);

    List<MemBoardDTO> DRA008(PageDTO paging);

    int DRA009_1(MemBoardDTO board);

    int DRA010(int board);

    int bwCount(String category);

    int DRA011(BoardHistoryDTO boardhistory);

    int DRA011_1(BoardHistoryDTO boardhistory);

    void DRA011_2(BoardHistoryDTO boardhistory);

    void DRA011_3(int boaCode);

    /*카테고리 파악*/
    String getCategoryBoard(int boaCode);

    /*워크스페이스 갯수 구하기*/
    int bwCountsql(String sql);


    void DRA010_1(int boaCode);

    void DRA010_2(int boaCode);

    void DRA010_3(int boaCode);

    void DRA010_4(int boaCode);

    int likenum(int boaCode);
}
