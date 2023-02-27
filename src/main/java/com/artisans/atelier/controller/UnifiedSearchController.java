package com.artisans.atelier.controller;


import com.artisans.atelier.service.UnifiedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class UnifiedSearchController {

    private ModelAndView mav;
    private final UnifiedSearchService ssvc;
    private final HttpSession session;

    // 통합검색
    @GetMapping("/UnifiedSearch")
    public ModelAndView UnifiedSearch(@RequestParam("q") String search){
        //System.out.println("컨트롤러 : " + search);
        mav = ssvc.UnifiedSearch(search);

        return mav;
    }

    // 통합검색 더보기
    @GetMapping("/UnifiedSearchMore")
    public ModelAndView UnifiedSearchMore(@RequestParam("q") String search,
                                          @RequestParam("type") String type,
                                          @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "limit", required = false, defaultValue = "6") int limit){
        System.out.println("컨트롤러 : " + search+" 타입 : "+type);
        mav = ssvc.UnifiedSearchMore(search,type,page,limit);

        return mav;
    }

    // 프로필 검색 페이징 처리
    @GetMapping("/ProfileSearch")
    public ModelAndView ProfileSearch(@RequestParam("page")int page,
                                      @RequestParam("search")String search){
        return ssvc.ProfileSearch(page,search);
    }

    //////////////////////////////////////////////////////////////////
    // 팔로우한 회원 게시물
    @GetMapping("/FollowingUpLoadBoard")
    public String FollowingUpLoadBoard(){
        return "Pro_FollowBoard";
    }

    // 게시글 추천
    @GetMapping("/RecommandBoard")
    public String RecommandBoard(){
        return "Boa_Recommand";
    }
    

}
