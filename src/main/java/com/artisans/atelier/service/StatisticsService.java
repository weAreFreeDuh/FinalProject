package com.artisans.atelier.service;


import com.artisans.atelier.dao.MemDAO;
import com.artisans.atelier.dao.StatisticsDAO;

import com.artisans.atelier.dto.StaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private ModelAndView mav;

    private final StatisticsDAO sdao;
    private final MemDAO mdao;

    private final HttpSession session;

    FISH fish = new FISH();


    // 회원 전체 통계
    public List<StaDTO> STA001() {
        String sql = "SELECT * FROM (SELECT COUNT(MEMCODE) AS StaNum FROM MEMBER) UNION ALL " +
                "(SELECT COUNT(MEMCODE) AS StaNum FROM MEMBER WHERE MEMSTATE NOT IN ('휴면','블랙','미인증') AND MEMAUTCODE =0)UNION ALL " +
                "(SELECT COUNT(MEMCODE) AS StaNum FROM MEMBER WHERE MEMSTATE ='휴면') UNION ALL " +
                "(SELECT COUNT(MEMCODE) AS StaNum FROM MEMBER WHERE MEMAUTCODE !=0)";
       // System.out.println(sdao.STADATA(sql));
        return sdao.STADATA(sql);
    }

    // 회원 증가 추이 통계
    public List<StaDTO> STA002(int year) {
        List<StaDTO> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int checkYear = now.getYear();
        int monRange =12;
        if(checkYear == year){
            monRange = now.getMonthValue();
        }

        for (int i = 1; i <= monRange; i++) {
            StaDTO dto = new StaDTO();
            String month;

            // 1~9월까지는 숫자 앞에 0을 붙여준다.
            if (i < 10) {
                month = "0" + String.valueOf(i);
            } else {
                month = String.valueOf(i);
            }

            // 구하는 범위의 다음년도 와 다음달 구하기
            int nextYear;
            String nextMonth;
            if (i < 12) {
                nextYear = year;

                if (i < 9) {    // 9월의 다음달은 10월이기 때문.
                    nextMonth = "0" + String.valueOf((i + 1));
                } else {
                    nextMonth = String.valueOf((i + 1));
                }

            } else { // 12월일시 다음년도 1월1일까지
                nextYear = year + 1;
                nextMonth = "01";
            }
            // 월별 추가 회원
            String sql = "SELECT COUNT(MEMDATE) FROM MEMBER WHERE MEMDATE BETWEEN '" + year + "-" + month + "-01' AND '" + nextYear + "-" + nextMonth + "-01'";
            int staNum = mdao.everyCount(sql);
            dto.setStaNum(staNum);

            // 월별 전체 회원
            sql = "SELECT COUNT(MEMDATE) FROM MEMBER WHERE MEMDATE < '" + nextYear + "-" + nextMonth + "-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum2(staNum);

            dto.setStaName(i + "월");
            result.add(dto);
        }


        return result;
    }

    // 매출
    public List<StaDTO> STA003(int year) {
        List<StaDTO> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int checkYear = now.getYear();
        int monRange =12;
        if(checkYear == year){
            monRange = now.getMonthValue();
        }


        for (int i = 1; i <= monRange; i++) {
            StaDTO dto = new StaDTO();
            String month;

            // 1~9월까지는 숫자 앞에 0을 붙여준다.
            if (i < 10) {
                month = "0" + String.valueOf(i);
            } else {
                month = String.valueOf(i);
            }

            // 구하는 범위의 다음년도 와 다음달 구하기
            int nextYear;
            String nextMonth;
            if (i < 12) {
                nextYear = year;

                if (i < 9) {    // 9월의 다음달은 10월이기 때문.
                    nextMonth = "0" + String.valueOf((i + 1));
                } else {
                    nextMonth = String.valueOf((i + 1));
                }

            } else { // 12월일시 다음년도 1월1일까지
                nextYear = year + 1;
                nextMonth = "01";
            }

            // 누적매출
            String sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('포인트','멤버쉽') AND SALDATE < '"+nextYear+"-"+nextMonth+"-01'";
            int staNum = mdao.everyCount(sql);
            dto.setStaNum(staNum);

            // 월별 매출
            sql ="SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('포인트','멤버쉽') AND SALDATE BETWEEN '"+year+"-"+month+"-01' AND '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum2(staNum);

            // 누적 포인트 매출
            sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('포인트') AND SALDATE < '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum3(staNum);

            // 월별 포인트 매출
            sql ="SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('포인트') AND SALDATE BETWEEN '"+year+"-"+month+"-01' AND '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum4(staNum);

            // 누적 멤버쉽 매출
            sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('멤버쉽') AND SALDATE < '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum5(staNum);

            // 월별 멤버쉽 매출
            sql ="SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('멤버쉽') AND SALDATE BETWEEN '"+year+"-"+month+"-01' AND '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum6(staNum);

            // 누적 환급
            sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('환급') AND SALDATE < '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum7((staNum*-1));

            // 월별 환급
            sql ="SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('환급') AND SALDATE BETWEEN '"+year+"-"+month+"-01' AND '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum8((staNum*-1));

            // 누적 보유금
            sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE  SALDATE < '"+nextYear+"-"+nextMonth+"-01'";
            staNum = mdao.everyCount(sql);
            dto.setStaNum9(staNum);

            dto.setStaName(i + "월");
            result.add(dto);
        }
        return result;
    }

    // 통계 기본 정보(총매출액, 총보유금)
    public StaDTO STA004() {
        StaDTO result = new StaDTO();
        // 총매출
        String sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES WHERE SALNAME IN ('포인트','멤버쉽')";
        int staNum = mdao.everyCount(sql);
        result.setStaNum(staNum);

        // 보유금
        sql = "SELECT NVL2(SUM(SALPRICE),SUM(SALPRICE),0) AS staNum FROM SALES";
        staNum = mdao.everyCount(sql);
        result.setStaNum2(staNum);

        return result;
    }

    // 게시판 통계
    public List<StaDTO> STA005(int year) {
        // 카테고리별 게시글 수 삽입
       String sql ="SELECT CATEGORY AS STANAME,SUM(BOACOUNT) AS STANUM FROM BOCATEGORY INNER JOIN (SELECT BOACATECODE, COUNT(BOACODE) AS BOACOUNT FROM BOARD GROUP BY BOACATECODE) ON BOACATECODE = CATECODE WHERE CATECODE !=0 GROUP BY CATEGORY";
        return sdao.STADATA(sql);
    }

    // 게시판 좋아요 통계
    public List<StaDTO> STA006(int year) {
        // 카테고리별 좋아요 갯수 가져왓
        String sql ="SELECT CATEGORY AS STANAME,SUM(boalike) AS STANUM FROM BOCATEGORY INNER JOIN (SELECT BOACATECODE, sum(likenum) AS boalike FROM finalboardlist GROUP BY BOACATECODE) ON BOACATECODE = CATECODE WHERE CATECODE !=0 GROUP BY CATEGORY";
        return sdao.STADATA(sql);
    }

    // 게시판 조회수 통계
    public List<StaDTO> STA007(int year) {
        String sql ="SELECT CATEGORY AS STANAME,SUM(boaHits) AS STANUM FROM BOCATEGORY INNER JOIN (SELECT BOACATECODE, sum(boaHit) AS boaHits FROM finalboardlist GROUP BY BOACATECODE) ON BOACATECODE = CATECODE WHERE CATECODE !=0 GROUP BY CATEGORY";
        return sdao.STADATA(sql);
    }

    // 트렌드 전반 통계
    public List<Object> STA008(int year) {
        List<Object> result = new ArrayList<>();
        
        // 날짜
        LocalDate now = LocalDate.now();
        int checkYear = now.getYear();
        int monRange =12;
        if(checkYear == year){
            monRange = now.getMonthValue();
        }
        String month;
        int nextYear;
        String nextMonth;

        // 재사용할 DTO, sql
        StaDTO dto = new StaDTO();
        String sql;



        // 월별 경매, 포인트 결제 금액 비율 -> 월별 단위인것은 아래 for문 안에 에 추가로 넣을것
        List<StaDTO> auLi = new ArrayList<>();
        for (int i = 1; i <= monRange; i++) {

            // 1~9월까지는 숫자 앞에 0을 붙여준다.
            if (i < 10) {
                month = "0" + String.valueOf(i);
            } else {
                month = String.valueOf(i);
            }

            // 구하는 범위의 다음년도 와 다음달 구하기

            if (i < 12) {
                nextYear = year;

                if (i < 9) {    // 9월의 다음달은 10월이기 때문.
                    nextMonth = "0" + String.valueOf((i + 1));
                } else {
                    nextMonth = String.valueOf((i + 1));
                }

            } else { // 12월일시 다음년도 1월1일까지
                nextYear = year + 1;
                nextMonth = "01";
            }
            // 날짜 sql
            String sqlDate = " BETWEEN '"+year+"-"+month+"-01' AND '"+nextYear+"-"+nextMonth+"-01'";

            // 입력할 sql
            sql = " SELECT *  FROM (SELECT NVL2(SUM(POIUPDATE),SUM(POIUPDATE),0) as staNum"+
                    " FROM pointhistory WHERE POICONTENT IN ('문학 게시글 판매') AND POIDATE"+sqlDate+" ),"+
                    " (SELECT NVL2(SUM(POIUPDATE),SUM(POIUPDATE),0) as staNum2"+
                    " FROM pointhistory WHERE POICONTENT IN ('상품 판매 수익') AND POIDATE"+sqlDate+" )";

            dto = sdao.MOEWSTADATA(sql);
            dto.setStaName(month+"월");
            auLi.add(dto);
        }


        // 최근 조회된 게시글들의 카테고리
        sql = "SELECT CATEGORY AS STANAME, SUM(CATENUM) AS STANUM"+
                " FROM("+
                " SELECT SUM(BOANUM) AS CATENUM, BOACATECODE"+
                " FROM(SELECT bhboacode, COUNT(BHBOACODE) AS BOANUM FROM BOARDHISTORY GROUP BY bhboacode)"+
                " INNER JOIN BOARD ON BOACODE = BHBOACODE"+
                " GROUP BY BOACATECODE)"+
                " INNER JOIN BOCATEGORY ON BOACATECODE = CATECODE"+
                " GROUP BY CATEGORY";
        List<StaDTO> manyHitCategory = sdao.STADATA(sql);
        
        // 최근 조회된 게시글들의 워크 스페이스 상위 10개
        sql = " SELECT STANAME, STANUM FROM("+
                " SELECT WORKTITLE AS STANAME, WORKNUM AS STANUM, ROW_NUMBER() OVER(ORDER BY WORKNUM DESC) AS RN FROM("+
                " SELECT SUM(BOANUM) AS WORKNUM, BOAWORKCODE"+
                " FROM(SELECT bhboacode, COUNT(BHBOACODE) AS BOANUM FROM BOARDHISTORY GROUP BY bhboacode)"+
                "  INNER JOIN BOARD ON BOACODE = BHBOACODE WHERE BOAWORKCODE !=0 GROUP BY BOAWORKCODE)"+
                "  INNER JOIN WORKSPACE ON WORKCODE = BOAWORKCODE"+
                " ) WHERE RN BETWEEN 1 AND 10";
        List<StaDTO> manyHitWorkSpace = sdao.STADATA(sql);

        // 최근 조회된 게시글들의 상위 10개
        sql =" SELECT STANAME, STANUM FROM("+
                " SELECT BOANUM AS STANUM, BOATITLE AS STANAME, ROW_NUMBER() OVER(ORDER BY BOANUM DESC) AS RN"+
                " FROM(SELECT bhboacode, COUNT(BHBOACODE) AS BOANUM FROM BOARDHISTORY GROUP BY bhboacode)"+
                " INNER JOIN BOARD ON BOACODE = BHBOACODE)"+
                " WHERE RN BETWEEN 1 AND 10";
        List<StaDTO> manyHitBoard = sdao.STADATA(sql);



        result.add(auLi);
        result.add(manyHitCategory);
        result.add(manyHitWorkSpace);
        result.add(manyHitBoard);

        return result;
    }

    //  남녀비율
    public List<StaDTO> STA009(int year) {
        List<StaDTO> result = new ArrayList<>();
        // 전체 남녀 비율
        String sql = "SELECT * FROM (SELECT COUNT(memgender) as STANUM FROM MEMBER WHERE MEMGENDER ='남자'), " +
                "(SELECT COUNT(memgender) as STANUM2 FROM MEMBER WHERE MEMGENDER ='여자'), " +
                "(SELECT COUNT(memgender) as STANUM3 FROM MEMBER WHERE MEMGENDER ='미입력')";
        result.add(sdao.MOEWSTADATA(sql));
        return result;
    }
}
