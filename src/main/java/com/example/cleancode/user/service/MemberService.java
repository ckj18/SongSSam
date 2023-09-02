package com.example.cleancode.user.service;

import com.example.cleancode.user.JpaRepository.MemberRepository;
import com.example.cleancode.user.dto.JwtDto;
import com.example.cleancode.user.dto.MemberDto;
import com.example.cleancode.user.entity.Member;
import com.example.cleancode.user.entity.Role;
import com.example.cleancode.utils.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class MemberService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberRepository memberRepository;
    @Value("${file.dir}")
    private String fileDir;
    public MemberDto findMember(HttpServletRequest request){
        Optional<JwtDto> jwtDtoE = jwtService.resolveJwt(request);
        if(jwtDtoE.isEmpty()){
            return null;
        }
        JwtDto jwtDto = jwtDtoE.get();
        log.info(jwtDto.getAccessToken(),jwtDto.getRefreshToken());
        Long id = jwtService.getId(jwtDto);
        log.info(id.toString());
        Optional<Member> mem = memberRepository.findById(id);
        Member member = mem.get();
        if(mem.isEmpty()) return null;
        return MemberDto.builder()
                .email(member.getEmail())
                .id(member.getId())
                .preference_Genre(member.getPreference_Genre())
                .preference_Singer(member.getPreference_Singer())
                .preference_Title(member.getPreference_Title())
                .nickname(member.getNickname())
                .build();
    }

    public boolean updatePrefer(List<String> artist,List<String> genre,List<String> title, HttpServletRequest request){
        MemberDto member = findMember(request);
        Set<String> set_artist = new HashSet<>(artist);
        Set<String> set_genre = new HashSet<>(genre);
        Set<String> set_title = new HashSet<>(title);
        member.setPreference_Genre(set_genre);
        member.setPreference_Singer(set_artist);
        member.setPreference_Title(set_title);
        try{
            memberRepository.save(member.makeMember());
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public boolean updateUser(MemberDto memberDto,HttpServletRequest request){
        MemberDto member = findMember(request);
        memberDto.setId(member.getId());
        memberDto.setRole(Collections.singletonList(Role.ROLE_USER));
        try {
            memberRepository.save(memberDto.makeMember());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean upload_file(MultipartFile file,HttpServletRequest request){
        log.info("Multipartfile={}", file);
        try{
            if(!file.isEmpty()){
                String fileName = file.getOriginalFilename();
                //----------------------디렉토리 확인 후 생성
                Path uploadPath = Paths.get(fileDir);
                if(!Files.exists(uploadPath)){
                    Files.createDirectories(uploadPath);
                }
                //----------------------디렉토리 확인 후 생성
                log.info("파일 저장 fileName={}",fileName);
                file.transferTo(new File(fileDir+fileName));
            }
            log.info("success");
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            log.info("failure");
            return false;
        }
    }

}
