package com.artisans.atelier.controller;


import com.artisans.atelier.dto.StaDTO;
import com.artisans.atelier.service.StatisticsService;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private ModelAndView mav;
    private final StatisticsService ssvc;
    private final HttpSession session;

    // 전체 회원 통계
    @PostMapping("/STA001")
    public List<StaDTO> STA001(){
        return ssvc.STA001();
    }

    // 회원 증가 추이
    @PostMapping("/STA002")
    public List<StaDTO> STA002(@RequestParam("year")int year){
        return ssvc.STA002(year);
    }

    // 매출 통계
    @PostMapping("/STA003")
    public List<StaDTO> STA003(@RequestParam("year")int year){
        return ssvc.STA003(year);
    }

    // 통계 기본 정보
    @PostMapping("/STA004")
    public StaDTO STA004(){
        return ssvc.STA004();
    }

    // 게시판별 게시글 갯수
    @PostMapping("/STA005")
    public List<StaDTO> STA005(@RequestParam("year")int year){return ssvc.STA005(year);}

    // 게시판별 좋아요 갯수
    @PostMapping("/STA006")
    public List<StaDTO> STA006(@RequestParam("year")int year){return  ssvc.STA006(year);}

    // 게시판별 조회수
    @PostMapping("/STA007")
    public List<StaDTO> STA007(@RequestParam("year")int year){return  ssvc.STA007(year);}
    // 트렌드 전반 조회수
    @PostMapping("/STA008")
    public List<Object> STA008(@RequestParam("year")int year){return  ssvc.STA008(year);}
    // 남녀 비율
    @PostMapping("STA009")
    public List<StaDTO> STA009(@RequestParam("year")int year){return ssvc.STA009(year);}

}
