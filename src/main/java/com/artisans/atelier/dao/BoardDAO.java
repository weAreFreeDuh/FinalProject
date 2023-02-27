package com.artisans.atelier.dao;


import com.artisans.atelier.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BoardDAO {

    // 서브 카테고리 가져오기
    List<BoCategoryDTO> ajaxcategory(String category);
    
    /*워크스페이스 카테고리 코드 가져오기*/
    int workCateCode(MemWorkSpaceDTO workspace);

    /*워크 스페이스 값 넣기*/
    int WOR2(MemWorkSpaceDTO workspace);

    /*워크 스페이스 갯수 찾기*/
    int wCount();

    /*페이지 구하기*/
    List<MemWorkSpaceDTO> wList(PageDTO paging);

    /*워크스페이스 값 받기*/
    MemWorkSpaceDTO WOR4(int workCode);

    /*워크스페이스 수정*/
    void WOR6(MemWorkSpaceDTO workspace);

    /*음악게시판 워크스페이스 출력*/
    List<MemWorkSpaceDTO> MUS003(String category);

    /*음악게시판 게시글 출력*/
    List<MemBoardDTO> MUS003B(String category);

    /*음악게시판 워크스페이스 출력 서브카테고리 */
    List<MemWorkSpaceDTO> MUS003Code(int cateCode);

    /*음악게시판 게시글 출력 서브카테고리 */
    List<MemBoardDTO> MUS003CodeB(int cateCode);

    /* 음악 더보기에서 보기 위한 페이징 갯수  */
    int MUS004Count(BoCategoryDTO category);

    /* 음악 더보기에서 워크스페이스 */
    List<MemWorkSpaceDTO> MUS004List(PageDTO paging);

    /*ajax 워크스페이스 작성시 게시글 검색 기능*/
    List<MemBoardDTO> ajaxsearchAll(String sql);

    /*워크스페이스 게시글 묶어주기*/
    void tieBoard(String sql);

    /*워크스페이스에 묶인 게시글 리스트 받기*/
    List<MemBoardDTO> tiedbBoard(int workCode);

    /*워크스페이스 카테고리 코드로 카테고리 찾기*/
    String workCodeSet(int catecode);

    /*음악을 들을 시 히스토리 업데이트*/
    int ajaxMusHistroyInput(BoardHistoryDTO boardHistory);

    /*워크스페이스 내 게시글 제거 */
    int WOR7(int boaCode);

    /*워크스페이스 카테고리 가져오기*/
    String getCategoryBoard(int workCode);

    /*워크스페이스 내 게시글 갯수 구하기*/
    int WOR4Count(int workCode);

    /*워크스페이스 내 게시글 페이징 처리*/
    List<MemBoardDTO> boardList(PageDTO paging);

    /*유로화 일경우 값넣기*/
    void litMethod(MemWorkSpaceDTO workspace);

    /*갯수 구하기*/
    int methodnum(int workCode);

    /*보드테이블에서 워크코드 가져오기*/
    MemBoardDTO getWorkCodeFormLit(int boaCode);

    /*문학테이블에서 워크코드로 문학정보 가져오기 */
    LiteratureDTO getAllAboutLit(int workCode);

    /*워크스페이스 내 어느 화 넘어서 무료일시 유료인지 아닌지 확인*/
    MemWorkSpaceDTO getLitNeedPay(MemWorkSpaceDTO workspace);

    /*문학 히스토리에서 결제내역이 있는지 확인*/
    Integer getPayCodeLit(LiteratureDTO literature);

    /*회원의 돈을 가져온다*/
    int getMemPointLit(int memCode);

    /*회원의 가진 돈을 수정*/
    int memPointafterPayLit(LiteratureDTO literature);

    /*문학을 샀었다는 히스토리 넣어주기*/
    void setPointHistoryLit(LiteratureDTO literature);

    /*워크스페이스 와 묶인 게시글 가져오기*/
    List<MemBoardDTO> WOR8_boaCode(int workCode);

    /*워크스페이스 내 보드 리스트 삭제*/
    int WOR8_Board(int boaCode);

    /*워크스페이스 삭제*/
    int WOR8_Workspace(int workCode);

    /*문학 게시글 삭제*/
    void WOR8_Workspace_lit(int workCode);

    /*판매한 회원의 포인트*/
    int getSellPointLit(int boaMemCode);

    /*문학 정보 가져오기*/
    LiteratureDTO WOR4Lit(int workCode);

    /*인덱스 정보 가져오기*/
    IndexDTO indexAddObject();

    void WOR8_BoardLit(int boaCode);

    int WOR8_BoardDelete(int boaCode);
}
