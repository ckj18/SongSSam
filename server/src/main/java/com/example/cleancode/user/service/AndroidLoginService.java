package com.example.cleancode.user.service;

import com.example.cleancode.user.JpaRepository.MemberRepository;
import com.example.cleancode.user.JpaRepository.TokenRepository;
import com.example.cleancode.user.dto.JwtDto;
import com.example.cleancode.user.dto.MemberDto;
import com.example.cleancode.user.entity.KakaoToken;
import com.example.cleancode.user.entity.Member;
import com.example.cleancode.user.entity.Role;
import com.example.cleancode.user.service.oauth.*;
import com.example.cleancode.utils.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class AndroidLoginService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private KakaoTokenProvider kakaoTokenProvider;
    @Autowired
    private JwtService jwtService;

    //로그인은 앱에서 맡기고 이후 요청은 암호화된 id값으로
    //여기서는 멤버객체 추가만 구현
    @Transactional
    public Member join(AndroidRequestParam androidRequestParam){
        KakaoValidateResponse kakaoValidateResponse = kakaoTokenProvider.tokenInfo(androidRequestParam.getAccessToken());
        Long id = kakaoValidateResponse.getId();
        KakaoInfoResponse kakaoInfoResponse = kakaoTokenProvider.requestUserInfo(androidRequestParam.getAccessToken());
        Optional<Member> isExist = memberRepository.findById(id);
        if(isExist.isEmpty()){
            Member member = Member.builder()
                    .id(id)
                    .email(kakaoInfoResponse.getKakaoAccount().getEmail())
                    .nickname(kakaoInfoResponse.getKakaoAccount().getProfile().getNickname())
                    .role(Collections.singletonList(Role.ROLE_USER))
                    .build();
            log.info(member.toString());
            return memberRepository.save(member);
        }
        return null;
    }
    public JwtDto login(AndroidRequestParam androidRequestParam){
        try{
            KakaoValidateResponse kakaoValidateResponse = kakaoTokenProvider.tokenInfo(androidRequestParam.getAccessToken());
            Long id = kakaoValidateResponse.getId();
            Optional<Member> isExist = memberRepository.findById(id);
            MemberDto memberDto = isExist.get().toMemberDto();
            return jwtService.generate(memberDto,memberDto.getRole());
        }catch (Exception exception){
            exception.getMessage();
            log.info("------------User matching err------------");
            return null;
        }
    }
}
