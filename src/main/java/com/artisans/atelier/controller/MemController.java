package com.artisans.atelier.controller;

import com.artisans.atelier.dto.MemberDTO;
import com.artisans.atelier.dto.ReportDTO;
import com.artisans.atelier.service.MemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemController {

    private ModelAndView mav;
    private final MemService msvc;
    private final HttpSession session;

    /////////////////////////////////////////////////////////////
    // 회원영역
    // 회원가입 및 로그인 폼 이동
    @GetMapping("/MEM001")
    public String MEM001(){
        return "MemJoin&Login";
    }

    // 회원 가입
    @PostMapping("/MEM004")
    public ModelAndView MEM004(@ModelAttribute MemberDTO member){
        //System.out.println("컨트롤러 : " + member);
        mav = msvc.MEM004(member);
        return mav;
    }

    // 이메일 인증
    @GetMapping("/MEM005")
    public ModelAndView MEM005(@RequestParam("user") int memCode){
        // System.out.println("컨트롤러 : " + memCode);
        mav = msvc.MEM005(memCode);
        return mav;
    }

    // 로그인
    @PostMapping("/MEM006")
    public ModelAndView MEM006(@ModelAttribute MemberDTO member){
        //System.out.println("컨트롤러 : " + member);
        mav = msvc.MEM006(member);
        return mav;
    }

    // 로그아웃
    @GetMapping("/MEM007")
    public String MEM007(){
        session.invalidate();
        return "index";
    }

    // 신고하기 폼 이동
    @GetMapping("/MEM008")
    public String MEM008(){
        return "Mem_ReportForm";
    }

    // 신고 처리
    @PostMapping("/MEM009")
    public ModelAndView MEM009(@ModelAttribute ReportDTO report){
        //System.out.println("컨트롤러 : " + report);
        mav = msvc.MEM009(report);
        return mav;
    }



    // 신고리스트 이동
    @GetMapping("/MEM010")
    public String MEM010(){
        return "Mem_ReportList";
    }

    // 회원목록 이동
    @GetMapping("/MEM011")
    public String MEM011(){
        return "Mem_List";
    }

    // 공지메일 이동
    @GetMapping("/MEM015")
    public String MEM015(){return "Mem_Notice";}

    // 직원목록 이동
    @GetMapping("/MEM021")
    public String MEM021(){return "Mem_StaffList";}


    //////////////////////////////////////////////////////////////
    // 프로필 영역

    // 회원 프로필 이동
    @GetMapping("/PRO001")
    public ModelAndView PRO001(@RequestParam("user")int memCode){
        //System.out.println(memCode);
        mav = msvc.PRO001(memCode);
        return mav;
    }

    // 프로필사진, 배경사진, 소개글 수정
    @PostMapping("/proUpdate")
    public ModelAndView proImgUpdate(@ModelAttribute MemberDTO member,
                                     @RequestParam("checkType")String check){
        //System.out.println(member);
        //System.out.println(check);
        mav=msvc.proUpdate(member,check);
        return mav;
    }

    // 팔로우 또는 언팔로우
    @PostMapping("/follow")
    public ModelAndView follow(@RequestParam("youCode")int youCode,
                               @RequestParam("myCode")int myCode,
                               @RequestParam("FOU")boolean FOU){
        //System.out.println("컨트롤러 해당코드memCode :"+youCode+"내 memCode:"+myCode+" 팔로우,언팔로우:"+FOU);
        mav = msvc.follow(youCode,myCode,FOU);
        return mav;
    }

    // 회원 프로필에서 상세 수정이동
    @GetMapping("/PRO009")
    public ModelAndView PRO009(@RequestParam("user")int memCode){
        mav = msvc.PRO009(memCode);
        return mav;
    }

    // 프로필 수정
    @PostMapping("/MEM019")
    public ModelAndView MEM019(@ModelAttribute MemberDTO member,
                               @RequestParam("yy")String year,
                               @RequestParam("mm")String mon,
                               @RequestParam("dd")String day){
        System.out.println(member+" 생년월일:"+year+""+mon+""+day);
        mav = msvc.MEM019(member,year,mon,day);
        return mav;
    }

    //ai채팅 이동
    @GetMapping("/aiChating")
    public String aiChating(){
        return "aiChat";
    }

    /////////////////////////////////////////////////////////////////////
    // 휴면 계정 전환
    @GetMapping("/MEM014")
    public ModelAndView MEM014(@RequestParam("user")int memCode){
        return mav= msvc.MEM014(memCode);
    }

}
