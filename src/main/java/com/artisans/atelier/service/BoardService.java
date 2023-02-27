package com.artisans.atelier.service;

import com.artisans.atelier.dao.BOADAO;
import com.artisans.atelier.dao.BoardDAO;
import com.artisans.atelier.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private ModelAndView mav;

    private FISH fish = new FISH();

    private DDAN ddan = new DDAN();

    private final BoardDAO bdao;

    // 서브 카테고리 가져오기
    public List<BoCategoryDTO>
    ajaxcategory(String category) {
        List<BoCategoryDTO> subCategoryList = new ArrayList<>();
        subCategoryList = bdao.ajaxcategory(category);

        System.out.println("subCategoryList 는? "+ subCategoryList);
        return subCategoryList;
    }

    /*워크스페이스 생성*/
    public ModelAndView WOR2(MemWorkSpaceDTO workspace) throws IOException {
        mav =new ModelAndView();
        System.out.println(workspace);

        try {

            /*이미지 파일이 있을 시*/
            if (!workspace.getWorkImg().isEmpty()) {
                String fileName = fish.fileSave("junho/workSpaceImg", workspace.getWorkImg());
                workspace.setWorkImgName(fileName);
            }else {
                /*파일이 없을 시*/
                workspace.setWorkImgName("default.jpg");
            }
            /*워크스페이스카테고리 코드 가져오기*/
            if(workspace.getSubCategory().equals("자유")){
                workspace.setWorkCateCode(0);
            }else {
                int cateCode = bdao.workCateCode(workspace);
                System.out.println(cateCode);
                /*워크 스페이스 카테고리에 코드값 넣기*/
                workspace.setWorkCateCode(cateCode);
            }

            System.out.println("service : " + workspace);

            /*워크스페이스 값 데이터베이스에 넣기*/
            int workcode = bdao.WOR2(workspace);
            System.out.println("workcode =="+workcode);
            System.out.println(workspace);

            String sql = ddan.tigBoard(workspace.getWorkCode(), workspace.getWorkCateCode(), workspace.getBoaCodeList());
            System.out.println("need =======" + sql);


            System.out.println("여기임============"+workspace);
            if(workspace.getLitMethod() != null){
            if(workspace.getLitMethod().equals("유료화")){
                /*유로화로 결정할 경우 값 넣어주기*/
                bdao.litMethod(workspace);
            } else if (workspace.getLitMethod().equals("무료화")) {
                bdao.litMethod(workspace);
            }
            }

            if(workspace.getBoaCodeList().size() >= 1) {
                bdao.tieBoard(sql);
            }
        }
        catch (Exception e){
            mav.setViewName("error");
            e.printStackTrace();
            /*파일이 잘못 업로드 시 바로 삭제.*/
            /*fish.fileDelete("workSpaceImg",workspace.getWorkImg());*/
        }

        mav.setViewName("index");

        return mav;
    }

    /*리스트 페이지 처리*/
    public ModelAndView WOR3(int page, int limit) {
        mav = new ModelAndView();


        System.out.println("[2] controller -> service \n page : " + page);

        int wCount = bdao.wCount();

        /*보여주는 워크스페이스 갯수 */limit = 6;
        PageDTO paging = fish.paging(page,limit,wCount);

        List<MemWorkSpaceDTO> WorkSpaceList = bdao.wList(paging);
        // member 정보도 가져오기
        
        System.out.println(WorkSpaceList);

        // model
        mav.addObject("WorkSpaceList", WorkSpaceList);
        mav.addObject("paging", paging);

        // view
        mav.setViewName("workspace");

        return mav;
    }

    /*뷰처리*/
    public ModelAndView WOR4(int workCode, int page, int limit) {
        mav = new ModelAndView();

        /*워크스페이스 값 받기*/
        MemWorkSpaceDTO workspace= bdao.WOR4(workCode);

        mav.addObject("workspace",workspace);

        /*게시글 갯수 구하기*/
        int boardCount = bdao.WOR4Count(workCode);
        System.out.println("workCode ==============" + workCode);
        limit = 6;

        /*워크스페이스 내 게시글 정리 오래된 것을 나중에 출력*/
        PageDTO paging = fish.paging(page,limit,boardCount);
        paging.setWorkCode(workCode);
        mav.addObject("paging", paging);
        System.out.println("paging ==============="+paging);

        List<MemBoardDTO> boardList = bdao.boardList(paging);
        System.out.println("boardList ==============="+boardList);


        mav.addObject("boardList",boardList);

        /*카테고리 별 나누기 */
        String category = bdao.getCategoryBoard(workCode);
        System.out.println("category ====== "+category);




        if(category.equals("음악")) {
            mav.setViewName("Mus_View.html");
        }else if(category.equals("문학")){
            // 문학 정보 가져오기
            LiteratureDTO literature = bdao.WOR4Lit(workCode);
            System.out.println("여긴 옴?======================="+literature);
            if(literature !=null){
                mav.addObject("literature",literature);
            }
            mav.setViewName("WOR_View");
        }
        else{
            mav.setViewName("WOR_View");
        }

        return mav;
    }

    /*워크스페이스 페이지 이동*/
    public ModelAndView WOR5(int workCode) {
        mav = new ModelAndView();

        /*워크스페이스 값 받기*/
        MemWorkSpaceDTO workspace= bdao.WOR4(workCode);
        System.out.println(workspace);

        mav.addObject("workspace",workspace);

        if(workspace !=null) {
            mav.setViewName("WOR_Modify");
        }else {
            mav.setViewName("index");
        }

        return mav;
    }

    /*워크스페이스 수정*/
    public ModelAndView WOR6(MemWorkSpaceDTO workspace) {
        mav = new ModelAndView();
        System.out.println("workspace ========================="+workspace);

        //워크스페이스카테고리 코드 가져오기*//*
        if(workspace.getSubCategory()==null){
        }else {
            int cateCode = bdao.workCateCode(workspace);
            System.out.println(cateCode);
                //*워크 스페이스 카테고리에 코드값 넣기*//*
            workspace.setWorkCateCode(cateCode);
        }

        try {
            System.out.println("workspcae + "+workspace);
            /*이미지 파일이 있을 시*/
            if (!workspace.getWorkImg().isEmpty()) {
                /*사진 업로드*/
                String fileName = fish.fileSave("junho/workSpaceImg", workspace.getWorkImg());
                /*기존 사진 삭제*/
                fish.fileDelete("junho/workSpaceImg",workspace.getWorkImgName());
                /*workImgName수정*/
                workspace.setWorkImgName(fileName);
            }else {
                /*파일이 없을 시*/

            }

            System.out.println("service : " + workspace);

            /*워크스페이스 값 데이터베이스에 넣기*/
            bdao.WOR6(workspace);

            /*워크스페이스 게시글 넣기 처리*/
            String sql = ddan.tigBoard(workspace.getWorkCode(), workspace.getWorkCateCode(), workspace.getBoaCodeList());
            System.out.println("need =======" + sql);

            if(workspace.getBoaCodeList().size() >= 1) {
                bdao.tieBoard(sql);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            mav.setViewName("error");

            /*파일이 잘못 업로드 시 바로 삭제.*/
            /*fish.fileDelete("workSpaceImg",workspace.getWorkImg());*/
        }

        /*수정성공 시 뷰처리*/
        mav.setViewName("index");

        return mav;
    }

    /*음악게시판 리스트 출력*/
    public ModelAndView MUS003(String category, int cateCode) {

        mav = new ModelAndView();

        /*3개까지 최신순으로 가져오기 */
        List<MemWorkSpaceDTO> WorkSpaceList = bdao.MUS003(category);
        // member 정보도 가져오기
        System.out.println(WorkSpaceList);
        // model
        mav.addObject("WorkSpaceList", WorkSpaceList);


        /*6개까지 최신순으로 가져오기 */
        List<MemBoardDTO> BoardList = bdao.MUS003B(category);
        // member 정보도 가져오기
        System.out.println(BoardList);
        // model
        mav.addObject("BoardList", BoardList);

        /*카테고리 가져오기*/
        mav.addObject("category",category);
        mav.addObject("cateCode",cateCode);


        // view
        mav.setViewName("music");


        return mav;

    }
    /*뮤직게시판 다 불러오기*/
    public ModelAndView MUS003Code(int cateCode, String category) {
        mav = new ModelAndView();
        System.out.println("========================"+cateCode);
        /*3개까지 최신순으로 가져오기 */
        List<MemWorkSpaceDTO> WorkSpaceList = bdao.MUS003Code(cateCode);
        // member 정보도 가져오기
        System.out.println("WorkSpaceList"+WorkSpaceList);
        // model
        mav.addObject("WorkSpaceList", WorkSpaceList);


        /*6개까지 최신순으로 가져오기 */
        List<MemBoardDTO> BoardList = bdao.MUS003CodeB(cateCode);
        // member 정보도 가져오기
        System.out.println("BoardList"+BoardList);
        // model
        mav.addObject("BoardList", BoardList);

        // view
        mav.setViewName("music");
        /*카테고리 가져오기*/
        /*카테고리 가져오기*/
        mav.addObject("category",category);
        mav.addObject("cateCode",cateCode);


        return mav;
    }

    /*워크스페이스 음악 더보기*/
    public ModelAndView MUS004(String category, int catecode, int page, int limit, String orderby) {
        mav = new ModelAndView();
        System.out.println("여기오류?");

        BoCategoryDTO categoryDTO = new BoCategoryDTO();
        PageDTO paging = new PageDTO();

        /*카테고리 대입*/
        categoryDTO.setCateCode(catecode);
        categoryDTO.setCategory(category);
        System.out.println(categoryDTO);


        /*카테고리에 맞는 워크스페이스 갯수 구하기*/
        int wCount = bdao.MUS004Count(categoryDTO);
        System.out.println("======================================="+wCount);
        System.out.println("여기오류?");

        /*보여주는 워크스페이스 갯수 */limit = 6;
        paging = fish.paging(page,limit,wCount);

        /*카테고리를 페이지 DTO에 넣어준다*/
        paging.setCategory(category);
        paging.setCateCode(catecode);
        paging.setOrderby(orderby);
        System.out.println(paging);
        System.out.println("여기감 ㅜㄴ제냐? tlqkffus들아 ㄴㅁㅇㅊㅊㅇㄴㅁㅁㄴㄹ");

        /*페이지 처리를 위한 워크스페이스 갯수 구하기 */
        List<MemWorkSpaceDTO> WorkSpaceList = bdao.MUS004List(paging);
        System.out.println("여기오류?");
        // member 정보도 가져오기
        System.out.println(WorkSpaceList);

        // model
        mav.addObject("WorkSpaceList", WorkSpaceList);
        mav.addObject("paging", paging);


        //view
        mav.setViewName("Mus_WorkspcaeList");

        return mav;
    }
    /*음악 워크스페이스 페이지 이동용*/
    public ModelAndView MUS004Code(String category, int catecode, int page, int limit, String orderby) {
        mav = new ModelAndView();
        PageDTO paging = new PageDTO();
        System.out.println("페이지 이동용이다 ㅅㅂ 여기로 오지?");
        
        /*카테고리 코드로 카테고리 찾기*/
        String cate = bdao.workCodeSet(catecode);
        category = cate;

        BoCategoryDTO categoryDTO = new BoCategoryDTO();
        categoryDTO.setCateCode(catecode);
        categoryDTO.setCategory(category);


        /*카테고리에 맞는 워크스페이스 갯수 구하기*/
        int wCount = bdao.MUS004Count(categoryDTO);
        System.out.println("======================================="+wCount);

        /*보여주는 워크스페이스 갯수 */limit = 6;
        paging = fish.paging(page,limit,wCount);

        /*카테고리를 페이지 DTO에 넣어준다*/
        paging.setCategory(category);
        paging.setCateCode(catecode);
        paging.setOrderby(orderby);

        System.out.println(paging);

        /*페이지 처리를 위한 워크스페이스 갯수 구하기 */
        List<MemWorkSpaceDTO> WorkSpaceList = bdao.MUS004List(paging);
        // member 정보도 가져오기
        System.out.println(WorkSpaceList);

        // model
        mav.addObject("WorkSpaceList", WorkSpaceList);
        mav.addObject("paging", paging);

        //view
        mav.setViewName("Mus_WorkspcaeList");


        return mav;
    }



    /////////////////////////////////////////////////////////////////////////////////

    private final BOADAO bbdao;


    public String DRA002(MemBoardDTO board) throws IOException {


        MultipartFile bFile = board.getBoaFile();
        if(!bFile.isEmpty()){
            String originFileName= bFile.getOriginalFilename();

            String uuid = UUID.randomUUID().toString().substring(0,8);

            String bfileName= uuid+"_"+originFileName;

            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/mp3");

            String savePath=path+"/"+bfileName;

            board.setBoaFileName(bfileName);

            bFile.transferTo(new File(savePath));
        }
        // 파일이 없을떄
        else {
            board.setBoaFileName("null");
        }

        MultipartFile bestFile = board.getBoaBestFile();
        if(!bestFile.isEmpty()){
            String bestFileoriginFileName= bestFile.getOriginalFilename();

            String uuid = UUID.randomUUID().toString().substring(0,8);

            String bestFileName= uuid+"_"+bestFileoriginFileName;

            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/bestimg");

            String savePath=path+"/"+bestFileName;

            board.setBoaBestImg(bestFileName);

            bestFile.transferTo(new File(savePath));
        }
        // 파일이 없을떄
        else {
            board.setBoaBestImg("default.jpg");
        }


        // 가져온 이미지 파일이 존재한다면
        if(!board.getImgname().isEmpty()){

            // 이미지삽입시 임시 저장되는 폴더
            Path path1 = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/imsi");
            // for문으로 List타입으로 image 이름 가져오기
            for(int i=0; i<board.getImgname().size(); i++){
                List list=board.getImgname();
                System.out.println("listget(i)의 값"+ list.get(i));

                // 게시글 작성클릭시 진짜로 저장되는 폴더경로
                String folderPath1 = "src/main/resources/static/PSW/upload/";
                Path path2 = Paths.get(System.getProperty("user.dir"));

                String save_folder = path2+"/"+folderPath1;

                // 파일옮기기 경로설정
                // file은 imsi폴더에 있는 파일
                Path file = Paths.get(path1+"/"+ list.get(i));
                // newfile 파일를 옮길 곳
                Path newFile = Paths.get(save_folder+list.get(i));

                try {
                    // 파일 옮기기
                    Path newFilePath = Files.move(file, newFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        // 가져온 게시글 내용을 saveStr에 저장
        String saveStr = board.getBoaContent();


        // 텍스트 파일의 폴더와 이름을 받아온다.
        String folderPath2 = "src/main/resources/static/PSW/text";
        Path path3 = Paths.get(System.getProperty("user.dir"), folderPath2);
        // 텍스트에 저장할 UUID 생성
        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();

        // 현재 날짜/시간 출력
        System.out.println(now); // 2021-06-17T06:43:21.419878100


        // 포맷팅
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));


        String memimageUUID = UUID.randomUUID().toString().substring(0,4);
        String filePath = path3+"/"+formatedNow+memimageUUID+".txt";


        // String filePath = "c:\\Temp\\test5.txt";
        try {
            // 텍스트 파일로 저장하기
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(saveStr);
            fileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 텍스트파일 이름을 board.boaContent에 저장해서 DB에 보내기
        board.setBoaContent(formatedNow+memimageUUID);

        System.out.println("controller -> service : " +board);
        int result=bbdao.bowrite(board);
        String check=null;
        if(result>0){
            check="OK";
        }else{
            check="NO";
        }
        return check;
    }

    public ModelAndView DRA003() {
        mav=new ModelAndView();
        List<MemWorkSpaceDTO> workSpaceDTOS= bbdao.DRA003_1();
        List<MemBoardDTO> boardDTOS = bbdao.DRA003();



        System.out.println("boardDTOS 첫번쨰"+boardDTOS.get(1));

        mav.addObject("workList",workSpaceDTOS);
        mav.addObject("boardList",boardDTOS);
        mav.setViewName("Dra_list");

        return mav;
    }
    // 게시물 상세보기 페이지
    public ModelAndView DRA004(int boaCode) throws IOException {
        mav=new ModelAndView();



       
        MemBoardDTO boardDTOS=bbdao.DRA004(boaCode);
        System.out.println("mapper에서 가져온 게시물값 "+boardDTOS);

        try{
        String folderPath2 = "src/main/resources/static/PSW/text";
        Path path = Paths.get(System.getProperty("user.dir"),folderPath2);

        BufferedReader reader = new BufferedReader(

                new FileReader(path+"/"+boardDTOS.getBoaContent()+".txt")

        );
        System.out.println("파일 읽기 성공");
        String result="";
        // 첫줄은 엔터를 적용하지 않기 위해서 first 사용
        boolean first = true;

        String input;
        // 파일 읽기!
        while ((input = reader.readLine()) != null) {
            if (first) {
                result += input;
                first = false;
            } else {
                result += "\n" + input;
            }
        }

        // 리더닫기
        reader.close();
        System.out.println("result????????"+result);
        boardDTOS.setBoaContent(result);
        }catch (Exception e){
            System.out.println("게시글 txt파일 없음");
        }

        /*카테고리 파악*/
        String category = bbdao.getCategoryBoard(boaCode);
        System.out.println(category);

        if(category.equals("음악")){
            System.out.println("음악페이지 이동");
            mav.addObject("bview",boardDTOS);
            mav.setViewName("Mus_BoardView");
        }else{
            mav.addObject("bview",boardDTOS);
            mav.setViewName("Dra_View");
        }

        return mav;
    }

    public List<MemWorkSpaceDTO> workspacelist(int workMemCode) {
        List<MemWorkSpaceDTO> memWorkSpaceList = bbdao.workspacelist(workMemCode);
        System.out.println("memWorkSpaceList ??"+memWorkSpaceList);
        return memWorkSpaceList;
    }

    public LIKE DRA005(int boaCode, int boaMemcode, int click) {
        LIKE like = new LIKE();

        // 회원코드와 게시물번호를 저장
        like.setBoaCode(boaCode);
        like.setBoaMemcode(boaMemcode);

        // 전에 눌렀다면 1 안눌렀다면 0
        int coulike=bbdao.DRA005_1(like);

        if(click ==1){

            // 좋아요 눌려있을떄
            if(coulike==1){
                bbdao.DRA005_3(like);
                coulike=0;
            }
            // 좋아요 안눌러있을떄
            else{
                bbdao.DRA005_2(like);
                coulike=1;
            }
        }

        int count=bbdao.DRA005(boaCode);
        System.out.println("좋아요 갯수"+count);
        like.setMLCount(count);
        like.setMLDo(coulike);
        return like;
    }

    public List<COMENT> DRA006(int boaCode) {



        List<COMENT> coment =bbdao.DRA006(boaCode);
        System.out.println("댓글불러오기!"+coment);


        return coment;
    }

    // 댓글 작성
    public List<COMENT> DRA006_1(COMENT coment) {

        List <COMENT> comentIn;
        System.out.println("서비스입니디ㅏ!!"+coment);
        try{
            bbdao.DRA006_1(coment);
            comentIn = bbdao.DRA006(coment.getComBoaCode());
        }catch (Exception e){
            comentIn = null;
            System.out.println("댓글에러?");
        }

        return comentIn;
    }

    // 댓글수정하기
    public List<COMENT> DRA006_2(COMENT coment) {
        List <COMENT> comentIn;
        System.out.println("서비스입니디ㅏ!!"+coment);
        try{
            coment.setCoModi(1);
            bbdao.DRA006_2(coment);
            comentIn = bbdao.DRA006(coment.getComBoaCode());
        }catch (Exception e){
            comentIn = null;
            System.out.println("댓글에러?");
        }
        return comentIn;
    }

    public List<COMENT> DRA006_3(COMENT coment) {
        List <COMENT> comentIn;

        int result= bbdao.DRA006_3(coment);

        if(result>0){
            comentIn = bbdao.DRA006(coment.getComBoaCode());
        }else{
            comentIn=null;
        }

        return comentIn;
    }

    // 게시물 리스트 가져오기
    public ModelAndView DRA007(String category, int page, int limit, int cateCode) {
        mav=new ModelAndView();

        String sql= " SELECT COUNT(*) FROM finalboardlist ";
        if(!category.equals("자유") & !category.equals("all"))
        {   /*자유랑 통합이 아닐경우*/
            sql+="WHERE  CATEGORY = '"+category+"'";
        }
        int bCount = bbdao.bCount(sql);



        /*보여주는 워크스페이스 갯수 */limit = 6;

        PageDTO paging = fish.paging(page,limit,bCount);
        paging.setCategory(category);

        paging.setCateCode(cateCode);
        System.out.println("맵퍼가기전 paging??"+paging);

        // 워크스페이스 가져오기
        List<MemWorkSpaceDTO> workSpaceDTOS= bbdao.DRA007_1(paging);
        // 게시물 가져오기
        List<MemBoardDTO> boardDTOS =bbdao.DRA007(paging);


        mav.addObject("paging", paging);


        System.out.println("dao->service :게시물리스트 가져오기"+boardDTOS);
        System.out.println("dao->service :워크스페이스!!!!!! 가져오기"+workSpaceDTOS);
        mav.addObject("category",category);
        mav.addObject("blist",boardDTOS);
        mav.addObject("wlist",workSpaceDTOS);
        mav.setViewName("Dra_bList");

        return mav;
    }
    // 조회수 증가!
    public void viewCountUp(int boaCode) {

        bbdao.viewCountUp(boaCode);

    }

    public ModelAndView DRA008(String category, int page, int limit, int cateCode, String orderby) {
        mav=new ModelAndView();

        String sql=" SELECT COUNT(*) FROM finalboardlist ";
        if(!category.equals("자유") & !category.equals("all")){
            sql+="WHERE  CATEGORY = '"+category+"'";
        }
        int bCount = bbdao.bCount(sql);

        /*보여주는 워크스페이스 갯수 */limit = 6;

        PageDTO paging = fish.paging(page,limit,bCount);
        paging.setCategory(category);
        paging.setOrderby(orderby);
        paging.setCateCode(cateCode);
        System.out.println("맵퍼가기전 paging??"+paging);

        // 게시물 가져오기
        List<MemBoardDTO> boardDTOS =bbdao.DRA008(paging);

        System.out.println("맵퍼갔다온후 paging??"+paging);
        System.out.println("맵퍼갔다온후 boardDTOS??"+boardDTOS);
        mav.addObject("paging", paging);


        System.out.println("dao->service :게시물리스트 가져오기"+boardDTOS);
        ;
        System.out.println("category ==================== "+ category);

        mav.addObject("blist",boardDTOS);
        if(category.equals("음악")){
            System.out.println("이거됌?");
            mav.setViewName("Mus_BoardList");
        }else {
            mav.setViewName("Dra_bListbMore");
        }


        return mav;
    }

    public ModelAndView DRA008_1(String category, int page, int limit, int cateCode, String orderby) {
        mav=new ModelAndView();

        String sql=" SELECT COUNT(*) FROM finalworkspacelist ";
        if(!category.equals("자유") & !category.equals("all")){
            sql+=" WHERE  CATEGORY = '"+category+"'";
        }
        System.out.println(sql);
        int bwCountsql = bbdao.bwCountsql(sql);

        System.out.println("bwCount ====="+bwCountsql);

        //*보여주는 워크스페이스 갯수 *//*limit = 5;

        PageDTO paging = fish.paging(page,limit,bwCountsql);
        paging.setCategory(category);
        paging.setOrderby(orderby);
        paging.setCateCode(cateCode);
        System.out.println("맵퍼가기전 paging??"+paging);

        // 워크스페이스 가져오기
        List<MemWorkSpaceDTO> workSpaceDTOS= bbdao.DRA008_1(paging);

        mav.addObject("paging", paging);

        System.out.println("dao->service :워크스페이스!!!!!! 가져오기"+workSpaceDTOS);

        mav.addObject("wlist",workSpaceDTOS);
        mav.setViewName("Dra_bListwMore");

        return mav;
    }
    // 게시물 수정하기 폼이동
    public ModelAndView DRA009(int boaCode) throws IOException {
        mav=new ModelAndView();
        MemBoardDTO boardDTOS=bbdao.DRA004(boaCode);
        System.out.println("mapper에서 가져온 게시물값 "+boardDTOS);

        String folderPath2 = "src/main/resources/static/PSW/text";
        Path path = Paths.get(System.getProperty("user.dir"),folderPath2);

        BufferedReader reader = new BufferedReader(

                new FileReader(path+"/"+boardDTOS.getBoaContent()+".txt")

        );
        System.out.println("파일 읽기 성공");
        String result="";
        // 첫줄은 엔터를 적용하지 않기 위해서 first 사용
        boolean first = true;

        String input;
        // 파일 읽기!
        while ((input = reader.readLine()) != null) {
            if (first) {
                result += input;
                first = false;
            } else {
                result += "\n" + input;
            }
        }

        // 리더닫기
        reader.close();
        System.out.println("result????????"+result);

        boardDTOS.setBoaContentname(boardDTOS.getBoaContent());
        boardDTOS.setBoaContent(result);



        mav.addObject("bview",boardDTOS);
        mav.setViewName("Dra_Modify");


        return mav;
    }
    // 게시물 수정하기!
    public String DRA009_1(MemBoardDTO board) throws IOException {
        MultipartFile bFile = board.getBoaFile();
        if(!bFile.isEmpty()){
            String originFileName= bFile.getOriginalFilename();

            String uuid = UUID.randomUUID().toString().substring(0,8);

            String bfileName= uuid+"_"+originFileName;

            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/mp3");

            String savePath=path+"/"+bfileName;

            String deletePath=path+"/"+board.getBoaFileName();

            File deleteFile = new File(deletePath);

            if(deleteFile.exists()) {
                deleteFile.delete();
                System.out.println("기존 파일 삭제 성공!");
            } else {
                System.out.println("기존 파일 삭제 실패!");
            }


            board.setBoaFileName(bfileName);

            bFile.transferTo(new File(savePath));
        }

        MultipartFile bestFile = board.getBoaBestFile();
        if(!bestFile.isEmpty()){
            String bestFileoriginFileName= bestFile.getOriginalFilename();

            String uuid = UUID.randomUUID().toString().substring(0,8);

            String bestFileName= uuid+"_"+bestFileoriginFileName;

            Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/bestimg");

            String savePath=path+"/"+bestFileName;

            String deletePath=path+"/"+board.getBoaBestImg();

            File deleteFile = new File(deletePath);

            if(deleteFile.exists()) {
                deleteFile.delete();
                System.out.println("기존 파일 삭제 성공!");
            } else {
                System.out.println("기존 파일 삭제 실패!");
            }

            board.setBoaBestImg(bestFileName);

            bestFile.transferTo(new File(savePath));
        }



        // 가져온 이미지 파일이 존재한다면
        if(!board.getImgname().isEmpty()){

            // 이미지삽입시 임시 저장되는 폴더
            Path path1 = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/imsi");
            // for문으로 List타입으로 image 이름 가져오기
            for(int i=0; i<board.getImgname().size(); i++){
                List list=board.getImgname();
                System.out.println("listget(i)의 값"+ list.get(i));

                // 게시글 작성클릭시 진짜로 저장되는 폴더경로
                String folderPath1 = "src/main/resources/static/PSW/upload/";
                Path path2 = Paths.get(System.getProperty("user.dir"));

                String save_folder = path2+"/"+folderPath1;

                // 파일옮기기 경로설정
                // file은 imsi폴더에 있는 파일
                Path file = Paths.get(path1+"/"+ list.get(i));
                // newfile 파일를 옮길 곳
                Path newFile = Paths.get(save_folder+list.get(i));

                try {
                    // 파일 옮기기
                    Path newFilePath = Files.move(file, newFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        // 가져온 게시글 내용을 saveStr에 저장
        String saveStr = board.getBoaContent();


        // 텍스트 파일의 폴더와 이름을 받아온다.
        String folderPath2 = "src/main/resources/static/PSW/text";
        Path path3 = Paths.get(System.getProperty("user.dir"), folderPath2);
        String filePath = path3+"/"+board.getBoaContentname()+".txt";

        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete();
            System.out.println("기존 파일 삭제 성공!");
        } else {
            System.out.println("기존 파일 삭제 실패!");
        }

        // String filePath = "c:\\Temp\\test5.txt";
        try {
            // 텍스트 파일로 저장하기
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(saveStr);
            fileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 텍스트파일 이름을 board.boaContent에 저장해서 DB에 보내기
        board.setBoaContent(board.getBoaContentname());

        System.out.println("controller -> service : " +board);
        int result=bbdao.DRA009_1(board);
        String check=null;
        if(result>0){
            check="OK";
        }else{
            check="NO";
        }
        return check;
    }

    // 게시글 삭제하기!!
    public ModelAndView DRA010(int boaCode) {
        mav=new ModelAndView();

        // 좋아요 삭제
        bbdao.DRA010_1(boaCode);
        // 댓글 지우기
        bbdao.DRA010_2(boaCode);
        // 상품 삭제
        bbdao.DRA010_3(boaCode);
        // 문학테이블 삭제
        bbdao.DRA010_4(boaCode);

        MemBoardDTO board=bbdao.DRA004(boaCode);
        bbdao.DRA011_3(boaCode);
        System.out.println("삭제할 꺼야,, board : ?? "+board);
        int result=bbdao.DRA010(boaCode);

        if(result>0){
            System.out.println("삭제성공");
            Path bestpath = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/bestimg");

            String deletePath=bestpath+"/"+board.getBoaBestImg();

            File deleteFile = new File(deletePath);

            if(deleteFile.exists()) {
                if(!board.getBoaBestImg().equals("default.jpg")){
                    deleteFile.delete();
                    System.out.println("기존 파일 삭제 성공!");
                }

            } else {
                System.out.println("기존 파일 삭제 실패!");
            }

            Path mp3path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/PSW/mp3");
            String deletePath1=mp3path+"/"+board.getBoaFileName();

            File deleteFile1 = new File(deletePath1);

            if(deleteFile.exists()) {
                deleteFile1.delete();
                System.out.println("기존 파일 삭제 성공!");
            } else {
                System.out.println("기존 파일 삭제 실패!");
            }
        }else{
            System.out.println("삭제실패");
        }
        mav.setViewName("index");


        return mav;
    }

    /* ajax워크스페이스에서 생성시 게시물 검색기능*/
    public List<MemBoardDTO> ajaxsearchAll(String devide, String category, String searchName, String memCode) {
        // 회원 검색

        if (category.equals("자유")) {
            category = "";
        } else {
            category = "AND category = '" + category + "'";
        }
            //System.out.println(devide);
            String sql = "SELECT * FROM memBoard WHERE boaWorkcode = 0 and boaMemcode = "+memCode+" AND " + devide + " like '%" + searchName + "%' " + category;
        System.out.println(sql);
            List<MemBoardDTO> result = bdao.ajaxsearchAll(sql);
        System.out.println(result);

            return result;


    }
    public String DRA011(BoardHistoryDTO boardhistory) {

        int historycount = bbdao.DRA011_1(boardhistory);
        if(historycount>100){
            bbdao.DRA011_2(boardhistory);
        }
        int result1= bbdao.DRA011(boardhistory);
        String result;

        if(result1>0){
            System.out.println("히스토리 삽입성공");
            result="OK";
        }else {
            System.out.println("삽입실패");
            result="NO";
        }

        return result;
    }

    /////////////////////////////////////////////////////////////
    /*테스트 용 */
    public ModelAndView testcontroller(List boaCodeList) {
        mav = new ModelAndView();
        String sql = "update board set boaWorkcode = 2 where ";
        for (int i=0; i<boaCodeList.size();i++){
            if (i == 0) {
                sql += "boaCode = " + boaCodeList.get(i);
            }else {
                sql += " OR boaCode = "+boaCodeList.get(i);
            }

        }
        System.out.println("sql == "+sql);

        return mav;
    }


    /*음악을 들을 시 히스토리에 테이블 넣기*/
    public String ajaxMusHistroyInput(BoardHistoryDTO boardhistory) {
        mav = new ModelAndView();
        String check = null;
        int num = bdao.ajaxMusHistroyInput(boardhistory);

        /*히스토리 */
        int count = bbdao.DRA011_1(boardhistory);
        System.out.println(count);
        
        
        if(num >0){
            check = "OK";
            System.out.println("일단 히스토리에 넣어짐");
            if(count>10){
                System.out.println("여긴오냐>/");
                bbdao.DRA011_2(boardhistory);
                /*지금 오류 발생*/
                System.out.println("삭제완료");
            }
        }else {
            check = "NO";
        }
        
        return check;
    }


    ////////////////////////////// 주말 추가기능
    /*워크스페이스 내 게시글 빼기*/
    public ModelAndView WOR7(int boaCode, int workCode) {
        mav= new ModelAndView();
        int result = bdao.WOR7(boaCode);

        /*뷰처리*/
        if(result > 0){
            /*성공시 워크스페이스 뷰로 이동*/
            mav.setViewName("redirect:/WOR4?workCode="+workCode);
        }else{
            mav.setViewName("index");
        }

        return mav;
    }
    /*워크스페이스 더보기 기능*/
    public ModelAndView WOR3_category(String category, int cateCode, int page, int limit, String orderby) {
        mav = new ModelAndView();
        return mav;
    }

    /*문학게시글인지 판단해서 결제 모든걸 처리*/
    public int ajaxPayAndCheck(int boaCode, int memCode) {
        int result = 0;
        LiteratureDTO literature = new LiteratureDTO();
        /*워크코드 가져오기*/
        MemBoardDTO board = bdao.getWorkCodeFormLit(boaCode);
        System.out.println("board ==== "+ board);

        if (board.getBoaWorkcode() == 0){
            return result=1;
        }

        if(board.getBoaWorkcode()!=0){
            /*워크스페이스 전체 값 가져오기*/
            MemWorkSpaceDTO workspace = bdao.WOR4(board.getBoaWorkcode());
            System.out.println(workspace);
            /*문학 테이블 값 가져오기*/
            literature = bdao.getAllAboutLit(board.getBoaWorkcode());
            System.out.println("literature======"+literature);



            if(literature.getLitMethod().equals("유료화")){
                /*워크스페이스 내 카운트로 유로화이기에 순서를 구한다.*/
                int litCount = literature.getLitCount();
                workspace.setLitCount(litCount);
                workspace.setBoaCode(boaCode);
                workspace.setPayMemCode(memCode);
                System.out.println(workspace);

                /*순서에 맞는 */
                MemWorkSpaceDTO workspace2 = bdao.getLitNeedPay(workspace);
                System.out.println("workspace2===????"+workspace2);
                if(workspace2!=null){
                    /*유로화이면서 가격을 내야한다면 지출할 가격을 result에 넣어준다*/
                    result = literature.getLitPrice();
                    /*같은 걸 지출한 내역이 있는지 확인*/
                    System.out.println(literature);
                    literature.setLitBoaCode(boaCode);
                    literature.setMemCode(memCode);

                    Integer payCode = bdao.getPayCodeLit(literature);
                    System.out.println("payCode ========="+payCode);

                    /*결과가 없으면 0이 나옴*/
                    if(payCode !=null){
                        /*결제한 내역이 있다면 무료*/
                        return result=1;
                    } else if (board.getBoaMemcode() == memCode) {
                        return result=1;
                    } else if (board.getBoaWorkcode() == 0){
                        return result=1;
                    }

                }else{
                     return result=1;
                }
                    

                System.out.println("result==========="+result);
                System.out.println("literature.getLitCount()====="+literature.getLitCount());
                
            }else {

                result=0;
            }
        }else {
            result=0;
        }
        /*result = 0이면 바로 계산, 1이면 가격이 표시된 result일 경우 가격이 나오면서 물어보고 계산*/
        return result;
    }

    /*문학 결제 처리*/
    public int ajaxLitPay(int boaCode, int memCode) {
        int result = 0;
        LiteratureDTO literature = new LiteratureDTO();
        System.out.println("여긴오냐?");
        /*게시글 전체 정보 가져오기*/
        MemBoardDTO board = bdao.getWorkCodeFormLit(boaCode);
        /*문학 정보 가져오기*/
        literature = bdao.getAllAboutLit(board.getBoaWorkcode());
        /*문학의 가격 알아내기*/
        int litCount = literature.getLitPrice();
        /*회원의 소지하는 포인트 가져오기*/
        int memPoint = bdao.getMemPointLit(memCode);
        int sellPoint = bdao.getSellPointLit(board.getBoaMemcode());
        int memPointafterPay =memPoint - litCount;
        int memPointafterSell = sellPoint + litCount;
        System.out.println("여긴오냐??????????????//"+board.getBoaWorkcode()+"//"+ litCount+"//"+memPoint +"//"+memPointafterPay+"//"+board.getBoaMemcode());
        /*소지하고 있느 돈이 충분하면 사용 돈을 차감 시킨다*/
        if(memPointafterPay>=0){
            /*문학 DTO에 판매한 회원코드를 넣어준다*/
            literature.setMemCodeSell(board.getBoaMemcode());
            /*회원코드를 넣어준다*/
            literature.setMemCode(memCode);
            /*계산이 끝난 가격을 넣어준다.*/
            literature.setMemPoint(memPointafterPay);
            literature.setMemCodeSellPoint(memPointafterSell);
            /*게시판의 넘버를 넣어준다*/
            literature.setLitBoaCode(boaCode);
            System.out.println(literature);
            /*회원의 포인트를 바꿔준다.*/
            int num = bdao.memPointafterPayLit(literature);
            System.out.println("여긴오냐?3333333333333333333333 num ="+num);
            if(num != 0){

                System.out.println("lit========="+literature);

                bdao.setPointHistoryLit(literature);
                result = 1;
            }else{
                result = 0;
            }
        }else{
            /*소지한 돈 부족*/
            return result = 2;
        }

        return result;
    }

    /*워크스페이스 삭제*/
    public ModelAndView WOR8(int workCode) {
        mav = new ModelAndView();

        /*문학 삭제*/

        /*워크스페이스의 사진과 관련 물품 다 삭제해야함*/
        /*워크스페이스 정보 가져오기*/
        MemWorkSpaceDTO workspace = bdao.WOR4(workCode);

        /*게시글 정보 다 불러오기*/
        List<MemBoardDTO> boardList = bdao.tiedbBoard(workCode);

        /*문학 관련 삭제*/
        if(workspace.getCategory().equals("문학")){
            bdao.WOR8_Workspace_lit(workCode);
        }

        if(boardList != null){
            for(int i=0;i<boardList.size();i++){
                System.out.println("i번째"+boardList.get(i));
                bdao.WOR8_BoardLit(boardList.get(i).getBoaCode());

                // 좋아요 삭제
                bbdao.DRA010_1(boardList.get(i).getBoaCode());
                // 댓글 지우기
                bbdao.DRA010_2(boardList.get(i).getBoaCode());
                // 상품 삭제
                bbdao.DRA010_3(boardList.get(i).getBoaCode());
                /*문학 삭제*/
                bbdao.DRA010_4(boardList.get(i).getBoaCode());
                /*히스토리 삭제*/
                bdao.WOR8_Board(boardList.get(i).getBoaCode());
                /*찐 게시글 삭제*/
                int BoardDeleteCheck =bdao.WOR8_BoardDelete(boardList.get(i).getBoaCode());
                System.out.println("BoardDeleteCheck는???"+BoardDeleteCheck);
                if(BoardDeleteCheck !=0){
                    fish.fileDelete("PSW/text",boardList.get(i).getBoaContent()+".txt");
                    System.out.println(boardList.get(i).getBoaContent());


                    if(boardList.get(i).getBoaBestImg().equals("default.jpg")){
                        System.out.println("default.jpg라 삭제 x");
                    }else{
                        fish.fileDelete("PSW/bestimg",boardList.get(i).getBoaBestImg());
                    }
                    if(boardList.get(i).getBoaFileName().equals("null")){
                        System.out.println("null이라 삭제 x");
                    }else{
                        fish.fileDelete("PSW/mp3",boardList.get(i).getBoaFileName());
                    }
                }
            }
        }

        /*워크스페이스 삭제*/
        int workspaceDeleteCheck = bdao.WOR8_Workspace(workCode);

        if(workspaceDeleteCheck>0 && !workspace.getWorkImgName().equals("default.jpg")) {
            /*워크스페이스 이미지 파일 삭제*/
            fish.fileDelete("junho/workSpaceImg", workspace.getWorkImgName());
        }

        mav.setViewName("index");
        return mav;
    }

    public int likenum(int boaCode) {
        int result = bbdao.likenum(boaCode);

        return result;
    }

}





