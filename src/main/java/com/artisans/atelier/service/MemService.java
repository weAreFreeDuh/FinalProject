package com.artisans.atelier.service;

import com.artisans.atelier.dao.MemDAO;
import com.artisans.atelier.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemService {
    private ModelAndView mav;

    private final PasswordEncoder pwEnc;

    private final MemDAO mdao;

    private final JavaMailSender mailSender;

    private final HttpSession session;

    FISH fish = new FISH();


    // 아이디 중복 체크
    public String MEM002(String memId) {
        String result = mdao.MEM002(memId);
        String msg = "NO";
        if (result == null) {
            msg = "OK";
        }
        return msg;
    }

    // 닉네임 중복 체크
    public String MEM003(String memName) {
        String result = mdao.MEM003(memName);
        String msg = "NO";
        if (result == null) {
            msg = "OK";
        }
        return msg;
    }

    // 회원 가입
    public ModelAndView MEM004(MemberDTO member) {
        mav = new ModelAndView();

        try {
            // 비밀번호 암호화
            // System.out.println(member.getMemPw());
            member.setMemPw(pwEnc.encode(member.getMemPw()));
            //System.out.println(member.getMemPw());

            // DB에 저장
            mdao.MEM004(member);

            // 인덱스 코드 불러오기
            int memCode = mdao.takeMemCode(member.getMemId());

            // 이메일 인증 보내기
            String str = "<h2>Athelier 회원 가입을 축하합니다.</h2>" +
                    "<p>다양한 창작 활동을 기원하며 아래 링크를 통해 인증을 해주세요</p>" +
                    "<p><a href='http://121.65.47.77:1023/MEM005?user=" + memCode + "'>로그인 하기</a></p>";

            MimeMessage mail = mailSender.createMimeMessage();

            mail.setSubject("Athelier 인증메일");
            mail.setText(str, "UTF-8", "html");
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemId()));
            mailSender.send(mail);

            int alert =1;
            mav.addObject("alertMessage",alert);
            mav.setViewName("MemJoin&Login");

        } catch (Exception e) {
            System.out.println("회원 가입 에러 발생");

            mav.setViewName("MemJoin&Login");
        }

        return mav;
    }

    // 이메일 인증
    public ModelAndView MEM005(int memCode) {
        mav = new ModelAndView();

        try {
            // 인증 상태 점검 미인증 -> 인증
            // 해당 회원 전체 내용을 가져오도록 로그인 메소드 사용
            MemberDTO member = mdao.MEM006(memCode);

            if (member.getMemState().equals("미인증") || member.getMemState().equals("휴면")) {
                // 인증상태 변경
                mdao.MEM005(memCode);

                // 로그인상태로 만들기 (비밀번호 숨기기)
                LoginDTO login = mdao.login(memCode);
                session.setAttribute("login", login);
            }
        } catch (Exception e) {
            System.out.println("이메일 인증 오류");
        }

        // 인덱스로 보내기
        mav.setViewName("index");

        return mav;

    }

    // 로그인
    public ModelAndView MEM006(MemberDTO member) {
        mav = new ModelAndView();

        // 비밀번호 일치 확인
        String Pw = mdao.checkPw(member.getMemId());
        if (pwEnc.matches(member.getMemPw(), Pw)) {
            // 회원 코드 가져오기
            int memCode = mdao.takeMemCode(member.getMemId());

            // 회원 코드로 세션에 들어갈 값들 가져오기(회원과 권한이 조인된 뷰를 조회)
            LoginDTO login = mdao.login(memCode);
            if (login.getMemState().equals("인증") || login.getMemState().equals("1차경고") || login.getMemState().equals("2차경고")) {

                session.setAttribute("login", login);

                // 로그인 기록 남김
                LHDTO log = new LHDTO();
                log.setLHMemCode(memCode);
                log.setLHIp(member.getMemIp());
                mdao.insertLH(log);

                // 인덱스로 이동
                mav.setViewName("index");
            }else if (login.getMemState().equals("휴면")) {
                try {
                    // 이메일 인증 보내기
                    String str = "<h2>Athelier 돌아오신걸 축하합니다.</h2>" +
                            "<p>다양한 창작 활동을 기원하며 아래 링크를 통해 인증을 해주세요</p>" +
                            "<p><a href='http://121.65.47.77:1023/MEM005?user=" + memCode + "'>로그인 하기</a></p>";

                    MimeMessage mail = mailSender.createMimeMessage();

                    mail.setSubject("Athelier 인증메일");
                    mail.setText(str, "UTF-8", "html");
                    mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemId()));
                    mailSender.send(mail);

                    int alert =1;
                    mav.addObject("alertMessage",alert);
                    mav.setViewName("MemJoin&Login");

                }catch (Exception e){
                    mav.setViewName("redirect:/MEM001");
                }

            } else {
                mav.setViewName("redirect:/MEM001");
            }
        } else {
            mav.setViewName("redirect:/MEM001");
        }

        return mav;
    }

    // 신고처리
    public ModelAndView MEM009(ReportDTO report) {
        mav = new ModelAndView();
        // 신고 사진 저장 성공 유무
        boolean imgSave = true;
        // 저장된 사진 이름
        String reportImgName = null;

        // 신고 사진 저장
        MultipartFile reportImg = report.getRepFile();
        if (!reportImg.isEmpty()) {
            // 문자는 폴더이름, 변수는 멀티파트타입으로 받아온파일.
            try {
                reportImgName = fish.fileSave("fish/img/reportImg", reportImg);
                report.setRepFileName(reportImgName);
            } catch (Exception e) {
                System.out.println("신고 처리 사진 저장 오류");
                imgSave = false;

            }
        } else {
            // 입력받은 사진이 없을시 디폴트 이미지
            report.setRepFileName("default.jpg");
        }

        // 사진 저장에서 예외처리가 발생하지 않을시
        if (imgSave) {
            try {
                mdao.MEM009(report);
                mav.setViewName("index");
            } catch (Exception e) {
                e.printStackTrace();

                // DB업데이트 오류 발생
                System.out.println("신고처리 DB업데이트 오류 발생");

                // DB오류이므로 이미 저장된 사진은 삭제처리 한다. 단, 이미지를 받았을 경우에만
                if (reportImgName != null) {
                    fish.fileDelete("img/reportImg", reportImgName);
                }
                mav.setViewName("index");
            }
        } else {
            // 이미지 예외 발생으로 인해 DB접근 없이 신고 폼으로 돌아간다.
            mav.setViewName("index");
        }

        return mav;
    }

    // 신고목록 페이지
    public List<Object> MEM010(int page, int limit, boolean all) {
        mav = new ModelAndView();
        List<Object> result = new ArrayList<>();
        String sqlData = "";
        if (!all) {
            sqlData = "WHERE REPSTATE = '처리 전' ";
        } else {
            sqlData = "";
        }
        // 총 갯수
        int count = mdao.reportCount(sqlData);

        // 페이징 처리 (비슷한 기능의 맵퍼를 2번 만드는것보다 sql문을 보내줘서 실행시키는게 낫다고 판단
        PageDTO paging = fish.paging(page, limit, count);
        if (!all) { // 처리전 인것만 가져오기
            paging.setSql("SELECT * FROM REPORTLIST2 WHERE RN BETWEEN #{startRow} AND #{endRow}");
        } else { // 전체 가져오기
            paging.setSql("SELECT * FROM REPORTLIST WHERE RN BETWEEN #{startRow} AND #{endRow}");
        }

        // 뷰에서 페이징에 따라서 가져오기
        List<ReportDTO> ReportList = mdao.MEM010(paging);

        // sql 정보 빼기
        paging.setSql(null);

        result.add(paging);
        result.add(ReportList);

        return result;
    }

    // 신고처리 상태 변경
    public String ReportState(int repCode) {
        String result = "상태 변경 성공";
        try {
            mdao.ReportState(repCode);
        } catch (Exception e) {
            result = "다시 시도해 주세요";
        }
        return result;
    }

    // 회원목록 보기
    public List<Object> MEM011(int page, int limit, String search, String devide, String category) {

        // 사용자 오류 체크용
        boolean error = false;

        // where에 들어갈 문자
        String Where = " MEMAUTCODE = 0 ";

        // 분류
        switch (devide) {
            case "인증":
                Where += "AND MEMSTATE = '인증' ";

                break;
            case "미인증":
                Where += "AND MEMSTATE = '미인증' ";

                break;
            case "블랙":
                Where += "AND MEMSTATE = '블랙' ";

                break;
            case "휴면":
                Where += "AND MEMSTATE = '휴면' ";

                break;
            case "1차경고":
                Where += "AND MEMSTATE = '1차경고' ";

                break;
            case "2차경고":
                Where += "AND MEMSTATE = '2차경고' ";

                break;
            default:
                error = true;
                break;
        }

        // 검색
        if (!search.equals("")) {
            //System.out.println("검색 있음");
            Where += "AND " + category + " LIKE '%" + search + "%'";
        }


        String sql = "SELECT COUNT(*) FROM MEMBER ";
        sql += "WHERE " + Where;
        // 검색이나 분류에 따른 전체 갯수 가져오기
        int count = mdao.everyCount(sql);

        // System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, limit, count);

        // 뷰에서 검색할 조건에 따라 페이징처리된 상태로 가져오기
        paging.setSql("SELECT * FROM (SELECT MEMBER.*, ROW_NUMBER() OVER(ORDER BY MEMCODE DESC) AS RN FROM MEMBER WHERE " + Where + ") WHERE RN BETWEEN #{startRow} AND #{endRow}");
        List<MemberDTO> MemberList = mdao.MEM011(paging);
        //System.out.println(MemberList);

        // sql 정보 빼기
        paging.setSql(null);

        // 정보 넣기
        List<Object> result = new ArrayList<>();
        result.add(paging);
        result.add(MemberList);
        return result;
    }

    // 직원으로 넣기
    public String MemberState(int memCode) {
        String result = "직원으로 변경 성공";
        try {
            mdao.MemberState(memCode);
        } catch (Exception e) {
            result = "직원으로 변경 실패";
        }
        return result;
    }

    // 회원 상태변경
    public String MemberWarning(int memCode) {
        String result = "님 회원상태 : ";
        String state = "";

        // 사용자 입력에러(예상치 못한) 방지
        boolean error = true;

        try {
            MemberDTO member = mdao.MEM006(memCode);
            switch (member.getMemState()) {
                case "인증":
                    state = "1차경고";
                    break;
                case "1차경고":
                    state = "2차경고";
                    break;
                case "2차경고":
                    state = "블랙";
                    break;
                case "블랙":
                    state = "인증";
                    break;
                default:
                    error = false;
                    break;
            }

            // 상태변경
            member.setMemState(state);
            if (error) {
                mdao.MemberWarning(member);


                // 상태변경 이메일 보내기
                String str = "<h1>Athelier</h1>" +
                        "<p>회원 상태가 " + state + "로 변경 되었습니다.</p>" +
                        "<p>이의제기는 페이지내에 채팅으로 받고 있습니다.</p>";

                MimeMessage mail = mailSender.createMimeMessage();

                mail.setSubject("Athelier 회원상태변경");
                mail.setText(str, "UTF-8", "html");
                mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemId()));
                mailSender.send(mail);


                result += member.getMemName() + result + state;
            } else {
                result = "변경 불가능한 회원상태";
            }

        } catch (Exception e) {
            result = "경고 적용 실패";
        }
        return result;
    }

    // 비밀번호 찾고 싶어요
    public String MEM020(String memId) {
        String result = "회원가입하지 않은 아이디 입니다.";
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE MEMID ='" + memId + "'";
        int count = mdao.everyCount(sql);
        if (count == 1) {
            //System.out.println(count);
            String pw = fish.singleUUID();

            try {
                // 비밀번호 재설정
                MemberDTO member = new MemberDTO();
                member.setMemPw(pwEnc.encode(pw));
                member.setMemId(memId);
                mdao.MEM020(member);

                // 비밀번호 변경 이메일 보내기
                String str = "<h1>Athelier 비밀번호 변경</h1>" +
                        "<h2>회원님의 비밀번호가 [" + pw + "]로 변경 되었습니다.</h2>" +
                        "<p>본인이 아니라면 문의해 주세요.</p>";

                MimeMessage mail = mailSender.createMimeMessage();

                mail.setSubject("Athelier 비밀번호 변경");
                mail.setText(str, "UTF-8", "html");
                mail.addRecipient(Message.RecipientType.TO, new InternetAddress(memId));
                mailSender.send(mail);

                result = "이메일로 변경된 비밀번호가 발송되었습니다.";
            } catch (Exception e) {
                result = "잠시후 다시 시도해 주세요";
            }

        }


        return result;
    }

    // 회원 검색
    public List<MemberDTO> MEM016(String devide, String category, String searchName) {
        if (devide.equals("all")) {
            devide = "";
        } else {
            devide = "AND MEMSTATE = '" + devide + "'";
        }
        //System.out.println(devide);
        String sql = "SELECT MEMID, MEMNAME, MEMSTATE FROM MEMBER WHERE MEMAUTCODE = 0 AND " + category + " like '%" + searchName + "%' " + devide;
        List<MemberDTO> result = mdao.MEM016(sql);
        return result;
    }

    // 이메일 보내기
    public String MEM017(Boolean sendRange, String sendRangeName, String title, String content, ArrayList idList) {
        String result = "메일이 보내졌습니다.";
        //System.out.println(sendRange);
        if (sendRange) {
            String sql = "SELECT MEMID FROM MEMBER WHERE MEMAUTCODE = 0";
            if (!sendRangeName.equals("all")) {
                sql += " AND MEMSTATE = '" + sendRangeName + "'";
            }
            idList = new ArrayList<>();
            List<MemberDTO> member = mdao.MEM016(sql);
            for (MemberDTO m : member) {
                //System.out.println(m);
                idList.add(m.getMemId());
            }
        }


        // 이메일 보내기
        try {
            // 비밀번호 변경 이메일 보내기
            String str = "<h1>" + title + "</h1>" +
                    "<h3>" + content + "</h3>" +
                    "<p>좋은 하루 되세요</p>";

            MimeMessage mail = mailSender.createMimeMessage();

            mail.setSubject("Athelier 공지메일");
            mail.setText(str, "UTF-8", "html");

            for (Object memId : idList) {
                mail.addRecipient(Message.RecipientType.TO, new InternetAddress(memId.toString()));
            }

            mailSender.send(mail);
        } catch (Exception e) {
            System.out.println("메일 보내기 실패");
            result = "메일 전송 실패";
        }


        return result;
    }

    // 직원 권한들 가져오기
    public List<AuthorityDTO> MEM023() {
        List<AuthorityDTO> result = mdao.MEM023();
        return result;
    }

    // 직원 목록(검색)
    public List<Object> EmployeeSelect(Boolean autAll, int autCode, String category, String search, int page, int limit) {
        List<Object> result = new ArrayList<>();

        // 검색 조건에 쓰이는 sql
        String where = "";

        if (!autAll) {
            where += " WHERE MEMAUTCODE = " + autCode;
        } else {
            where += "WHERE MEMAUTCODE != 0";
        }
        where += " AND " + category + " like '%" + search + "%' ";

        String sql = "SELECT Count(*) FROM MEMBER " + where;
        // 검색이나 분류에 따른 전체 갯수 가져오기
        int count = mdao.everyCount(sql);

        // System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, limit, count);

        // 뷰에서 검색할 조건에 따라 페이징처리된 상태로 가져오기
        paging.setSql("SELECT MEMCODE, MEMID, MEMNAME, AUTNAME FROM (SELECT MEMBER.*, ROW_NUMBER() OVER(ORDER BY MEMCODE DESC) AS RN FROM MEMBER " + where + "), authority WHERE AUTCODE = MEMAUTCODE AND RN BETWEEN #{startRow} AND #{endRow} ");
        List<MemberDTO> MemberList = mdao.MEM011(paging);
        //System.out.println(MemberList);

        paging.setSql(null);
        result.add(paging);
        result.add(MemberList);

        return result;
    }

    // 직원 상세보기
    public MemberDTO MEM022(int memCode) {
        MemberDTO result = mdao.MEM006(memCode);
        result.setMemPw(null);
        return result;
    }

    // 직원 권한수정
    public String MEM025(int memCode, int autCode) {
        String result = "수정 실패";
        MemberDTO member = new MemberDTO();
        member.setMemCode(memCode);
        member.setMemAutCode(autCode);
        try {
            mdao.MEM025(member);
            String sql = "SELECT AUTNAME FROM AUTHORITY WHERE AUTCODE = " + autCode;
            String autName = mdao.StringReturn(sql);
            result = autName;
        } catch (Exception e) {
            System.out.println("직원권한수정 오류");
        }
        return result;
    }

    // 직원해고
    public String MEM026(int memCode) {
        String result = "OK";
        try {
            mdao.MEM026(memCode);
        } catch (Exception e) {
            result = "NO";
        }
        return result;
    }

    // 권한 리스트
    public List<Object> autResearch(boolean autAll, String category, String search, int page, int limit) {
        List<Object> result = new ArrayList<>();

        // 검색 조건 작성
        String where = " WHERE AUTCODE != 0 AND ";
        if (!autAll) {
            where += category + " = 1  AND ";
        }
        where += "AUTNAME LIKE '%" + search + "%'";

        String sql = "SELECT Count(*) FROM AUTHORITY " + where;
        // 검색이나 분류에 따른 전체 갯수 가져오기
        int count = mdao.everyCount(sql);

        // 페이징 처리
        PageDTO paging = fish.paging(page, limit, count);

        // 뷰에서 검색할 조건에 따라 페이징처리된 상태로 가져오기
        paging.setSql("SELECT * FROM (SELECT AUTHORITY.*, ROW_NUMBER() OVER(ORDER BY AUTCODE DESC) AS RN FROM AUTHORITY " + where + ") WHERE RN BETWEEN #{startRow} AND #{endRow} ");
        List<AuthorityDTO> AuthorityList = mdao.autResearch(paging);
        //System.out.println(MemberList);

        paging.setSql(null);
        result.add(paging);
        result.add(AuthorityList);

        return result;
    }

    // 권한 수정
    public String autUpdate(String category, int data, int autCode) {
        String result = "OK";

        try {
            String sql = "UPDATE AUTHORITY SET " + category + " = " + data + " WHERE AUTCODE = " + autCode;
            mdao.sqlUpdate(sql);
        } catch (Exception e) {
            result = "NO";
            //e.printStackTrace();
        }

        return result;
    }


    // 권한 생성
    public String MEM024(String name, int give, int secu, int acco, int chat, int mail, int cate) {
        String result = "OK";
        try{
            AuthorityDTO aut = new AuthorityDTO();
            aut.setAutName(name);
            aut.setAutGive(give);
            aut.setAutSecu(secu);
            aut.setAutAcco(acco);
            aut.setAutAChat(chat);
            aut.setAutAMail(mail);
            aut.setAutCategory(cate);
            mdao.MEM024(aut);
        }catch (Exception e){
            result = "NO";
        }
        return result;
    }

    // 권한 삭제
    public String autDelete(int autCode) {
        String result ="OK";

        switch (autCode){
            case 0:
                result="유저 권한은 삭제할 수 없습니다.";
                break;
            case 1:
                result="전체권한은 삭제 할 수 없습니다.";
                break;
            case 2:
                result="신입권한은 삭제 할 수 없습니다.";
                break;
            default:
                try {
                    mdao.autDelete(autCode);
                }catch (Exception e){
                    result = "NO";
                    e.printStackTrace();
                }
                break;
        }


        return result;
    }

    // 프로필 조회
    public ModelAndView PRO001(int memCode) {
        mav = new ModelAndView();
        ProFileDTO profile = mdao.PRO001(memCode);

        // 팔로잉 팔로워 관리
        int follow = Integer.parseInt(profile.getFollow());
        int follower = Integer.parseInt(profile.getFollower());

        if(follow>1000000){
            String output = String.valueOf(follow/1000000);
            if(((follow%1000000)/100000)>1){
                output+="."+String.valueOf((follow%1000000)/100000);
            }
            output +="M";
            profile.setFollow(output);
        } else if (follow>1000) {
            String output = String.valueOf(follow/1000);
            if(((follow%1000)/100)>1){
                output+="."+String.valueOf((follow%1000)/100);
            }
            output +="K";
            profile.setFollow(output);
        }

        if(follower>1000000){
            String output = String.valueOf(follower/1000000);
            if(((follower%1000000)/100000)>1){
                output+="."+String.valueOf((follower%1000000)/100000);
            }
            output +="M";
            profile.setFollower(output);
        } else if (follower>1000) {
            String output = String.valueOf(follower/1000);
            if(((follower%1000)/100)>1){
                output+="."+String.valueOf((follower%1000)/100);
            }
            output +="K";
            profile.setFollower(output);
        }
        //System.out.println(profile);
        mav.addObject("profile",profile);
        mav.setViewName("Pro_Profile");
        return mav;
    }

    // 프로필에서 워크스페이스 조회
    public List<Object> ProfileWorkSpace(int memCode,int page) {

        // 프로필에 따른 워크스페이스 전체 갯수 가져오기
        int count = mdao.everyCount("SELECT COUNT(*) FROM FINALWORKSPACELIST WHERE WORKMEMCODE = "+memCode);

        //System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, 9, count);

        // sql 셋팅
        paging.setSql("SELECT * FROM (SELECT FINALWORKSPACELIST.*, ROW_NUMBER() OVER(ORDER BY WORKCODE DESC) AS RN FROM FINALWORKSPACELIST WHERE WORKMEMCODE = "+memCode+") WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 워크스페이스 가져오기
        List<MemWorkSpaceDTO> workList =  mdao.ProfileWorkSpace(paging);

        // 현재 페이지와 페이징처리된 워크스페이스 리스트보내기
        List<Object> result = new ArrayList<>();

        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // sql문 삭제
        paging.setSql(null);

        // 리스트에 페이징과 워크 리스트 첨부
        result.add(paging);
        result.add(workList);
        return result;
    }

    // 프로필에서 게시글 가져오기
    public List<Object> ProfileBoard(int memCode, int page) {

        // 프로필에 따른 게시글 전체 갯수 가져오기
        int count = mdao.everyCount("SELECT COUNT(*) FROM FINALBOARDLIST WHERE BOAMEMCODE = "+memCode);

        //System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, 12, count);

        // sql 셋팅
        paging.setSql("SELECT * FROM (SELECT FINALBOARDLIST.*, ROW_NUMBER() OVER(ORDER BY BOACODE DESC) AS RN FROM FINALBOARDLIST WHERE BOAMEMCODE = "+memCode+") WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 게시물 가져오기
        List<MemBoardDTO> boardList =  mdao.ProfileBoard(paging);

        // 현재 페이지와 페이징처리된 게시글 리스트보내기
        List<Object> result = new ArrayList<>();

        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // sql문 삭제
        paging.setSql(null);

        // 리스트에 페이징과 워크 리스트 첨부
        result.add(paging);
        result.add(boardList);
        return result;
    }

    // 프로필 팔로워 가져오기
    public List<Object> ProfileFollower(int memCode, int page) {

        // 프로필에 따른 팔로워 전체 갯수 가져오기
        int count = mdao.everyCount("SELECT COUNT(*) FROM FOLLOW WHERE FOLLOWER = "+memCode);

        //System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, 50, count);

        // sql 셋팅
        paging.setSql("SELECT * FROM(SELECT fo1.*,ROW_NUMBER() OVER(ORDER BY MEMNAME ASC) AS RN FROM (SELECT MEMNAME,MEMPROFILENAME,MEMCODE FROM FOLLOW INNER JOIN MEMBER ON follow.following = MEMBER.MEMCODE WHERE FOLLOWER = "+memCode+")fo1) WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 팔로워 가져오기
        List<MemberDTO> memberList =  mdao.ProfileFollow(paging);

        // 현재 페이지와 페이징처리된 회원 리스트보내기
        List<Object> result = new ArrayList<>();

        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // sql문 삭제
        paging.setSql(null);

        // 리스트에 페이징과 회원리스트 첨부
        result.add(paging);
        result.add(memberList);
        return result;
    }

    // 프로필에서 팔로잉 가져오기
    public List<Object> ProfileFollowing(int memCode, int page) {
        // 프로필에 따른 팔로잉 전체 갯수 가져오기
        int count = mdao.everyCount("SELECT COUNT(*) FROM FOLLOW WHERE FOLLOWING = "+memCode);

        //System.out.println(count);

        // 페이징 처리
        PageDTO paging = fish.paging(page, 50, count);

        // sql 셋팅
        paging.setSql("SELECT * FROM(SELECT fo1.*,ROW_NUMBER() OVER(ORDER BY MEMNAME ASC) AS RN FROM (SELECT MEMNAME,MEMPROFILENAME,MEMCODE FROM FOLLOW INNER JOIN MEMBER ON follow.follower = MEMBER.MEMCODE WHERE FOLLOWING = "+memCode+")fo1) WHERE RN BETWEEN #{startRow} AND #{endRow}");

        // 팔로잉 가져오기
        List<MemberDTO> memberList =  mdao.ProfileFollow(paging);

        // 현재 페이지와 페이징처리된 회원 리스트보내기
        List<Object> result = new ArrayList<>();

        // 다음페이지로 셋팅
        paging.setPage(page+1);

        // sql문 삭제
        paging.setSql(null);

        // 리스트에 페이징과 회원리스트 첨부
        result.add(paging);
        result.add(memberList);
        return result;
    }

    // 프로필사진, 배경사진, 소개 수정
    public ModelAndView proUpdate(MemberDTO member, String check) {
        mav = new ModelAndView();
        //수정할시 반복되는 sql문장 미리 설정
        String sql =" UPDATE MEMBER SET ";

        // 프로필 사진 수정
        if(check.equals("profileImg")){
            // 문자는 폴더이름, 변수는 멀티파트타입으로 받아온파일.
            try{
                // 기존 사진 이름 메모리에 저장
                String deleteImgName = member.getMemProfileName();
                // 저장
                String profileName = fish.fileSave("fish/img/memProfileImg", member.getMemProfile());
                //새로저장된 사진 이름 넣기
                member.setMemProfileName(profileName);

                // 회원수정에 대해서 맵퍼 통일을 위해 sql문을 보내준다.
                // 프로필이미지 이름 수정
                sql+="MEMPROFILENAME = '"+profileName+"' WHERE MEMCODE = "+member.getMemCode();
                mdao.sqlUpdate(sql);

                session.invalidate();
                // 회원 코드로 세션에 들어갈 값들 가져오기(회원과 권한이 조인된 뷰를 조회)
                LoginDTO login = mdao.login(member.getMemCode());
                session.setAttribute("login",login);

                // 기존사진 삭제(디폴트 이미지가 아닐시)
                if(!deleteImgName.equals("default.png")){
                    fish.fileDelete("fish/img/memProfileImg",deleteImgName);}

            }catch (Exception e){
                System.out.println("사진 수정 실패");
                e.printStackTrace();
            }

            // 배경 사진 수정
        }else if(check.equals("profileBackImg")){
            // 문자는 폴더이름, 변수는 멀티파트타입으로 받아온파일.
            try{
                // 기존 사진 이름 메모리에 저장
                String deleteBackImgName = member.getMemBackGround();
                // 저장
                String profileBackName = fish.fileSave("fish/img/profileBackground", member.getMemProfile());
                //새로저장된 사진 이름 넣기
                member.setMemBackGround(profileBackName);

                // 회원수정에 대해서 맵퍼 통일을 위해 sql문을 보내준다.
                // 프로필배경이미지 이름 수정
                sql+="MEMBACKGROUND = '"+profileBackName+"' WHERE MEMCODE = "+member.getMemCode();
                mdao.sqlUpdate(sql);

                // 기존사진 삭제(디폴트 이미지가 아닐시)
                if(!deleteBackImgName.equals("default.jpg")){
                    fish.fileDelete("fish/img/profileBackground",deleteBackImgName);}

            }catch (Exception e){
                System.out.println("사진 수정 실패");
                e.printStackTrace();}
        } else if (check.equals("intro")) {
            sql+="MEMINTRO = '"+member.getMemIntro()+"' WHERE MEMCODE = "+member.getMemCode();
            try {
                mdao.sqlUpdate(sql);
            }catch (Exception e){
                System.out.println("인트로 수정 실패");
                e.printStackTrace();
            }

        }


        mav.setViewName("redirect:/PRO001?user="+member.getMemCode());
        return mav;
    }

    // 기존에 팔로우 했는지 안헀는지 구해오기
    public int followOrNot(int youCode, int myCode) {
        return mdao.everyCount("SELECT COUNT(*) FROM FOLLOW WHERE FOLLOWING = "+myCode+" AND FOLLOWER = "+youCode);
    }

    // 팔로우 또는 언팔로우
    public ModelAndView follow(int youCode, int myCode, boolean fou) {
        mav = new ModelAndView();
        String sql;
        // 팔로우
        if(fou){
            sql = "INSERT INTO FOLLOW VALUES("+myCode+","+youCode+")";
            try {
                mdao.everyInsert(sql);
            }catch (Exception e){
                System.out.println("팔로우 실패");
                e.printStackTrace();
            }

            // 언팔로우
        }else{
            sql = "DELETE FOLLOW WHERE FOLLOWING = "+myCode+" AND FOLLOWER = "+youCode;
            try {
                mdao.everyDelete(sql);
            }catch (Exception e){
                System.out.println("언팔로우 실패");
                e.printStackTrace();
            }

        }

        mav.setViewName("redirect:/PRO001?user="+youCode);
        return mav;
    }

    // 회원 상세 수정
    public ModelAndView PRO009(int memCode) {
        mav = new ModelAndView();
        // 회원 정보 가져오기
        MemberDTO member = mdao.MEM006(memCode);

        // 주소 3개로 찢어서 넣기
        if(!member.getMemAddr().equals("미입력")){
            member.setMemAddr1(member.getMemAddr().substring(1,member.getMemAddr().indexOf(")")));
            member.setMemAddr2(member.getMemAddr().substring(member.getMemAddr().indexOf(")")+1,member.getMemAddr().indexOf(",")));
            member.setMemAddr3(member.getMemAddr().substring(member.getMemAddr().indexOf(",")+1));

        }



        // 회원정보 모델에 추가해주기
        mav.addObject("member",member);
        mav.setViewName("Mem_View");
        return mav;
    }

    // 프로필 수정
    public ModelAndView MEM019(MemberDTO member, String year, String mon, String day) {
        mav= new ModelAndView();

        // 주소 합치기
        member.setMemAddr("("+member.getMemAddr1()+")"+member.getMemAddr2()+","+member.getMemAddr3());

        // 주소가 미입력인데 변경사항이 없을시 미입력 그대로 저장
        if(member.getMemAddr().equals("(),")){
            member.setMemAddr("미입력");
        }

        // 생년월일 합치기
        member.setMemBirth(year+mon+day);

        // 전화번호 -빼고 저장하기
        member.setMemPhone(fish.phone(member.getMemPhone()));

        // 합친것들 확인
        //System.out.println(member.getMemAddr());
        //System.out.println(member.getMemBirth());
        //System.out.println(member.getMemPhone());

        // 수정실행
        try {
            mdao.MEM019(member);
        }catch (Exception e){
            System.out.println("회원수정 실패");
            e.printStackTrace();
        }

        mav.setViewName("redirect:/PRO009?user="+member.getMemCode());
        return mav;
    }

    public String PRO010(int memCode, String oldPw, String newPw, String newPwCheck, String memId) {
        String result="";
        String oldPwCheck = mdao.checkPw(memId);
        if (pwEnc.matches(oldPw, oldPwCheck)){
            if(newPw.equals(newPwCheck)){
                // 암호화 된것으로 비밀번호 재정의
                newPw=pwEnc.encode(newPwCheck);
                // sql 생성
                String sql = "UPDATE MEMBER SET MEMPW = '"+newPw+"' WHERE MEMCODE = "+memCode;
                try {
                    mdao.sqlUpdate(sql);
                    result="비밀번호가 수정되었습니다.";
                }catch (Exception e){
                    result="새로운 비밀번호 등록에 문제가 있습니다.";
                    e.printStackTrace();
                }
            }else{
                result = "새로운 비밀번호가 확인과 일치하지 않습니다.";
            }
        }else{
            result="기존 비밀번호가 일치하지 않습니다.";
        }

        return result;
    }

    // 로그인 기록 가져오기
    public List<Object> PRO013(int page, int memCode) {
        // 해당 회원의 로그인 기록 전체 갯수 구해오기
        int count = mdao.everyCount("SELECT COUNT(*) FROM LOGINHISTORY WHERE LHMEMCODE = "+memCode);

        // 페이징 처리
        PageDTO paging = fish.paging(page, 10, count);
        paging.setSql("SELECT * FROM (SELECT LOGINHISTORY.*,ROW_NUMBER() OVER(ORDER BY LHDATE DESC) AS RN FROM LOGINHISTORY WHERE LHMEMCODE = "+memCode+" ) WHERE RN BETWEEN #{startRow} AND #{endRow}");

        List<LHDTO> LHList = mdao.PRO013(paging);
        List<Object> result = new ArrayList<>();
        result.add(paging);
        result.add(LHList);

        return result;
    }


    //////////////////////////////////////////////////////////////////
    // 휴면계정 전환
    public ModelAndView MEM014(int memCode) {
        mav = new ModelAndView();
        MemberDTO member = mdao.MEM006(memCode);

        try{
            mdao.sqlUpdate("UPDATE MEMBER SET MEMSTATE = '휴면' WHERE MEMCODE = "+memCode);

            // 상태변경 이메일 보내기
            String str = "<h1>Athelier</h1>" +
                    "<p>회원 상태가  휴면으로 변경 되었습니다.</p>" +
                    "<p>다시 로그인시 인증메일이 발송 되오니 참고 바랍니다..</p>";

            MimeMessage mail = mailSender.createMimeMessage();

            mail.setSubject("Athelier 회원상태변경");
            mail.setText(str, "UTF-8", "html");
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemId()));
            mailSender.send(mail);
            // 로그아웃 시키기
            mav.setViewName("redirect:/MEM007");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("휴면계정 전환 오류");
            mav.setViewName("redirect:/PRO009?user="+memCode);
        }
        return mav;
    }
}
