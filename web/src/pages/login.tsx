import axios from "axios";
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { setAccessToken } from "../redux/accessTokenSlice";
import { setRefreshToken } from "../redux/refreshTokenSlice";
import { useDispatch } from 'react-redux';
import serverURL from "../asset/Url";
import LoadingCircle from "../components/LoadingCircle";


const Wrapper = styled.div`
    width : 100%;
    height : 100%;
    display : flex;
    justify-content : center;
    align-items : center;
    background-color : ${prop => prop.theme.iconColor};
`

const Container = styled.div`
  width : 100%;
  height : 100%;
  display : flex;
  justify-content : center;
  align-items : center;
`


export default function Redirect(){

    const movePage = useNavigate();
    const dispatch = useDispatch();

    const kakaoLogin = async(code : string | string[] | undefined) => {

        try {
          const res = await axios.post(`https://${serverURL}/auth/login`, 
          {
          authorizationCode: code // 코드를 요청의 본문에 추가ㄴ
          }, );

          dispatch(setAccessToken(res.data.response.accessToken));
          dispatch(setRefreshToken(res.data.response.refreshToken));        
          movePage("/");
          
        } catch (err) {
          console.log("소셜로그인 에러", err);
          window.alert("로그인에 실패했습니다.");
          movePage("/");
        }
      };

        useEffect(()=> {

        let code = new URL(window.location.href).searchParams.get("code"); 
        
        if(code){
            kakaoLogin(code);
        }
        else{
            movePage("/");
        }
        },[kakaoLogin]);

    
    return (
       <Wrapper>
          <Container>
            <LoadingCircle/>
          </Container>
            
       </Wrapper>

    )
}