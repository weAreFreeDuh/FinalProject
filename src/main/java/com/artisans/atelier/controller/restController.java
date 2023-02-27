package com.artisans.atelier.controller;

import com.artisans.atelier.dao.BoardDAO;
import com.artisans.atelier.dto.*;
import com.artisans.atelier.service.BoardService;
import com.artisans.atelier.service.MemService;
import com.artisans.atelier.service.PayService;
import com.artisans.atelier.service.UnifiedSearchService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class restController {
    private final MemService msvc;

    private final BoardService bsvc;

    private final UnifiedSearchService ssvc;

    private final PayService psvc;

    private final BoardDAO bdao;

    // 아이디 중복 체크 ajax
    @PostMapping("MEM002")
    public String MEM002(@RequestParam("memId") String memId){
        //System.out.println("memId : "+memId);
        String msg = msvc.MEM002(memId);
        //System.out.println("msg : "+msg);
        return msg;
    }

    // 닉네임 중복 체크 ajax
    @PostMapping("MEM003")
    public String MEM003(@RequestParam("memName") String memName){
        //System.out.println("memName : "+memName);
        String msg = msvc.MEM003(memName);
        //System.out.println("msg : "+msg);
        return msg;
    }

    // 신고 목록 Ajax
    @PostMapping("/ReportList")
    public List<Object> ReportList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                   @RequestParam(value = "limit", required = false, defaultValue = "5") int limit,
                                   @RequestParam(value = "all", required = false, defaultValue = "true") boolean all) {
        //System.out.println("신고리스트 : " + page + " 가져올 갯수 : " + limit + "전체 가져오나요? : " + all);

        List<Object> result = msvc.MEM010(page, limit, all);

        return result;
    }

    // 신고 처리 상태 변경
    @PostMapping("/ReportState")
    public String ReportState(@RequestParam("repCode") int repCode) {
        //System.out.println(repCode);
        String result = msvc.ReportState(repCode);
        return result;
    }

    // 회원목록
    @PostMapping("/MemberList")
    public List<Object> MemberList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit,
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "devide", required = false, defaultValue = "all") String devide,
            @RequestParam(value = "category", required = false, defaultValue = "MEMID") String category) {
        // page:페이지
        // limit: 가져올 갯수
        // search: 검색
        // devide: 분류 -> 회원상태
        // category: id,name
        //System.out.println("컨트롤러 페이지:" + page + " 가져올갯수:" + limit + " 검색:" + search + " 분류:" + devide + "카테고리" + category);
        List<Object> result = msvc.MEM011(page, limit, search, devide,category);
        return result;
    }

    // 직원으로 상태 변경
    @PostMapping("/MemberState")
    public String MemberState(@RequestParam("memCode") int memCode) {
        String result = msvc.MemberState(memCode);
        return result;
    }

    // 회원 경고
    @PostMapping("/MemberWarning")
    public String MemberWarning(@RequestParam("memCode") int memCode) {
        String result = msvc.MemberWarning(memCode);
        return result;
    }

    // 공지사항 회원 검색
    @PostMapping("/MEM016")
    public List<MemberDTO> MEM016(@RequestParam("devide") String devide,
                                  @RequestParam("category") String category,
                                  @RequestParam("searchName") String searchName) {
        List<MemberDTO> result = msvc.MEM016(devide, category, searchName);
        return result;
    }

    // 곻지사항 메일 전송
    @PostMapping("/MEM017")
    public String MEM017(@RequestParam("sendRange") Boolean sendRange,
                         @RequestParam("sendRangeName") String sendRangeName,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam("idList") ArrayList idList) {
        System.out.println("restController.MEM017"+sendRange);
        System.out.println("restController.MEM017"+sendRangeName);
        System.out.println("restController.MEM017"+title);
        System.out.println("restController.MEM017"+content);
        System.out.println("restController.MEM017"+idList);
        String result = msvc.MEM017(sendRange,sendRangeName,title,content,idList);
        return result;
    }


    // 회원 비밀번호 재설정
    @PostMapping("/MEM020")
    public String MEM020(@RequestParam("userEmail") String memId) {
        String result = msvc.MEM020(memId);
        return result;
    }

    // 직원 권한들 가져오기
    @PostMapping("/MEM023")
    public List<AuthorityDTO> MEM023() {
        List<AuthorityDTO> result = msvc.MEM023();
        //System.out.println(result);
        return result;
    }

    // 직원 목록(검색)
    @PostMapping("/EmployeeSelect")
    public List<Object> EmployeeSelect(@RequestParam("autAll") Boolean autAll,
                                       @RequestParam("autCode") int autCode,
                                       @RequestParam("category") String category,
                                       @RequestParam("search") String search,
                                       @RequestParam("page") int page,
                                       @RequestParam("limit") int limit) {
        //System.out.println("컨트롤러 all:"+autAll+" autcode"+autCode+" category"+category+" search:"+search+" page:"+page+" limit"+limit);
        List<Object> result = msvc.EmployeeSelect(autAll,autCode,category,search,page,limit);
        //System.out.println(result);
        return result;
    }

    // 직원 상세보기
    @PostMapping("/MEM022")
    public MemberDTO MEM022(@RequestParam("memCode") int memCode) {
        MemberDTO result = msvc.MEM022(memCode);
        //System.out.println(result);
        return result;
    }

    // 직원 권한 수정
    @PostMapping("/MEM025")
    public String MEM025(@RequestParam("memCode") int memCode,
                         @RequestParam("autCode") int autCode) {
        String result = msvc.MEM025(memCode,autCode);
        //System.out.println(result);
        return result;
    }

    // 직원 해고
    @PostMapping("/MEM026")
    public String MEM025(@RequestParam("memCode") int memCode) {
        String result = msvc.MEM026(memCode);
        //System.out.println(result);
        return result;
    }

    // 권한 리스트
    @PostMapping("/autResearch")
    public List<Object> autResearch(@RequestParam("autAll") boolean autAll,
                                    @RequestParam("category") String category,
                                    @RequestParam("search") String search,
                                    @RequestParam("page") int page,
                                    @RequestParam("limit") int limit) {
        List<Object> result = msvc.autResearch(autAll,category,search,page,limit);
        //System.out.println(result);
        return result;
    }

    // 권한 수정
    @PostMapping("/autUpdate")
    public String autUpdate(@RequestParam("category") String category,
                            @RequestParam("data") int data,
                            @RequestParam("autCode") int autCode){

        String result = msvc.autUpdate(category,data,autCode);
        //System.out.println(result);
        return result;
    }

    // 권한 생성
    @PostMapping("/MEM024")
    public String MEM024(@RequestParam("name") String name,
                         @RequestParam("give") int give,
                         @RequestParam("secu") int secu,
                         @RequestParam("acco") int acco,
                         @RequestParam("chat") int chat,
                         @RequestParam("mail") int mail,
                         @RequestParam("cate") int cate){

        String result = msvc.MEM024(name,give,secu,acco,chat,mail,cate);
        //System.out.println(result);
        return result;
    }

    // 권한 삭제
    @PostMapping("/autDelete")
    public String autDelete(@RequestParam("autCode") int autCode){

        String result = msvc.autDelete(autCode);
        //System.out.println(result);
        return result;
    }



    ////////////////////////////////////////////////////////////////

    // 서브 카테고리 리스트로 가져오기
    @PostMapping("/category")
    public List<BoCategoryDTO> ajaxcategory(@RequestParam("cate") String category) {

        System.out.println(category);
        // 리스트에 서브 카테고리를 담아온다.
        List<BoCategoryDTO> subCategoryList = bsvc.ajaxcategory(category);

        return subCategoryList;
    }

    ////////////////////////////////////////////////////////////////

    // 썸머노트 이미지 URL로 보내기
    @PostMapping(value="/summer_image",produces = "application/json")
    public JsonObject summer_image(MultipartFile file) throws Exception {
        JsonObject jsonObject = new JsonObject();
        System.out.println("넘어옴???");


        String fileRoot = "C:\\Users\\user\\Desktop\\intellijworkspace\\atelier\\src\\main\\resources\\static/PSW/imsi/";	//저장될 외부 파일 경로
        String originalFileName = file.getOriginalFilename();	//오리지날 파일명
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));	//파일 확장자

        String savedFileName = UUID.randomUUID() + extension;	//저장될 파일 명

        File targetFile = new File(fileRoot + savedFileName);

        try {
            InputStream fileStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);	//파일 저장
            jsonObject.addProperty("url", "static/PSW/imsi/"+savedFileName);
            jsonObject.addProperty("responseCode", "success");
            System.out.println("json"+jsonObject);

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;


    }

    private String fileDBName(String fileName, String save_folder) {
        String result = save_folder + fileName;
        return result;
    }

    // ajax 서비스 연결

    // ajax 게시글 삽입하기
    @PostMapping("/DRA002")
    public String DRA002(@ModelAttribute MemBoardDTO board) throws Exception {
        System.out.println("controller board?? " + board);

        // 게시글이 삽입되었는지 확인하는 check
        String check = bsvc.DRA002(board);
        return check;

    }

    /* 워크스페이스에서 생성시 게시물 검색기능*/
    @PostMapping("/ajaxsearchAll")
    public List<MemBoardDTO> ajaxsearchAll(@RequestParam("devide") String devide,
                                           @RequestParam("category") String category,
                                           @RequestParam("searchName") String searchName,
                                           @RequestParam("memCode") String memCode) {
        System.out.println("===========");
        System.out.println(devide);
        System.out.println(category);
        System.out.println(searchName);
        System.out.println(memCode);

        List<MemBoardDTO> boardList = bsvc.ajaxsearchAll(devide, category, searchName, memCode);

        return boardList;
    }

    //ajaxMusHistroyInput : 음악게시판 음악을 들을 시 히스토리 테이블에 넣기
    @PostMapping("/ajaxMusHistroyInput")
    public String ajaxMusHistroyInput(@ModelAttribute BoardHistoryDTO boardHistory) {
        System.out.println("controller board?? " + boardHistory);

        // 게시글이 삽입되었는지 확인하는 check
        String check = bsvc.ajaxMusHistroyInput(boardHistory);
        return check;

    }

    //////////////////////////////////////////////////////////////// 02.03


    @PostMapping("workspacelist")
    public List<MemWorkSpaceDTO> workspacelist(@RequestParam("workMemCode") int workMemCode){

        System.out.println("workspace 회원코드 :"+workMemCode);
        List<MemWorkSpaceDTO> workspacelist=bsvc.workspacelist(workMemCode);

        return workspacelist;
    }
    // 좋아요기능
    @PostMapping("DRA005")
    public LIKE DRA005(@RequestParam("boaCode") int boaCode, @RequestParam("boaMemcode") int boaMemcode, @RequestParam("click") int click){
        System.out.println("좋아요 날라왓니??"+boaCode+boaMemcode+click);
        LIKE result=bsvc.DRA005(boaCode,boaMemcode,click);
        System.out.println("result =============="+result);
        return result;
    }
    // 댓글불러오기
    @PostMapping("DRA006")
    public List<COMENT> DRA006(@RequestParam("boaCode") int boaCode){

        List <COMENT> coment=bsvc.DRA006(boaCode);

        return coment;
    }
    // DRA006_1 :댓글작성하기
    @PostMapping("/DRA006_1")
    public  List<COMENT> DRA006_1(@ModelAttribute COMENT coment){

        System.out.println("컨트롤러입니다"+coment);
        List <COMENT> comentIn = bsvc.DRA006_1(coment);
        return comentIn;

    }
    // DRA006_2 : 댓글 수정하기
    @PostMapping("/DRA006_2")
    public  List<COMENT> DRA006_2(@ModelAttribute COMENT coment){

        System.out.println("댓글수정 컨트롤러입니다"+coment);
        List <COMENT> comentIn = bsvc.DRA006_2(coment);
        return comentIn;

    }
    // DRA006_3 : 댓글삭제하기
    @PostMapping("/DRA006_3")
    public  List<COMENT> DRA006_3(@ModelAttribute COMENT coment){

        System.out.println("댓글삭제 컨트롤러입니다"+coment);
        List <COMENT> comentIn = bsvc.DRA006_3(coment);
        return comentIn;

    }
    // 파일 다운로드하기
    @GetMapping("/download")
    public ResponseEntity<Object> download(@RequestParam("boaFileName") String boaFileName) {
        String path = "C:\\Users\\user\\Desktop\\intellijworkspace\\atelier\\src\\main\\resources\\static\\PSW\\mp3\\"+boaFileName;

        try {
            Path filePath = Paths.get(path);
            Resource resource = new InputStreamResource(Files.newInputStream(filePath)); // 파일 resource 얻기

            File file = new File(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());  // 다운로드 되거나 로컬에 저장되는 용도로 쓰이는지를 알려주는 헤더

            return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
        }
    }

    // DRA009_1 : 게시물 수정하기!
    @PostMapping("/DRA009_1")
    public String DRA009_1(@ModelAttribute MemBoardDTO board) throws Exception {
        System.out.println("controller board?? " + board);

        // 게시글이 삽입되었는지 확인하는 check
        String check = bsvc.DRA009_1(board);
        return check;

    }

    // DRA011 : 게시물 보았을떄 히스토리 남기기
    @PostMapping("/DRA011")
    public String DRA011(@ModelAttribute BoardHistoryDTO boardhistory) {
        System.out.println("?????날라왓어>?>>"+boardhistory);
        String result= bsvc.DRA011(boardhistory);
        return result;
    }

    ///////////////////////////////////
    // 프로필

    // 프로필에서 워크스페이스 가져오기
    @PostMapping("/ProfileWorkSpace")
    public List<Object> ProfileWorkSpace(@RequestParam("memCode") int memCode,
                                         @RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = msvc.ProfileWorkSpace(memCode,page);
        return result;
    }

    // 프로필에서 게시글 가져오기
    @PostMapping("/ProfileBoard")
    public List<Object> ProfileBoard(@RequestParam("memCode") int memCode,
                                     @RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = msvc.ProfileBoard(memCode,page);
        return result;
    }

    // 프로필에서 팔로워 가져오기
    @PostMapping("/ProfileFollower")
    public List<Object> ProfileFollower(@RequestParam("memCode") int memCode,
                                        @RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = msvc.ProfileFollower(memCode,page);
        return result;
    }

    // 프로필에서 팔로잉 가져오기
    @PostMapping("/ProfileFollowing")
    public List<Object> ProfileFollowing(@RequestParam("memCode") int memCode,
                                         @RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = msvc.ProfileFollowing(memCode,page);
        return result;
    }

    // 프로필에서 기존에 팔로우 했는지 안했는지 구해오기
    @PostMapping("/followOrNot")
    public int followOrNot(@RequestParam("youCode")int youCode,
                           @RequestParam("myCode")int myCode){
        int result = msvc.followOrNot(youCode,myCode);
        return result;
    }
    // 비밀번호 변경
    @PostMapping("/PRO010")
    public String PRO010(@RequestParam("memCode")int memCode,
                         @RequestParam("oldPw")String oldPw,
                         @RequestParam("newPw")String newPw,
                         @RequestParam("newPwCheck")String newPwCheck,
                         @RequestParam("memId")String memId){
        //System.out.println("컨트롤러 memCode:"+memCode+" 기존비번:"+oldPw+" 새로운비번:"+newPw+" 새로운비번 체크:"+ newPwCheck);
        String result = msvc.PRO010(memCode,oldPw,newPw,newPwCheck,memId);
        return result;
    }

    // 로그인 기록 가져오기
    @PostMapping("/PRO013")
    public List<Object> PRO013(@RequestParam("page")int page,
                               @RequestParam("memCode")int memCode){
        return msvc.PRO013(page,memCode);
    }

    // 프로필 타이핑중 추천 검색
    @PostMapping("/profileResearching")
    public List<MemberDTO> profileResearching(@RequestParam("search")String search){
        return ssvc.profileResearching(search);
    }


    // 파파고 번역
    @PostMapping("/papago")
    public String papago(@RequestParam("source")String source,
                         @RequestParam("target")String target,
                         @RequestParam("content")String content){
        String clientId = "앱아이디";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "앱키";//애플리케이션 클라이언트 시크릿값";

        String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
        String text;
        try {
            text = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = post(apiURL, requestHeaders, text,source,target);
        //System.out.println(responseBody);
        // 결과값에서 파싱하여 번역값 도출
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(responseBody);
        if(element.getAsJsonObject().get("errorMessage") != null){
            System.out.println("번역오류"+element.getAsJsonObject().get("errorCode").getAsString());
            responseBody = element.getAsJsonObject().get("errorCode").getAsString();
        }else if(element.getAsJsonObject().get("message") != null){
            responseBody = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
        }

        return responseBody;
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text, String source,String target){
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source="+source+"&target="+target+"&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (Exception e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    /*문학게시글일 경우 판단해주는 프로그램*/
    @PostMapping("ajaxPayAndCheck")
    public int ajaxPayAndCheck(@RequestParam("boaCode")int boaCode,
                               @RequestParam(value = "memCode", required = false ,defaultValue = "0")int memCode){
        System.out.println("ajaxPayAndCheck "+boaCode+ " "+memCode );
        int result = bsvc.ajaxPayAndCheck(boaCode,memCode);
        System.out.println("result ====== 마지막 값은??? 0이면 결제 1이면 공짜="+result);
        return result;
    }


    @PostMapping("ajaxLitPay")
    public int ajaxLitPay(@RequestParam("boaCode")int boaCode,
                          @RequestParam(value = "memCode", required = false ,defaultValue = "0")int memCode){
        System.out.println("ajaxLitPay ===== "+boaCode+ " "+memCode );
        int result = bsvc.ajaxLitPay(boaCode,memCode);
        System.out.println("ajaxLitPay ==result   "+result);
        return result;
    }

    // 팔로잉 피드
    @PostMapping("/ProfileFollowBoard")
    public List<Object> ProfileFollowBoard(@RequestParam("memCode")int memCode,
                                           @RequestParam("page")int page){
        return ssvc.ProfileFollowBoard(memCode,page);
    }

    // 일반추천
    @PostMapping("/RegularRecommand")
    public List<Object> RegularRecommand(@RequestParam("memCode")int memCode,
                                         @RequestParam("page")int page){
        return ssvc.RegularRecommand(memCode,page);
    }

    //////////////////////////////////////////////////////////////////////////
    // 2023-02-17
    // 태그 중심 추천
    @PostMapping("/TagRecommand")
    public List<MemBoardDTO> TagRecommand(@RequestParam("memCode")int memCode,
                                          @RequestParam("page")int page){
        return ssvc.TagRecommand(memCode,page);
    }

    // 프로필에서 전체게시글 가져오기(상품페이지)
    @PostMapping("/productAll")
    public List<Object> ProfileBoard(@RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = psvc.productAll(page);
        return result;
    }

    // 프로필에서 카테고리별 게시글 가져오기
    @PostMapping("/productCategory")
    public List<Object> productCategory(@RequestParam("category") String category,
                                        @RequestParam("page") int page){
        //System.out.println("컨트롤러 도착");
        List<Object> result = psvc.productCategory(category,page);
        return result;
    }

    // 프로필에서 카테고리별 게시글 가져오기
    @PostMapping("/ProSelect")
    public List<Object> ProSelect(@RequestParam("category") String category,
                                  @RequestParam("page") int page,
                                  @RequestParam("order") String order){
        System.out.println("(select)컨트롤러 도착");
        List<Object> result = psvc.ProSelect(category,page, order);
        return result;
    }

    // 프로필에서 카테고리별 게시글 가져오기
    @PostMapping("/proTime")
    public ProductBoardDTO proTime(@RequestParam("proBoaCode") int proBoaCode){
        System.out.println("(select)컨트롤러 도착");
        ProductBoardDTO result = psvc.proTime(proBoaCode);
        return result;
    }

    // 경매 현재 입찰가 가져오기(최소 입찰가)
    @PostMapping("/auctionGet")
    public AuctionDTO auctionGet(@RequestParam("AucProCode") int AucProCode){
        System.out.println("(auctionGet)컨트롤러 도착, AucProCode : " + AucProCode);
        AuctionDTO result = psvc.auctionGet(AucProCode);
        return result;
    }

    // 경매 새로 입찰하기
    @PostMapping("/auctionInsert")
    public AuctionDTO auctionInsert(@ModelAttribute AuctionDTO auction){
        System.out.println("(auctionInsert)컨트롤러 도착, auction : " + auction);
        AuctionDTO result = psvc.auctionInsert(auction);
        return result;
    }

    // 상품 등록할때 상품 테이블에 있는지 확인하기
    @PostMapping("/checkProduct")
    public String checkProduct(@RequestParam("boaCode") int boaCode){
        System.out.println("(checkProduct)컨트롤러 도착, boaCode : " + boaCode);
        String result = psvc.checkProduct(boaCode);
        return result;
    }

    // 상품 시간이 종료되었을때
    @PostMapping("/auctionFinish")
    public AucMemberDTO auctionFinish(@ModelAttribute AuctionDTO auction){
        System.out.println("(auctionFinish)컨트롤러 도착, auction : " + auction);
        AucMemberDTO result = psvc.auctionFinish(auction);
        return result;
    }

    // indexAddObject : 인덱스 페이지 숫자 변경 ajax
    @PostMapping("/indexAddObject")
    public IndexDTO indexAddObject(){
        IndexDTO indexDTO = new IndexDTO();
        indexDTO = bdao.indexAddObject();
        ModelAndView mav = new ModelAndView();
        mav.addObject("index",indexDTO);

        return indexDTO;
    }

    // 좋아요 갯수 가져오기
    @PostMapping("likenum")
    public int DRA005_1(@RequestParam("boaCode") int boaCode){
        int result=bsvc.likenum(boaCode);
        System.out.println("result =============="+result);
        return result;
    }

}
