package com.artisans.atelier.controller;

import com.artisans.atelier.dto.MemberDTO;
import com.artisans.atelier.dto.ProductBoardDTO;
import com.artisans.atelier.dto.ProductDTO;
import com.artisans.atelier.dto.SalesDTO;
import com.artisans.atelier.service.BoardService;
import com.artisans.atelier.service.MemService;
import com.artisans.atelier.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PayController {
    private ModelAndView mav;
    private final PayService psvc;
    private final BoardService bsvc;
    private final HttpSession session;



    // 카카오페이 페이지 이동
    @GetMapping ("/PAY004")
    public ModelAndView PAY004(@ModelAttribute SalesDTO sales){
        /*System.out.println("PayController(sales) : " + sales);*/
        mav = psvc.PAY004(sales);
        return mav;
    }

    // 포인트 결제 후
    @GetMapping ("/PAY007")
    public ModelAndView PAY007(@ModelAttribute SalesDTO sales){
        System.out.println("Pay007(sales) : " + sales);
        mav = psvc.PAY007(sales);
        return mav;
    }

    // 멤버쉽 결제 후
    @GetMapping ("/PAY008")
    public ModelAndView PAY008(@ModelAttribute SalesDTO sales){
        System.out.println("Pay008(sales) : " + sales);
        mav = psvc.PAY008(sales);
        return mav;
    }

    // 포인트 반환
    @GetMapping ("/PAY014")
    public ModelAndView PAY014(@ModelAttribute SalesDTO sales){
        System.out.println("Pay008(sales) : " + sales);
        mav = psvc.PAY014(sales);
        return mav;
    }

    // 경매 상품으로 변환
    @PostMapping ("/PAY003")
    public ModelAndView PAY003(@ModelAttribute ProductDTO product){
        System.out.println("PAY003(product) Controller : " + product);
        mav = psvc.PAY003(product);
        return mav;
    }
    // 상품 게시판 리스트
    @GetMapping ("/PAY002")
    public String PAY002(){

        return "Pay_PayList";
    }
    // 경매 상품으로 변환
// 게시물 상세보기 페이지 이동
    @GetMapping("/PAY015")
    public ModelAndView DRA004(@RequestParam("boaCode") int boaCode,
/*                               @RequestParam("proStartDate") Date proStartDate,
                               @RequestParam("proEndDate") Date proEndDate,
                               @RequestParam("proPrice") int proPrice,*/
                               HttpServletRequest request, HttpServletResponse response) throws IOException {

        mav=psvc.PAY015(boaCode);

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




}
