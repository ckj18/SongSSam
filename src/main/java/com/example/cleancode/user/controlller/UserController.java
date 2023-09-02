package com.example.cleancode.user.controlller;

import com.example.cleancode.user.JpaRepository.MemberRepository;
import com.example.cleancode.user.dto.MemberDto;
import com.example.cleancode.user.entity.Role;
import com.example.cleancode.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class UserController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;


    /**
     * 쿠키값에 저장된 jwt 토큰을 기반으로 유저 반환
     * @param request
     * @return
     */
    @GetMapping("/member")
    public ResponseEntity<MemberDto> memberinfo(HttpServletRequest request){
        MemberDto member = memberService.findMember(request);
        log.info(member.toString());
        return ResponseEntity.ok(member);
    }
    /**
     * 쿠키값에 저장된 jwt 토큰을 기반으로 유저 검색후 유저 선호 장르 업데이트
     * 이후 상태코드도 같이 보내주게 변경 필요
     * @param artist
     * @param genre
     * @param title
     * @param request
     */
    @PostMapping("/update_prefer")
    public boolean preferUpdate(@RequestBody List<String> artist, @RequestBody List<String> genre, @RequestBody List<String> title, HttpServletRequest request){
        return memberService.updatePrefer(artist, genre, title, request);
    }
    @PostMapping("/update_user")
    public boolean userUpdate(@RequestBody MemberDto memberDto,HttpServletRequest request){
        return memberService.updateUser(memberDto,request);
    }
    @PostMapping("/upload")
    public boolean saveFileV1(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        return memberService.upload_file(file,request);
    }
    @GetMapping("/get_file") //업로드한 파일 보기 -스트리밍형식으로?
    public void getFile(){
        
    }
    @GetMapping("/get_result") //완료 결과 가져오기 -스트리밍형식으로?
    public void getResult(){
        
    }
}
