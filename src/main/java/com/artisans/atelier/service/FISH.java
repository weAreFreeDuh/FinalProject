package com.artisans.atelier.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import com.artisans.atelier.dto.PageDTO;
import org.springframework.web.multipart.MultipartFile;

public class FISH {

    // 파일저장 메소드
    // 저장폴더, 저장파일을 받아온다.
    public String fileSave(String folder, MultipartFile profile) throws  IOException {


        // 파일 이름 불러오기
        String originalFileName = profile.getOriginalFilename();

        // 난수(UUID) 생성하기
        String uuid = makeUUID();


        // 업로드할 파일이름 생성하기
        String profileName = uuid+"_"+originalFileName;

        // 저장 경로 만들기
        String folderPath = "src/main/resources/static/"+folder;

        //  파일 저장 위치 설정(상대경로)
        Path path = Paths.get(System.getProperty("user.dir"), folderPath);
        String savePath = path+"/"+profileName;

        // 파일선택시 MemberDTO member객체의 profileName필드에 업로드파일 이름 저장
        profile.transferTo(new File(savePath));

        // 저장된 파일 이름 리턴
        return profileName;
    }

    // 파일 삭제 메소드
    // 저장된 폴더, 삭제할 파일 이름을 받아온다.
    public void fileDelete(String folder, String fileName) {
        String folderPath = "src/main/resources/static/"+folder;
        Path path = Paths.get(System.getProperty("user.dir"), folderPath);

        String deletePath=path+"/"+fileName;
        File deleteFile = new File(deletePath);

        // 삭제할파일이 존재하다면, 삭제하기
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    //날짜 티켓식 uuid 생성기
    public String makeUUID(){
        // uuid 생성기
        String uuid = UUID.randomUUID().toString().substring(0,8);
        String date = LocalDate.now().toString();
        date =date.substring(0, 7)+date.substring(8);
        uuid = date+"-"+uuid.substring(0, 4)+"-"+uuid.substring(4, 8);
        return uuid;
    }

    //단순8자리 uuid(비밀번호 초기화등에 사용)
    public String singleUUID(){
        // 단순 uuid 생성기
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return uuid;
    }


    // 나이구하기
    public int Age(String age){
        // 유저 나이 구해오기
        int age2 = Integer.parseInt(age.substring(0, age.indexOf("-")));

        // 올해 구해오기
        int date = Integer.parseInt(LocalDate.now().toString().substring(0,4));

        // 나이구해오기
        age2 = date-age2+1;
        return age2;
    }

    // 이번달 생일인지 구하기
    public boolean Birth(String age){
        // 유저 생일 월 구하기
        age = age.substring(age.indexOf("-")+1, age.indexOf("-")+3);

        // 이번달
        String date = LocalDate.now().toString().substring(5,7);

        // 비교해서 return값을 true || false 로 주기
        if(age.equals(date)) {
            return true;
        }else {
            return false;
        }
    }

    // 전화번호에서 - 빼기
    public String phone(String Number){
        return Number.replaceAll("-", "");
    }


    // txt 파일 읽기
    public String txtRead(String folder, String name) throws IOException {
        String result = "";

        // 텍스트 파일의 폴더와 이름을 받아온다.
        String folderPath = "src/main/resources/static/text/" + folder;
        Path path = Paths.get(System.getProperty("user.dir"), folderPath);

        String textPath = path + "/" + name + ".txt";

        // 객체(버퍼 사이즈를 16KB인)를 만든다.
        BufferedReader bufferedReader = new BufferedReader(
                new FileReader(textPath),
                16 * 1024
        );

        System.out.println("객체 생성 성공");

        // txt파일을 읽는다.
        BufferedReader reader = new BufferedReader(
                new FileReader(textPath)
        );

        System.out.println("파일 읽기 성공");

        // 첫줄은 엔터를 적용하지 않기 위해서 first 사용
        boolean first = true;

        String input;
        // 끝 부분 까지 쭉 쭉 읽는다!
        while ((input = reader.readLine()) != null) {
            //System.out.println(input);

            if (first) {
                result += input;
                first = false;
            } else {
                result += "\n" + input;
            }
        }
        //System.out.println(result);

        // 리더를 닫는다.
        reader.close();
        bufferedReader.close();

        return result;
    }

    // txt 파일 삭제 메소드
    // 삭제 성공시 true 리턴
    // 삭제 실패시 false 리턴
    public boolean txtDelete(String folder, String name) {
        boolean result = true;
        // 텍스트 파일의 폴더와 이름을 받아온다.
        String textPath = "src/main/resources/static/text/" + folder+ "/" + name + ".txt";
        Path path = Paths.get(System.getProperty("user.dir"), textPath);

        try {
            // 파일 삭제
            Files.delete(path);
        } catch (NoSuchFileException e) {
            System.out.println("삭제하려는 파일/디렉토리가 없습니다");
        } catch (Exception e){
            System.out.println("삭제 실패 오류");
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    // txt 파일 생성 메소드
    // 생성 성공시 true 리턴
    // 생성 실패시 false 리턴
    public boolean txtCreate(String folder, String name, String content) {
        boolean result = true;
        String folderPath = "src/main/resources/static/text/"+folder;
        Path path = Paths.get(System.getProperty("user.dir"), folderPath);
        String filePath = path+"/"+name+".txt";
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e){
            System.out.println("파일 생성 실패");
            //e.printStackTrace();
            result = false;
        }
        return result;
    }

    // txt 파일 수정 메소드
    // 수정 성공시 true 리턴
    // 수정 실패시 false 리턴
    public boolean txtUpdate(String folder, String name, String content) {
        boolean result = true;
        boolean delete = txtDelete(folder,name);

        // 삭제 성공하면 생성 할것, 삭제 실패하면 false리턴
        if(delete){
            boolean create = txtCreate(folder,name,content);
            // 생성 실패하면 false리턴
            if(!create){
                result = false;
            }
        }else {
            result = false;
        }
        return result;
    }


    // Paging - > PageDTO랑 같이 사용할것!
    // 페이지,리미트,DB에 몇개 있는지 카운트한 숫자를 넘긴다.
    // 리턴은 PageDTO 형식으로 보낸다.
    public PageDTO paging(int page, int limit, int count){
        PageDTO pageDTO = new PageDTO();

        int block = 5;

        int startRow = (page - 1) * limit + 1;
        int endRow = page * limit;


        int maxPage = (int) (Math.ceil((double) count / limit));
        int startPage = (((int) (Math.ceil((double) page / block))) - 1) * block + 1;
        int endPage = startPage + block - 1;

        if (endPage > maxPage) {
            endPage = maxPage;
        }
        pageDTO.setPage(page);
        pageDTO.setStartRow(startRow);
        pageDTO.setEndRow(endRow);
        pageDTO.setMaxpage(maxPage);
        pageDTO.setStartpage(startPage);
        pageDTO.setEndpage(endPage);
        pageDTO.setLimit(limit);

        return pageDTO;
    }


}

