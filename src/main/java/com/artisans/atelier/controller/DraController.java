package com.artisans.atelier.controller;


import com.artisans.atelier.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequiredArgsConstructor
@Controller
public class DraController {

    private ModelAndView mav= new ModelAndView();

    private  final BoardService bsvc;


    // 게시물 작성페이지 이동
    @GetMapping("/DRA001")
    public String DRA001(){
        return "Dra_Write";
    }
    // 통합리스트 페이지 이동
    @GetMapping("/DRA003")
    public ModelAndView DRA003(){
        mav=bsvc.DRA003();
        return mav;
    }

    // 게시물 상세보기 페이지 이동
    @GetMapping("/DRA004")
    public ModelAndView DRA004(@RequestParam("boaCode") int boaCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        mav=bsvc.DRA004(boaCode);

        // HttpServletRequest로부터 클라이언트의 쿠키를 가져온다. 먼저 쿠키가 null인지 검사하고,
        // 그렇지 않다면 foreach를 통해 해당 쿠키들 중 postView라는 이름의 쿠키가 있는지 검사한다.
        //만약 존재한다면 oldCookie라는 이름으로 가져온다.
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }


        // oldCookie가 null이 아니라면, 즉 postView가 존재한다면 해당 쿠키의 value가 현재 접근한 게시글의 id를 포함하고 있는지 검사한다.
        //만약 가지고있다면 아무일도 일어나지 않고(조회수가 증가하지 않고), 가지고있지 않다면 postService의 viewCountUp을 통해 해당 게시글의 조회수를 올려주고,
        // 게시글 id를 괄호로 감싸 oldCookie에 추가하고 HttpServletResponse에게 전달한다.
        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + boaCode+ "]")) {
                bsvc.viewCountUp(boaCode);
                oldCookie.setValue(oldCookie.getValue() + "_[" + boaCode + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        }
        // oldCookie가 null이라면 마찬가지로 postService의 viewCountUp을 통해 게시글의 조회수를 올려주고,
        // 해당 게시글 id를 괄호로 감싼 새로운 쿠키 postView를 생성하여 HttpServletResponse에게 전달한다.
        else {
            bsvc.viewCountUp(boaCode);
            Cookie newCookie = new Cookie("postView","[" + boaCode + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
        System.out.println("날라온 게시물 코드!!!"+boaCode);

        return mav;
    }
    // 게시물 리스트 페이지 이동
    @GetMapping("/DRA007")
    public ModelAndView DRA007(@RequestParam("category") String category,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "limit", required = false, defaultValue = "5") int limit ,
                               @RequestParam(value = "cateCode", required = false, defaultValue = "0") int cateCode)  {
        System.out.println(" 게시물리스트 코드!!!"+category);
        mav=bsvc.DRA007(category,page,limit,cateCode);
        return mav;
    }

    // DRA008: 게시물 리스트 페이지 이동
    @GetMapping("/DRA008")
    public ModelAndView DRA008(@RequestParam("category") String category, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "limit", required = false, defaultValue = "5") int limit ,
                               @RequestParam(value = "cateCode", required = false, defaultValue = "0") int cateCode,
                               @RequestParam(value = "orderby", required = false, defaultValue = "boaCode") String orderby)  {
        System.out.println(" 게시물리스트 코드!!!"+category);
        mav=bsvc.DRA008(category,page,limit,cateCode,orderby);
        return mav;
    }
    // DRA008_1 :워크스페이스 리스트 페이지 이동
    @GetMapping("/DRA008_1")
    public ModelAndView DRA008_1(@RequestParam("category") String category, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "limit", required = false, defaultValue = "5") int limit ,
                               @RequestParam(value = "cateCode", required = false, defaultValue = "0") int cateCode,
                                 @RequestParam(value = "orderby", required = false, defaultValue = "workCode") String orderby){
        System.out.println(" 워크스페이스 코드!!!"+category+orderby);
        mav=bsvc.DRA008_1(category,page,limit,cateCode,orderby);
        return mav;
    }
    // DRA009 : 수정페이지 이동
    @GetMapping("/DRA009")
    public ModelAndView DRA009(@RequestParam("boaCode") int boaCode) throws IOException {
        mav= bsvc.DRA009(boaCode);
        return mav;
    }
    // DRA0010 : 게시물 삭제하기
    @GetMapping("/DRA0010")
    public ModelAndView DRA010(@RequestParam("boaCode") int boaCode) throws IOException {
        mav= bsvc.DRA010(boaCode);
        return mav;
    }








}
