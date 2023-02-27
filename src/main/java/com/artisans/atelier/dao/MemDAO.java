package com.artisans.atelier.dao;

import com.artisans.atelier.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemDAO {
    // 회원가입 및 로그인
    String MEM002(String memId);

    String MEM003(String memName);

    void MEM004(MemberDTO member);

    int takeMemCode(String memId);

    void MEM005(int memCode);

    MemberDTO MEM006(int memCode);


    String checkPw(String memId);

    LoginDTO login(int memCode);

    void insertLH(LHDTO log);


    // 신고처리
    void MEM009(ReportDTO report);

    int reportCount(String sqlData);

    List<ReportDTO> MEM010(PageDTO paging);

    void ReportState(int repCode);

    // DB 카운트 전용 맵퍼
    int everyCount(String sql);

    // 회원 목록 및 목록 검색처리
    List<MemberDTO> MEM011(PageDTO paging);

    // 직원으로 변경
    void MemberState(int memCode);

    // 회원 경고
    void MemberWarning(MemberDTO member);

    // 공지메일 회원 검색
    List<MemberDTO> MEM016(String sql);

    // 비밀번호 재설정
    void MEM020(MemberDTO member);

    // 권한 조회
    List<AuthorityDTO> MEM023();

    // 직원 권한 수정
    void MEM025(MemberDTO member);

    // 결과값으로 문자열을 리턴해주는 맵퍼
    String StringReturn(String sql);

    // 직원 해고
    void MEM026(int memCode);

    // 권한조회 -> 직책 리스트
    List<AuthorityDTO> autResearch(PageDTO paging);

    // sql로 작성된 업데이트문 실행
    void sqlUpdate(String sql);

    // 권한 생성
    void MEM024(AuthorityDTO aut);

    // 권한 삭제
    void autDelete(int autCode);

    /////////////////////////////////////////////////
    // 프로필

    // 프로필 정보 가져오기
    ProFileDTO PRO001(int memCode);

    // 프로필에서 워크스페이스 조회
    List<MemWorkSpaceDTO> ProfileWorkSpace(PageDTO paging);

    // 프로필에서 게시물 조회
    List<MemBoardDTO> ProfileBoard(PageDTO paging);

    // 프로필에서 팔로잉, 팔로워 조회
    List<MemberDTO> ProfileFollow(PageDTO paging);

    // DTO없는 항목 insert문
    void everyInsert(String sql);

    // DTO없는 항목 삭제문
    void everyDelete(String sql);

    // 회원수정
    void MEM019(MemberDTO member);

    // 로그인 기록 가져오기
    List<LHDTO> PRO013(PageDTO paging);
}
