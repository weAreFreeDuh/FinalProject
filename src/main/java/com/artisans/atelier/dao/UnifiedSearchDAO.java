package com.artisans.atelier.dao;

import com.artisans.atelier.dto.MemBoardDTO;
import com.artisans.atelier.dto.MemWorkSpaceDTO;
import com.artisans.atelier.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UnifiedSearchDAO {

    // 통합검색 - 보드 검색
    List<MemBoardDTO> BoardSearch(String sql);

    // 통합검색 - 워크스페이스 검색
    List<MemWorkSpaceDTO> WorkSearch(String sql);

    // 통함 검색 -워크스페이스 상세보기
    List<MemWorkSpaceDTO> WorkMoreSearch(PageDTO paging);

    // 통합검색 - 게시글 상세보기
    List<MemBoardDTO> BoardMoreSearch(PageDTO paging);

    // 태그 중심 추천
    List<String> TagRecommand(int memCode);
}
