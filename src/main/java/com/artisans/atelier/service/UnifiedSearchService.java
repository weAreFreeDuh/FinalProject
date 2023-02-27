package com.artisans.atelier.service;

import com.artisans.atelier.dao.MemDAO;
import com.artisans.atelier.dao.UnifiedSearchDAO;
import com.artisans.atelier.dto.MemBoardDTO;
import com.artisans.atelier.dto.MemWorkSpaceDTO;
import com.artisans.atelier.dto.MemberDTO;
import com.artisans.atelier.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnifiedSearchService {
    private ModelAndView mav;

    private final UnifiedSearchDAO sdao;
    private final MemDAO mdao;

    private final HttpSession session;

    FISH fish = new FISH();


    // 통합검색
    public ModelAndView UnifiedSearch(String search) {
        mav = new ModelAndView();
        if(search.indexOf("@")==0){// 프로필 검색
            // @제거
            search = search.substring(1);
            // 페이징 준비
            String sql = "SELECT COUNT(*) FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%' ORDER BY MEMNAME ASC";
            int count = mdao.everyCount(sql);
            PageDTO paging = fish.paging(1,10,count);
            paging.setSql("SELECT * FROM (SELECT MEMCODE,MEMNAME,MEMPROFILENAME,ROW_NUMBER() OVER(ORDER BY MEMNAME ASC) AS RN FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%') WHERE RN BETWEEN #{startRow} AND #{endRow}");

            // 검색
            List<MemberDTO> memberList = mdao.MEM011(paging);

            mav.addObject("paging",paging);
            mav.addObject("memberList",memberList);
            mav.addObject("search",search);

            mav.setViewName("Pro_Search");

        }else{  // 워크스페이스, 게시글 검색
            String boardFirstSql = "SELECT * FROM (SELECT * FROM finalBoardList ";
            String boardLastSql = " ORDER BY LIKENUM DESC, BOAHIT DESC, COMENTNUM DESC ) WHERE ROWNUM <=8 ";
            String WorkFirstSql = "SELECT * FROM (SELECT * FROM finalWorkSpaceList ";
            String WorkLastSql = " ORDER BY LIKENUM DESC, HITNUM DESC, COMENTNUM DESC ) WHERE ROWNUM <=5 ";

            // 검색 sql
            // 게시글
            String sql = boardFirstSql;
            String sql2= boardFirstSql;
            // 워크 스페이스
            String sql3 = WorkFirstSql;
            String sql4= WorkFirstSql;

            // 첫실행인가?
            boolean first = true;
            boolean second = true;

            //(ORDER BY case when (worktag like '%#123,%' or worktag like '%#123')

            // 태그 검색인가 아닌가 확인후 나눔
            if(search.indexOf("#")==0){
                // 태그 기본 검색 AND연산과 패턴 값
                // 태그 확장 검색 OR연산과 패턴 값

                String[] words = search.split("#|,#|\\s#");
                for (String wo : words) {
                    if(first){
                        first = false;
                    }else if(second){
                        sql +=" WHERE BOATAG LIKE '%#"+wo+",%' OR BOATAG LIKE '%,#"+wo+"'";
                        sql2 += " WHERE BOATAG LIKE '%#"+wo+"%'";
                        sql3 +=" WHERE WORKTAG LIKE '%#"+wo+",%' OR WORKTAG LIKE '%,#"+wo+"'  ";
                        sql4 +=" WHERE WORKTAG LIKE '%#"+wo+"%'";
                        second = false;
                    }else{
                        sql +=" OR BOATAG LIKE '%#"+wo+",%' OR BOATAG LIKE '%,#"+wo+"' ";
                        sql2 += " OR BOATAG LIKE '%#"+wo+"%'";
                        sql3 +=" OR WORKTAG LIKE '%#"+wo+",%' OR WORKTAG LIKE '%,#"+wo+"' ";
                        sql4 += " OR WORKTAG LIKE '%#"+wo+"%'";
                    }
                }

            }else{
                // 기본 검색 AND연산과 패턴 값
                // 확장 검색 OR연산과 패턴 값

                String[] words = search.split("\\s");
                for (String wo : words){
                    //System.out.println(wo);
                    if(first){
                        sql +="WHERE boatitle LIKE '%"+wo+"%' ";
                        sql2 =sql;
                        sql3 +="WHERE worktitle LIKE '%"+wo+"%' ";
                        sql4 =sql3;
                        first = false;
                    }else{
                        sql +=" AND boatitle LIKE '%"+wo+"%' ";
                        sql2 +=" OR boatitle LIKE '%"+wo+"%' ";
                        sql3 +=" AND worktitle LIKE '%"+wo+"%' ";
                        sql4 +=" OR worktitle LIKE '%"+wo+"%' ";
                    }
                }
            }

            // sql 완성
            sql += boardLastSql;
            sql2 += boardLastSql;
            sql3 += WorkLastSql;
            sql4 += WorkLastSql;


            // 게시글 검색
            List<MemBoardDTO> BoardSearch = sdao.BoardSearch(sql);
            //System.out.println(BoardSearch);
            if(BoardSearch.size()<6){
                // 결과가 6개 미만이면 검색범위 확장
                BoardSearch.addAll(sdao.BoardSearch(sql2));
                // 중복 제거
                BoardSearch = BoardSearch.stream().distinct().collect(Collectors.toList());

            }



            // 워크 스페이스 검색
            List<MemWorkSpaceDTO> WorkSearch = sdao.WorkSearch(sql3);

            if(WorkSearch.size()<3){
                // 결과가 3개 미만이면 검색범위 확장
                WorkSearch.addAll(sdao.WorkSearch(sql4));
                // 중복 제거
                WorkSearch = WorkSearch.stream().distinct().collect(Collectors.toList());
            }



            // 더보기 버튼 a태그에 검색어 오브젝트 추가
            if(WorkSearch.size()>3){
                mav.addObject("workMore",search);
            }else{
                mav.addObject("workMore",null);
            }
            if(BoardSearch.size()>6){
                mav.addObject("boardMore",search);
            }else{
                mav.addObject("boardMore",null);
            }


            // 게시글 6개만 결과가 남도록 삭제
            if(BoardSearch.size()>6){
                BoardSearch=BoardSearch.subList(0,6);
            }else if(BoardSearch.size()==6){
                BoardSearch.remove(5);
            }


            // 워크스페이스 3개만 결과가 남도록 삭제
            if(WorkSearch.size()>3){
                WorkSearch=WorkSearch.subList(0,3);
            }else if(WorkSearch.size()==3){
                WorkSearch.remove(2);
            }



            // 검색결과 오브젝트 추가
            mav.addObject("BoardSearch",BoardSearch);
            mav.addObject("WorkSearch",WorkSearch);

            // sql 확인
            //System.out.println("sql1 : "+sql);
            //System.out.println("sql2 : "+sql2);
            System.out.println("sql3 : "+sql3);
            System.out.println("sql4 : "+sql4);

            // 결과 확인
            //System.out.println(BoardSearch);
            //System.out.println(WorkSearch);

            mav.setViewName("UnifiedSearch_List");
        }
        
        return mav;
    }

    // 통합검색 더 보기 목록
    public ModelAndView UnifiedSearchMore(String search, String type, int page, int limit) {
        mav= new ModelAndView();
        String sql = "";
        String where = "";
        String where2 = "";
        boolean first = true;
        boolean second = true;
        PageDTO pages = new PageDTO();

        if(search.indexOf("#")==0){//태그검색

            String[] words = search.split("#|,#|\\s#");

            if(type.equals("work")){    // 워크 스페이스 조회
                sql ="SELECT finalWorkSpaceList.*, ROW_NUMBER() OVER (ORDER BY case when ( ";

//                -- 태그검색2
//                SELECT finalWorkSpaceList.*, ROW_NUMBER() OVER
//                        -- 기본검색, 확장검색 순으로 늘어남
//                (ORDER BY case when (worktag like '%#123,%' or worktag like '%#123') then '1' else '0' end DESC,
//                    LIKENUM DESC, HITNUM DESC, COMENTNUM DESC) AS RN FROM finalWorkSpaceList
//                    where worktag like '%123%';

                for (String wo : words) {
                    if(first){
                        first = false;
                    }else if (second) {
                        where +=" (WORKTAG like '%#"+wo+",%' or WORKTAG like '%#"+wo+"') ";
                        where2 +="  WORKTAG LIKE '%#"+wo+"%' ";
                        second = false;
                    }else{
                        where +=" AND (WORKTAG LIKE '%#"+wo+",%' OR WORKTAG LIKE '%#"+wo+"') ";
                        where2 += " OR WORKTAG LIKE '%#"+wo+"%' ";
                    }


//                    System.out.println("단어 시작");
//                    System.out.println(wo);

                }

                sql += where+") then '1' else '0' end DESC, LIKENUM DESC, HITNUM DESC, COMENTNUM DESC) AS RN FROM finalWorkSpaceList WHERE "+where2;
                int count = mdao.everyCount("SELECT Count(*) FROM (" + sql+")");
                //System.out.println(count);
                PageDTO paging = fish.paging(page, limit, count);
                sql ="SELECT * FROM ( "+sql+" ) WHERE RN BETWEEN #{startRow} AND #{endRow}";
                paging.setSql(sql);
                List<MemWorkSpaceDTO> SearchList = sdao.WorkMoreSearch(paging);
                //System.out.println(SearchList);

                paging.setSearch(search);
                mav.addObject("paging",paging);
                mav.addObject("SearchList",SearchList);

            }else if(type.equals("board")){ // 보드 게시글 조회
                sql ="SELECT finalBoardList.*, ROW_NUMBER() OVER (ORDER BY case when ( ";

                for (String wo : words) {
                    if(first){
                        first = false;
                    }else if (second) {
                        where +=" BOATAG like '%#"+wo+",%' or BOATAG like '%#"+wo+"' ";
                        where2 +=" BOATAG LIKE '%#"+wo+"%' ";
                        second = false;
                    }else{
                        where +=" AND BOATAG LIKE '%#"+wo+",%' OR BOATAG LIKE '%#"+wo+"' ";
                        where2 += " OR BOATAG LIKE '%#"+wo+"%' ";
                    }

                }

                sql += where+") then '1' else '0' end DESC, LIKENUM DESC, boahit DESC, COMENTNUM DESC) AS RN FROM finalBoardList WHERE "+where2;
                int count = mdao.everyCount("SELECT Count(*) FROM (" + sql+")");
                //System.out.println(count);
                PageDTO paging = fish.paging(page, limit, count);
                sql ="SELECT * FROM ( "+sql+" ) WHERE RN BETWEEN #{startRow} AND #{endRow}";
                paging.setSql(sql);
                List<MemBoardDTO> SearchList = sdao.BoardMoreSearch(paging);
                //System.out.println(SearchList);
                paging.setSearch(search);
                mav.addObject("paging",paging);
                mav.addObject("SearchList",SearchList);
            }

        }else{// 일반검색
            // 일반검색 실행
            System.out.println("일반검색 실행");
            String[] words = search.split("\\s");

            if(type.equals("work")){    // 워크 스페이스 조회

//                SELECT finalWorkSpaceList.*, ROW_NUMBER() OVER (ORDER BY case when
//                        ( worktitle like '%123%' )
//                        then '1' else '0' end DESC, LIKENUM DESC, HITNUM DESC, COMENTNUM DESC) AS RN FROM finalWorkSpaceList
//                    WHERE worktitle LIKE '%123%';

                sql ="SELECT finalWorkSpaceList.*, ROW_NUMBER() OVER (ORDER BY case when ( ";

                for (String wo : words) {
                    if (first) {
                        where +=" WORKTITLE LIKE '%"+wo+"%' ";
                        where2 +=" WORKTITLE LIKE '%"+wo+"%' ";
                        first = false;
                    }else{
                        where +=" AND WORKTITLE LIKE LIKE '%"+wo+"%' ";
                        where2 += " OR WORKTITLE LIKE '%"+wo+"%' ";
                    }

                }
                sql += where+" ) then '1' else '0' end DESC, LIKENUM DESC, HITNUM DESC, COMENTNUM DESC) AS RN FROM finalWorkSpaceList WHERE "+where2;
                int count = mdao.everyCount("SELECT Count(*) FROM (" + sql+")");
                //System.out.println(count);
                PageDTO paging = fish.paging(page, limit, count);
                sql ="SELECT * FROM ( "+sql+" ) WHERE RN BETWEEN #{startRow} AND #{endRow}";
                paging.setSql(sql);
                List<MemWorkSpaceDTO> SearchList = sdao.WorkMoreSearch(paging);
                //System.out.println(SearchList);
                paging.setSearch(search);
                mav.addObject("paging",paging);
                mav.addObject("SearchList",SearchList);

            }else if(type.equals("board")){ // 보드 게시글 조회
                sql ="SELECT finalBoardList.*, ROW_NUMBER() OVER (ORDER BY case when ( ";
                //System.out.println("진짜 제발 보드 인식 한번만 해줘라 형 힘들다 어>?");
                for (String wo : words) {
                    if(first) {
                        where +=" BOATITLE LIKE '%"+wo+"%' ";
                        where2 +=" BOATITLE LIKE '%"+wo+"%' ";
                        first = false;
                    }else{
                        where +=" AND BOATITLE LIKE LIKE '%"+wo+"%' ";
                        where2 += " OR BOATITLE LIKE '%"+wo+"%' ";
                    }

                }
                sql += where+" ) then '1' else '0' end DESC, LIKENUM DESC, boahit DESC, COMENTNUM DESC) AS RN FROM finalBoardList WHERE "+where2;
                int count = mdao.everyCount("SELECT Count(*) FROM (" + sql+")");
                //System.out.println(count);
                PageDTO paging = fish.paging(page, limit, count);
                sql ="SELECT * FROM ( "+sql+" ) WHERE RN BETWEEN #{startRow} AND #{endRow}";
                paging.setSql(sql);
                List<MemBoardDTO> SearchList = sdao.BoardMoreSearch(paging);
                System.out.println(SearchList);
                System.out.println(type);

                paging.setSearch(search);
                mav.addObject("paging",paging);
                mav.addObject("SearchList",SearchList);
            }

        }


        mav.addObject("researchType",type);
        //UnifiedSearch_ListMore
        mav.setViewName("UnifiedSearch_ListMore");
        return mav;
    }

    // 프로필 검색
    public ModelAndView ProfileSearch(int page, String search) {
        mav = new ModelAndView();
        // 페이징 준비
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%' ORDER BY MEMNAME ASC";
        int count = mdao.everyCount(sql);
        PageDTO paging = fish.paging(page,10,count);
        paging.setSql("SELECT * FROM (SELECT MEMCODE,MEMNAME,MEMPROFILENAME,ROW_NUMBER() OVER(ORDER BY MEMNAME ASC) AS RN FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%') WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 검색
        List<MemberDTO> memberList = mdao.MEM011(paging);

        mav.addObject("paging",paging);
        mav.addObject("memberList",memberList);
        mav.addObject("search",search);

        mav.setViewName("Pro_Search");
        return mav;
    }

    // 프로필 타이핑중 검색
    public List<MemberDTO> profileResearching(String search) {
        search = search.substring(1);

        String sql = "SELECT COUNT(*) FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%' ORDER BY MEMNAME ASC";
        int count = mdao.everyCount(sql);
        PageDTO paging = fish.paging(1,10,count);
        paging.setSql("SELECT * FROM (SELECT MEMCODE,MEMNAME,MEMPROFILENAME,ROW_NUMBER() OVER(ORDER BY MEMNAME ASC) AS RN FROM MEMBER WHERE MEMNAME LIKE '%"+search+"%') WHERE RN BETWEEN 1 AND 10");

        // 검색
        return mdao.MEM011(paging);
    }

    // 팔로잉한 게시물 가져오기
    public List<Object> ProfileFollowBoard(int memCode, int page) {
        List<Object> result = new ArrayList<>();
        int count = mdao.everyCount("SELECT count(*) FROM BOARD BO INNER JOIN FOLLOW FO ON BO.BOAMEMCODE = fo.follower WHERE FO.FOLLOWING = "+memCode+" AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory WHERE BHMEMCODE = FO.FOLLOWING)");
        PageDTO paging = fish.paging(page,12,count);
        paging.setSql("SELECT * FROM (SELECT BO.*, ROW_NUMBER() OVER(ORDER BY bo.boadate DESC) AS RN FROM finalboardList BO INNER JOIN FOLLOW FO ON BO.BOAMEMCODE = fo.follower WHERE FO.FOLLOWING = "+memCode+" AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory WHERE BHMEMCODE = FO.FOLLOWING))WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 게시물 가져오기
        List<MemBoardDTO> boardList =  mdao.ProfileBoard(paging);
        System.out.println(boardList);
        // sql 숨기기
        paging.setSql(null);
        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // 보내는 객체에 추가
        result.add(paging);
        result.add(boardList);
        return result;
    }

    // 일반추천
    public List<Object> RegularRecommand(int memCode, int page) {

        int count = mdao.everyCount("SELECT count(*) FROM FINALBOARDLIST fi WHERE BOAMEMCODE != "+memCode+" AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory  WHERE BHMEMCODE = "+memCode+") AND (BOAMEMCODE IN (SELECT f2.BOAMEMCODE FROM board f2 WHERE f2.BOACODE IN (SELECT BHBOACODE FROM BOARDHISTORY WHERE BOARDHISTORY.BHMEMCODE = "+memCode+")) OR BOAWORKCODE IN (SELECT f3.BOAWORKCODE FROM board f3 WHERE f3.BOACODE IN (SELECT BHBOACODE FROM BOARDHISTORY WHERE BOARDHISTORY.BHMEMCODE = "+memCode+") AND f3.BOAWORKCODE !=0)) ORDER BY boadate DESC");
        PageDTO paging = fish.paging(page,10,count);

        paging.setSql("SELECT * FROM( SELECT fi.*, ROW_NUMBER() OVER(ORDER BY fi.boadate DESC) AS RN FROM FINALBOARDLIST fi WHERE BOAMEMCODE != "+memCode+" AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory  WHERE BHMEMCODE = "+memCode+") AND (BOAMEMCODE IN (SELECT f2.BOAMEMCODE FROM board f2 WHERE f2.BOACODE IN (SELECT BHBOACODE FROM BOARDHISTORY WHERE BOARDHISTORY.BHMEMCODE = "+memCode+")) OR BOAWORKCODE IN (SELECT f3.BOAWORKCODE FROM board f3 WHERE f3.BOACODE IN (SELECT BHBOACODE FROM BOARDHISTORY WHERE BOARDHISTORY.BHMEMCODE = "+memCode+") AND f3.BOAWORKCODE !=0))) WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 게시물 가져오기
        List<MemBoardDTO> boardList =  mdao.ProfileBoard(paging);
        // sql 숨기기
        paging.setSql(null);
        // 다음페이지로 셋팅
        paging.setPage(page+1);

        List<Object> result = new ArrayList<>();
        // 보내는 객체에 추가
        result.add(paging);
        result.add(boardList);
        return result;
    }

    // 태그 중심 추천
    public List<MemBoardDTO> TagRecommand(int memCode, int page) {
        List<String> tags = sdao.TagRecommand(memCode);
        //System.out.println(tags);
        //System.out.println(tags.size());

        List<String> tags2 = new ArrayList<>();
        for (int i=0; i<tags.size(); i++){
            tags2.addAll(List.of(tags.get(i).split(",")));
        }

        System.out.println(tags2);

        // 태그 크기
        int tagSize = tags2.size();
        // 반복문 메소드
        boolean run = true;

        int tagNum1 = 0;
        int tagNum2 = 0;
        int tagNum3 = 0;

        // 태그가 한개 이상일떄,
        if (tagSize >= 1) {
            tagNum1 = (int) (Math.random() * tagSize);
        }

        //태그가 2개 이상일때,
        if (tagSize >= 2) {
            while (run) {
                //System.out.println("태그 2개 이상일때 while문");
                tagNum2 = (int) (Math.random() * tagSize);
                if (tagNum1 != tagNum2) {
                    run = false;
                }
            }
        }
        // 태그가 3개 이상일떄
        if (tagSize >= 3) {
            run = true;
            while (run) {
                //System.out.println("태그 3개 이상일때 while문");
                tagNum3 = (int) (Math.random() * tagSize);
                if (tagNum3 != tagNum1 && tagNum3 != tagNum2) {
                    run = false;
                }
            }
        }
        //System.out.println("태그1넘버:"+tagNum1+"태그2넘버:"+tagNum2+"태그3넘버:"+tagNum3);

        // 랜덤으로 뽑은 태그들 추출
        String tag1 = "";
        String tag2 = "";
        String tag3 = "";

        if (tagSize == 1) {
            tag1 = tags2.get(tagNum1);

        } else if (tagSize == 2) {
            tag1 = tags2.get(tagNum1);
            tag2 = tags2.get(tagNum2);
        } else if(tagSize >=3) {
            tag1 = tags2.get(tagNum1);
            tag2 = tags2.get(tagNum2);
            tag3 = tags2.get(tagNum3);
        }
        //System.out.println("태그1"+tag1+"태그2"+tag2+"태그3"+tag3);


        // sql like문 작성
        String likesql = "";
        if (tagSize == 1) {
            likesql = " AND ((BOATAG LIKE '%" + tag1 + "%'))";
        } else if (tagSize == 2) {
            likesql = " AND ((BOATAG LIKE '%" + tag1 + "%')";
            likesql +=" OR (BOATAG LIKE '%" + tag2 + "%' ))";
        } else if (tagSize >= 3) {
            likesql = " AND ( (BOATAG LIKE '%" + tag1 + "%')";
            likesql +=" OR (BOATAG LIKE '%" + tag2 + "%')";
            likesql += " OR (BOATAG LIKE '%" + tag3 + "%'))";
            //System.out.println("태그 3개 이상");
        }

        int count = mdao.everyCount("SELECT count(*) FROM FINALBOARDLIST fi WHERE BOAMEMCODE != " + memCode + " AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory  WHERE BHMEMCODE = " + memCode + ")  "+likesql+"  ORDER BY boadate DESC");
        //System.out.println("조건에 맞는 게시글 갯수:"+count);
        PageDTO paging = fish.paging(1, 10, count);
        paging.setSql("SELECT * FROM(SELECT fi.*, ROW_NUMBER() OVER(ORDER BY fi.boadate DESC) AS RN FROM FINALBOARDLIST fi WHERE BOAMEMCODE != " + memCode + " AND BOACODE NOT IN (SELECT BHBOACODE FROM boardhistory  WHERE BHMEMCODE = " + memCode + ") "+likesql+") WHERE RN BETWEEN #{startRow} AND #{endRow}");
        List<MemBoardDTO> boardList = mdao.ProfileBoard(paging);

        //System.out.println("태그기반 추천 끝");

        return boardList;
    }


}
