package com.artisans.atelier.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.artisans.atelier.dao.BOADAO;
import com.artisans.atelier.dao.MemDAO;
import com.artisans.atelier.dao.PayDAO;
import com.artisans.atelier.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayService {

    private ModelAndView mav;


    private final PayDAO pdao;
    private final BOADAO bbdao;

    // 결제 및 반환 세션에 사용
    private final MemDAO mdao;

    FISH fish = new FISH();


    private final HttpSession session;

    // 카카오페이 결제창 이동
    public ModelAndView PAY004(SalesDTO sales) {
        mav = new ModelAndView();

        System.out.println("PayService(sales) : " + sales);

        session.setAttribute("salMemCode", sales.getSalMemCode());
        session.setAttribute("salName", sales.getSalName());
        session.setAttribute("salPrice", sales.getSalPrice());
        session.setAttribute("salContent", sales.getSalContent());
        mav.setViewName("Pay_Kakaopay");

        /*int result = pdao.PAY007(sales);*/


        return mav;
    }
    
    // 포인트 결제 후
    public ModelAndView PAY007(SalesDTO sales) {
        mav = new ModelAndView();

        System.out.println("Pay007Service : " + sales);

        // sales 테이블 삽입
        int result = pdao.PAY007(sales);
        System.out.println("result : " + result);

        try{
            // PointHistory 테이블 삽입
            int result1 = pdao.pHistory(sales);
            System.out.println("result1 : " + result1);

            try{
                // Member point update
                int result2 = pdao.memPoint(sales);
                System.out.println("result2 : " + result2);
                mav.setViewName("index");
                LoginDTO login = mdao.login(sales.getSalMemCode());
                session.invalidate();
                session.setAttribute("login", login);

            } catch (Exception e){
                mav.setViewName("Pay_Payment");
            }

        } catch (Exception e){
                mav.setViewName("Pay_Payment");
        }

        return mav;
    }
    
    // 멤버쉽 결제 후
    public ModelAndView PAY008(SalesDTO sales) {
        mav = new ModelAndView();

        System.out.println("Pay008Service : " + sales);

        // 멤버 vip로 바꿔주기
        int result = pdao.memGrade(sales);
        System.out.println("memGrade RESULT : " + result);

        try{
            // sales 테이블 삽입(매출정보)
            int result1 = pdao.PAY008(sales);
            System.out.println("PAY008 RESULT1 : " + result1);

                try{
                    mav.setViewName("index");
                    LoginDTO login = mdao.login(sales.getSalMemCode());
                    session.invalidate();
                    session.setAttribute("login", login);
                }catch (Exception e){
                    mav.setViewName("Pay_Payment");
                }


            } catch (Exception e){
                mav.setViewName("Pay_Payment");
            }


        return mav;
    }
    
    // 포인트 반환
    public ModelAndView PAY014(SalesDTO sales) {
        mav = new ModelAndView();

        System.out.println("Pay014Service : " + sales);

        // sales 매출테이블 추가
        int result = pdao.PAY014(sales);
        System.out.println("PAY014 RESULT : " + result);

        try{
            // 포인트 히스토리 추가
            int result1 = pdao.RePointHistory(sales);
            System.out.println("RePointHistory RESULT1 : " + result1);

            try{
                int result2 = pdao.RememPoint(sales);
                System.out.println("RememPoint RESULT2 : " + result2);
                try {
                    mav.setViewName("index");
                    LoginDTO login = mdao.login(sales.getSalMemCode());
                    session.invalidate();
                    session.setAttribute("login", login);
                }catch (Exception e){
                    mav.setViewName("Pay_ReturnPoint");
                }
            }catch (Exception e){
                mav.setViewName("Pay_Payment");
            }


        } catch (Exception e){
            mav.setViewName("Pay_Payment");
        }


        return mav;
    }

    public ModelAndView PAY003(ProductDTO product) {
        mav = new ModelAndView();

        System.out.println("PAY003 Service(product): " + product);

        int result = pdao.PAY003(product);
        System.out.println("PAY003Service(result) : " + result);

        System.out.println("product : " + product);

        int result1 = pdao.auctionStart(product);
        System.out.println("auctionStart result1 : " + result1);
        mav.setViewName("Pay_PayList");



        try{
            mav.setViewName("Pay_PayList");

        } catch (Exception e){
            mav.setViewName("index");
        }


        return mav;
    }

    public List<Object> productAll(int page) {
        // 상품 게시판에서 게시글 가져오기

            // 상품 게시판 게시글 전체 갯수 가져오기
            int count = mdao.everyCount("SELECT COUNT(*) FROM PRODUCT");

            System.out.println("productAll(conut) : " + count);

            // 페이징 처리
            PageDTO paging = fish.paging(page, 12, count);

            // sql 셋팅
            paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROENDDATE ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");

            // 게시물 가져오기
            List<ProductBoardDTO> productAll =  pdao.productAll(paging);

            System.out.println("productAll(service) : " + productAll);

            // 현재 페이지와 페이징처리된 게시글 리스트보내기
            List<Object> result = new ArrayList<>();

            // 다음페이지로 셋팅
            paging.setPage(page+1);

            // sql문 삭제
            paging.setSql(null);

            // 리스트에 페이징과 워크 리스트 첨부
            result.add(paging);
            result.add(productAll);
            return result;
        }

    public List<Object> productCategory(String category, int page) {
          // 상품 게시판에서 카테고리별 가져오기

            System.out.println("category" + category);

            // 상품 게시판 게시글 전체 갯수 가져오기
            int count = mdao.everyCount("SELECT COUNT(*) FROM PRODUCTBOARD WHERE CATEGORY = '"+category+"'");

            System.out.println("productCategory(conut) : " + count);

            // 페이징 처리
            PageDTO paging = fish.paging(page, 12, count);

            // sql 셋팅
            paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROENDDATE ASC) AS RN FROM PRODUCTBOARD WHERE CATEGORY = '"+category+"') WHERE RN BETWEEN #{startRow} AND #{endRow}");

            // 게시물 가져오기
            List<ProductBoardDTO> productCategory =  pdao.productCategory(paging);

            System.out.println("productCategory(productCategory) : " + productCategory);

            // 현재 페이지와 페이징처리된 게시글 리스트보내기
            List<Object> result = new ArrayList<>();

            // 다음페이지로 셋팅
            paging.setPage(page+1);

            // sql문 삭제
            paging.setSql(null);

            // 리스트에 페이징과 워크 리스트 첨부
            result.add(paging);
            result.add(productCategory);
            return result;

    }

    public List<Object> ProSelect(String category, int page, String order) {
        // 상품 게시판에서 select별 가져오기

        System.out.println("(select)category" + category);
        System.out.println("(select)order" + order);
        int count = 0;
        if(category.equals("전체")) {
            count = mdao.everyCount("SELECT COUNT(*) FROM PRODUCT");
            System.out.println("(select)productCategory(conut) : " + count);

        } else {
            // 상품 게시판에 따른 게시글 전체 갯수 가져오기
            count = mdao.everyCount("SELECT COUNT(*) FROM PRODUCTBOARD WHERE CATEGORY = '" + category + "'");

            System.out.println("productCategory(conut) : " + count);
        }

        // 페이징 처리
        PageDTO paging = fish.paging(page, 12, count);

        
        // 카테고리 전체 / 세부화
        if(category.equals("전체")){
            if (order.equals("PROENDDATE")) { // 시간 끝나는 순서대로는 ASC기 때문에 다시 걸었음
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY "+order+" ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY "+order+" ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
        }   else if (order.equals("PROPRICE1")){
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
            }else if (order.equals("PROPRICE2")){
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
            }
            else {
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY "+order+" DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY "+order+" DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
        }
        } else {
            if (order.equals("PROENDDATE")) { // 시간 끝나는 순서대로는 ASC기 때문에 다시 걸었음
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY " + order + " ASC) AS RN FROM PRODUCTBOARD WHERE CATEGORY = '" + category + "') WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY " + order + " ASC) AS RN FROM PRODUCTBOARD WHERE CATEGORY = '" + category + "') WHERE RN BETWEEN #{startRow} AND #{endRow}");

            } else if (order.equals("PROPRICE1")){
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE DESC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
            }else if (order.equals("PROPRICE2")){
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY PROPRICE ASC) AS RN FROM PRODUCTBOARD) WHERE RN BETWEEN #{startRow} AND #{endRow}");
            }else {
                paging.setSql("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY " + order + " DESC) AS RN FROM PRODUCTBOARD WHERE CATEGORY = '" + category + "') WHERE RN BETWEEN #{startRow} AND #{endRow}");
                System.out.println("SELECT * FROM (SELECT PRODUCTBOARD.*, ROW_NUMBER() OVER(ORDER BY " + order + " DESC) AS RN FROM PRODUCTBOARD WHERE CATEGORY = '" + category + "') WHERE RN BETWEEN #{startRow} AND #{endRow}");
            }
        }

        // sql 셋팅
        System.out.println("(select)paging : " + paging);


        // --- 여기서부터 오류 ---
        // 게시물 가져오기
        List<ProductBoardDTO> ProSelect =  pdao.ProSelect(paging);

        System.out.println("ProSelect(productCategory) : " + ProSelect);

        // 현재 페이지와 페이징처리된 게시글 리스트보내기
        List<Object> result = new ArrayList<>();

        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // sql문 삭제
        paging.setSql(null);

        // 리스트에 페이징과 워크 리스트 첨부
        result.add(paging);
        result.add(ProSelect);
        return result;
    }

    // 판매 게시물 상세보기 페이지
    public ModelAndView PAY015(int boaCode) throws IOException {
        mav=new ModelAndView();

        MemBoardDTO boardDTOS=bbdao.DRA004(boaCode);
        System.out.println("mapper에서 가져온 게시물값 "+boardDTOS);

        String folderPath2 = "src/main/resources/static/PSW/text";
        Path path = Paths.get(System.getProperty("user.dir"),folderPath2);

        BufferedReader reader = new BufferedReader(

                new FileReader(path+"/"+boardDTOS.getBoaContent()+".txt")

        );
        System.out.println("파일 읽기 성공");
        String result="";
        // 첫줄은 엔터를 적용하지 않기 위해서 first 사용
        boolean first = true;

        String input;
        // 파일 읽기!
        while ((input = reader.readLine()) != null) {
            if (first) {
                result += input;
                first = false;
            } else {
                result += "\n" + input;
            }
        }

        // 리더닫기
        reader.close();
        System.out.println("result????????"+result);
        boardDTOS.setBoaContent(result);

        ProductBoardDTO proBoard = pdao.ProBoard(boaCode);

        System.out.println(proBoard);

        mav.addObject("bview",boardDTOS);
        mav.addObject("product",proBoard);

        mav.setViewName("Pay_View");

        return mav;
    }

    public ProductBoardDTO proTime(int proBoaCode) {

        ProductBoardDTO result = pdao.proTime(proBoaCode);

        return result;
    }

    public AuctionDTO auctionGet(int AucProCode) {
        System.out.println("Service도착 AucProCode : " + AucProCode);
        AuctionDTO result = pdao.auctionGet(AucProCode);
        System.out.println("auctionGet(result) : " + result);



        return result;
    }

    public AuctionDTO auctionInsert(AuctionDTO auction) {
        System.out.println("Service도착(auctionInsert) auction : " + auction);
        
        // 원래 auction 테이블에서 값 가져오기
        AuctionDTO auctionTbl = pdao.auctionGet(auction.getAucProCode());
        System.out.println("AuctionTbl : " + auctionTbl);

        // 첫 입찰하는 손님일 경우
        ProductBoardDTO pbdto = pdao.pbdtoGet(auction.getAucProCode());
        System.out.println("pbdto : " + pbdto);

        // 입찰한 가격이 원래 입찰가격보다 적을때
        if(auction.getAucPrice() < auctionTbl.getAucPrice()){

        } else {    // 입찰되는 코드

            if(auctionTbl.getAucPrice() != pbdto.getProPrice()) {  // 첫 입찰하는 손님이 아닐때 원래 입찰했던 사람의 포인트는 돌려줘야함
                // 기존 입찰했던 사람의 멤버 포인트 돌려줌
                int memGet = pdao.memGet(auctionTbl);
                System.out.println("memGet : " + memGet);

                // 기존 입찰했던 사람의 포인트 변동내역 적용
                int pointGet = pdao.pointGet(auctionTbl);
                System.out.println("pointGet : " + pointGet);

            }

            // 구매한 사람 경매 테이블 update
            int aucUpdate = pdao.auctionInsert(auction);
            System.out.println("aucUpdate : " + aucUpdate);
            // 구매한 사람 포인트 멤버포인트에서 사라지게함.
            int memBuy = pdao.memBuy(auction);
            System.out.println("memBuy : " + memBuy);
            // 구매한 사람 포인트 입찰 변동내역 적용
            int pointBuy = pdao.pointBuy(auction);
            System.out.println("pointBuy : " + pointBuy);
        }

        session.invalidate();
        // 회원 코드로 세션에 들어갈 값들 가져오기(회원과 권한이 조인된 뷰를 조회)
        LoginDTO login = mdao.login(auction.getAucMemCode());
        session.setAttribute("login",login);


        AuctionDTO result = pdao.auctionResult(auction);
        System.out.println("auctionResult(result) : " + result);

        return result;
    }

    public String checkProduct(int boaCode) {
        String result = "없다";
        System.out.println("boaCode(service) : " + boaCode);
        int a = pdao.checkProduct(boaCode);

        System.out.println("a : " + a);
        
        if(a >= 1){
            result ="있다";
        }


        return result;
    }

    public AucMemberDTO auctionFinish(AuctionDTO auction) {
        System.out.println("Service도착 auction : " + auction);

        int proCode = auction.getAucProCode();
        System.out.println("proCode : " + proCode);

        // 최종 입찰자 정보 가져오기
        AucMemberDTO result = pdao.auctionFinish(auction);
        System.out.println("auctionGet(result) : " + result);


        try {

/*            // 최종 입찰자 정보 가져오기
            AucMemberDTO result = pdao.auctionFinish(auction);
            System.out.println("auctionGet(result) : " + result);*/



            System.out.println("proCode : " + proCode);

            // 판매자 정보 가져오기 (boamemcode)
            int proboa = pdao.getBoard(proCode);
            System.out.println("proboa : " + proboa);



            // 상품 삭제

            int auctionDel = pdao.auctionDel(auction);

            int aucDelete = pdao.auctionDelete(auction);
            System.out.println("auctionGet(aucDelete) : " + aucDelete);

            if(result.getAucMemCode() != proboa){
                // 판매자 수익 추가하기
                String sql="INSERT INTO POINTHISTORY VALUES("+proboa+", '상품 판매 수익', "+result.getAucPrice()+", SYSDATE)";
                mdao.everyInsert(sql);
                sql="UPDATE MEMBER SET MEMPOINT = MEMPOINT + " + result.getAucPrice() + "WHERE MEMCODE = " + proboa;
                mdao.sqlUpdate(sql);
            }

            System.out.println("result : " + result);
            return result;


        } catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}

