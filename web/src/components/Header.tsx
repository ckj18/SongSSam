import React, { useState } from 'react';
import styled from 'styled-components';
import {KAKAO_AUTH_URL} from "./KaKao";


import "../styles/global.css";
import { useNavigate } from 'react-router-dom';
import {  useSelector } from 'react-redux/es/hooks/useSelector';
import { deleteAccessToken } from '../redux/accessTokenSlice';
import { deleteRefreshToken } from '../redux/refreshTokenSlice';
import { useDispatch } from 'react-redux';
import { RootState, persistor } from '../redux/store';
import {REST_API_KEY,REDIRECT_LOGOUT_URI} from './KaKao'
import { Link } from 'react-router-dom';

const Wrapper = styled.div`
  background-color :  #FFFFFF;
  height : var(--navigation-height);
  width : calc( 100% - var(--LeftMenu-width) );
  color : black;
  position : fixed;
  top : 0;
  right : 0;
  z-index: 1;
  border-bottom : 1px solid  rgba(0,0,0,.15); 
  
`;


const Container = styled.div`
  font-size : 10px;
  width : 100%;
  padding : 10px 20px;
  height :  100%;
  display : flex;
  font-size : 20px;
  justify-content : space-between;
  align-items : center;
  
`


const Column = styled.div`
  display : flex;
  align-items : center;
  cursor : pointer;
  font-size : 15px;
`;  


const LoginBtn = styled.div`

  color : white;
  background-color: var(--iconColor);
  padding : 7px 15px;
  border-radius : 5px;
  font-size : 14px;
  border : none;

`



const LoginContainer = styled.div`

  width : 300px;
  height : 430px;
  background-color : white  ;
  position : fixed;
  z-index : 4;
  top : 20%;
  left : 40%;
  border-radius : 10px;
  
`


const LoginInnerContainer = styled.div`
  display : flex;
  flex-direction : column;
  align-items : center;
  padding-top : 100px;
  position : relative;

  

`;



const LogoImg = styled.img`

    width : 150px;
    height : 150px;
    margin-bottom : 80px;
  
`


const CloseBtn = styled.div`
  position : absolute ;
  top : 5px;
  right : 5px;
  width : 30px;
  height : 30px;
  display : flex;
  justify-content : center;
  align-items : center;
  background-color : rgba(0,0,0,0.2);
  border-radius : 50%;
  cursor : pointer;

  svg{
  border-radius : 50%;
  width : 15px;
  height : 15px;
  }
`

const Overlay = styled.div`
  position : fixed;
  top : 0;
  left : 0;
  width : 100%;
  height : 100%;
  background-color : rgba(133, 133, 133,0.5);
  cursor : pointer;
`


function MainHeader() {


  const dispatch = useDispatch();  
  const movePage = useNavigate();
  const [click, setClick] = useState(false);
  
  const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);
 
  const clickLogin = () =>{
    
    setClick((cur) => true);
    
  }

  const kakaoLogout = async() => {

    try{
      const response =await fetch(
        `https://kauth.kakao.com/oauth/logout?client_id=${REST_API_KEY}&logout_redirect_uri=${REDIRECT_LOGOUT_URI}`,
        {
          method : 'GET'
        }
      );
    }
    catch(error) {
      console.log(error);
    }
  }

  
  const clickLogout = () => {
    dispatch(deleteAccessToken());
    dispatch(deleteRefreshToken());
    persistor.purge();
    kakaoLogout();
    setClick(cur => false);
    movePage("/");
  }

  const loginclose = () => {
     setClick((cur) => false);
  };

  return (

    <Wrapper>
      <Container>
        <Column>
          <audio src=''></audio>
        </Column>

        <Column >
          <LoginBtn onClick={accessToken ?  clickLogout : clickLogin }> { accessToken ? "로그아웃" :"로그인"}</LoginBtn>
        </Column>
      </Container>

      
        { 
          click && 
          <Overlay onClick={loginclose}>
            <LoginContainer >

              <LoginInnerContainer >

                <CloseBtn onClick={loginclose}>
                <svg xmlns="http://www.w3.org/2000/svg" fill='white'  viewBox="0 0 384 512"><path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/>
                </svg>
                </CloseBtn>
                    <LogoImg alt='로고' src='/img/logo.svg'/>
                <Link  to={KAKAO_AUTH_URL}>
                  <img style={{width : '270px'}} src='/img/kakao.png' alt='카카오톡'/>
                </Link>
                
              </LoginInnerContainer>
            </LoginContainer>
          </Overlay>
        
      }
      
    </Wrapper>

  )
}

export default MainHeader;