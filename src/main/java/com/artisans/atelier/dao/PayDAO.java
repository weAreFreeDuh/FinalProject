package com.artisans.atelier.dao;

import com.artisans.atelier.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PayDAO {

    int PAY007(SalesDTO sales);

    int pHistory(SalesDTO sales);

    int memPoint(SalesDTO sales);

    int memGrade(SalesDTO sales);

    int PAY008(SalesDTO sales);

    int PAY014(SalesDTO sales);

    int RePointHistory(SalesDTO sales);

    int RememPoint(SalesDTO sales);

    int PAY003(ProductDTO product);

    List<ProductBoardDTO> productAll(PageDTO paging);

    List<ProductBoardDTO> productCategory(PageDTO paging);

    List<ProductBoardDTO> ProSelect(PageDTO paging);

    ProductBoardDTO ProBoard(int boaCode);

    ProductBoardDTO proTime(int proBoaCode);

    int auctionStart(ProductDTO product);

    AuctionDTO auctionGet(int AucProCode);

    int auctionInsert(AuctionDTO auction);

    ProductBoardDTO pbdtoGet(int aucProCode);

    int memBuy(AuctionDTO auction);

    int pointBuy(AuctionDTO auction);

    int memGet(AuctionDTO auctionTbl);

    int pointGet(AuctionDTO auctionTbl);

    AuctionDTO auctionResult(AuctionDTO auction);

    int checkProduct(int boaCode);

    AucMemberDTO auctionFinish(AuctionDTO auction);

    int auctionDelete(AuctionDTO auction);

    int memSell(AuctionDTO auction);

    int pointSell(AuctionDTO auction);

    int getBoard(int proCode);

    int auctionDel(AuctionDTO auction);
}
