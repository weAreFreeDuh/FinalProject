package com.artisans.atelier.dao;

import com.artisans.atelier.dto.StaDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface StatisticsDAO {

    // 전반적인 통계데이터
    List<StaDTO> STADATA(String sql);

    // 세부적으로 구해야하는 통계데이터
    StaDTO MOEWSTADATA(String sql);

    // 테스트
    List<Date> test1();

    // 숫자 리스트
    List<Integer> InteList(String sql);
}
