package com.artisans.atelier.controller;


import com.artisans.atelier.dto.MemWorkSpaceDTO;
import com.artisans.atelier.dto.WorkSpaceDTO;
import com.artisans.atelier.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class BoardController {

    private ModelAndView mav = new ModelAndView();

    private final BoardService bsvc;


    @PostMapping("/test")
    public ModelAndView testcontroller(@RequestParam("boaCodeList") List boaCodeList ){
        System.out.println("boaCodeList는 "+boaCodeList);
        mav = bsvc.testcontroller(boaCodeList);
        return mav;
    }

    /*워크 스페이스 생성폼 가기 */
    @GetMapping("/WOR1")
    public String WOR1(){
        return "WOR_Write";
    }


    /*워크 스페이스 생성 */
    @PostMapping("/WOR2")
    public ModelAndView WOR2(@ModelAttribute MemWorkSpaceDTO workspace) throws IOException {
        System.out.println("memWorkspace================="+workspace);
        mav = bsvc.WOR2(workspace);

        return mav;
    }

    // WOR3 : 페이징 처리 & 리스트
    @GetMapping("/WOR3")
    public ModelAndView WOR3(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             @RequestParam(value = "limit", required = false, defaultValue = "5") int limit
                             ) {
        // page를 필수로 가져와야 하나 -> false
        /*
         * String page1 = request.getParameter("page"); if(page1 == null) { int page =
         * 1; } else { int page = Integer.parseInt(page1); }
         */

        System.out.println("[1] jsp → controller \n page : " + page);

            mav = bsvc.WOR3(page, limit);


//			System.out.println("[5] service → controller \n mav : " + mav);

        return mav;
    }

    /*워크스페이스 뷰처리*/

    @GetMapping("/WOR4")
    public ModelAndView WOR4(@RequestParam("workCode") int workCode,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "limit", required = false, defaultValue = "5") int limit
    ){
        System.out.println(workCode);

        mav = bsvc.WOR4(workCode,page,limit);

        return mav;
    }

    /*워크스페이스 페이지 이동*/
    @GetMapping("/WOR5")
    public ModelAndView WOR5(@RequestParam("workCode") int workCode){
        System.out.println("WOR55555"+workCode);
        mav = bsvc.WOR5(workCode);
        return mav;
    }

    /*워크스페이스 수정*/
    @PostMapping("/WOR6")
    public ModelAndView WOR6(@ModelAttribute MemWorkSpaceDTO workspace){
        System.out.println(workspace);
        mav = bsvc.WOR6(workspace);
        return mav;
    }

    ///////////////////////  주말에 추가된 기능
    /*워크스페이스에서 추가된 게시글 빼기*/
    @GetMapping("/WOR7")
    public ModelAndView WOR7(@RequestParam("boaCode") int boaCode,
    @RequestParam("workCode") int workCode){
        System.out.println("boaCode = "+ boaCode + "workCode ="+workCode);

        mav = bsvc.WOR7(boaCode,workCode);

        return mav;
    }

    // 워크스페이스 게시글 삭제
    @GetMapping("/WOR8")
    public ModelAndView WOR8(@RequestParam("workCode")int workCode){
        System.out.println("workCode ==??"+workCode);
        mav = bsvc.WOR8(workCode);
        return mav;
    }
    
    
    
    /*워크스페이스 내부 게시글 취소*/


    /*음악게시판 리스트 처리*/
    @GetMapping("/MUS003")
    public ModelAndView MUS003(
    @RequestParam(value = "category", required = false ,defaultValue = "null") String  category,
    @RequestParam(value = "cateCode", required = false ,defaultValue = "0"

    ) int cateCode
        ){
        mav = new ModelAndView();
        /*음악게시판를 처음 이동할 시 사용*/
        if (category != null) {
            System.out.println("이건 되냐?");
            System.out.println(category);
            mav = bsvc.MUS003(category,cateCode);
        }

        /*음악게시판의 서브 카테고리를 선택시 카테고리 코드가 정해지기에 그것을 사용하여 쓴다*/
        System.out.println("cateCode = "+cateCode);
        if(cateCode != 0) {
            mav = bsvc.MUS003Code(cateCode,category);
        }

        
        return mav;
    }

    /*음악게시판 리스트 처리*/
    @GetMapping("/MUS004")
    public ModelAndView MUS004(
            @RequestParam(value = "category", required = false ,defaultValue = "null") String  category,
            @RequestParam(value = "cateCode", required = false ,defaultValue = "999") int cateCode,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit,
            @RequestParam(value = "orderby", required = false, defaultValue = "workCode") String orderby
            ){

        mav = new ModelAndView();
        System.out.println("page = "+page);
        System.out.println("category = "+category);
        System.out.println("paging.setOrderby(orderby);"+orderby);

        /*음악게시판의 서브 카테고리를 선택시 카테고리 코드가 정해지기에 그것을 사용하여 쓴다*/
        System.out.println("cateCode = "+cateCode);
        if(cateCode != 999) {
            mav = bsvc.MUS004Code(category,cateCode,page,limit,orderby);
        }else{
            /*음악게시판를 처음 이동할 시 사용*/
            System.out.println(category);
            mav = bsvc.MUS004(category,cateCode,page,limit,orderby);
        }


        return mav;
    }








}
