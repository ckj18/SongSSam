import React, { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import { styled } from 'styled-components';
import "../styles/global.css";
import PerfectScore from '../components/PerfectScore';
import { useParams } from 'react-router-dom';

const Wrapper = styled.div` 
  width : 80%;
  margin-left : 10%;
  padding-top : 80px;
  
`

const Container = styled.div`
    width : 100%;
    min-height : 100vh;

`;

const SongContainer = styled.div`
    display : flex; 
    

    @media screen and (max-width : 700px){
        flex-direction : column;
        width : 100%;
       
        img{
            margin : 0 auto;
            max-width: 100%;
        }

        div{
            text-align : center;
        }
        
    }


    


`;

const SongImg = styled.img`
    width : var(--Detail-SongImg-size);
    height : var(--Detail-SongImg-size);
    background-color : gray;
    border-radius : 10px;
    margin-right : 50px;
    box-shadow: 2px 2px 0.5px rgba(255,255,255,0.7);
`;

const SongCol = styled.div`
    display : flex;
    flex-direction : column;
    
`;

const SongTitle = styled.div`
    font-size : var(--Detail-SongTitle-Fontsize);
    padding : 30px 0px;
    
`
const Singer = styled.div`
    font-size : var(--Detail-Singer-Fontsize);
`

const SampleContainer = styled.div`
    margin-top : 20px;
    display : flex;
    padding : 10px 10px;
    width : 90%;
    margin-bottom : 10px;
    
    
    font-size : 20px;
    background: linear-gradient(108deg, rgb(251, 250, 45) 0.5%, rgb(214, 4, 4) 29.8%, rgb(241, 57, 221) 59.9%, rgb(95, 11, 228) 84.2%);
    box-shadow: 2px 2px 0.5px black;
    cursor : pointer;

    &:hover{
        text-decoration : underline;
        text-decoration-color: white;
        text-decoration-skip: spaces;
        text-underline-offset: 5px; 
        text-decoration-thickness: 1px;

    }
`
const SampleWrite = styled.div`
    font-size : var(--Detail-SampleWrite-Fontsize);
    color : white;
    font-weight : 500;
    width : 100%;
    padding-top : 5px;
    padding-bottom : 5px;
    text-align : center;

`




const Square = styled.div`
    width : 130px;
    height : 130px;
    border-radius : 10px;
    background-color : gray;
`

const OtherContainer = styled.div`
    margin-top : 100px;
`

const OtherTitle = styled.div`
    font-size : 20px;
    margin-bottom  : 20px;
`
const OtherListContainer = styled.div`
    display : grid;
    grid-template-columns : repeat(4,130px);
    grid-gap : 30px;
`
const OtherList = styled.div``;

const OtherCol = styled.div`
    margin-top : 20px;
    margin-bottom : 50px;
`;


const SampleFixed = styled.div`
    position : fixed;
    top : 20%; 
    left : var(--SampleFixed-ml);
    width : 1050px;
    height : 370px;
    background: linear-gradient(112.1deg, rgb(32, 38, 57) 11.4%, rgb(63, 76, 119) 70.2%);
    padding-left : 15px;
    border-radius : 15px;
    padding-right : 15px;
`

const SFContainer = styled.div`
    width : 100%;
    height : 100%;
`
;

const SFHeader = styled.div` 
    display : flex;
    width : 100%;
    justify-content : space-between;
    align-items : center;
    color : white;
    padding-top : 10px;
    padding-bottom : 10px;
    height : 50px;


`;

const SFcloseBtn = styled.div`
    width : 30px;
    height : 30px;
    border-radius : 50%;
    color : white;
    background-color : rgba(0,0,0,0.2);
    display : flex;
    align-items : center;
    justify-content : center;
    cursor : pointer;

    svg{
        width : 17px;
        height : 17px;
    }
`;

const SFtitle = styled.div`
    padding-left : 10px;
`;

const SFProcessContainer = styled.div`
    display : flex;

    div:first-child{
        margin-right : 20px;
    }
`;


const SFprocess = styled.div`
    display : flex;
    align-items : center;
    justify-content : center;
`;

const SFMusicSheet = styled.div`
    width : 100%;
    height : 300px;
    background-color : white;

    div:first-child{
        border-bottom : 1px solid black;
    }
`;

const SFLyric = styled.div`
    width : 100%;
    height : 50%;
`

const AlertContainer = styled(SampleContainer)`
    display : none;
    @media screen and (max-width : 890px){
        display : block;
    }
`;

const PerfectScoreContainer = styled.div`
    display : block;
    @media screen and (max-width : 890px){
        display : none;
    }
`



export default function Detail() {

    const {title, singer,imgUrl,songId} = useParams();
   
    const [sample, setSample] = useState(false);
    const clickSample = () => {
        setSample((cur) => !(cur));
    }

    
    

  return (
  <Layout>
     <Wrapper>
       <Container>
        <SongContainer>
            <SongImg src={imgUrl} alt='Song Image'/>
            <SongCol>
                <SongTitle>
                    <span>{title}</span>
                </SongTitle>
                <Singer>
                    <span>{singer}</span>
                </Singer>
            </SongCol>
        </SongContainer>

        <SampleContainer onClick={clickSample}>
            <SampleWrite >
                <span>샘플링이 필요합니다.</span> 
            </SampleWrite>
            
        </SampleContainer>

        <AlertContainer>
            <span>화면이 좁습니다 888px 이상으로 늘려주세요</span>
        </AlertContainer>
       
        {
            sample &&
        <SampleFixed>
            <SFContainer>
                <SFHeader>
                    <SFtitle>
                        샘플링
                    </SFtitle>
                    <SFProcessContainer>
                        <SFprocess>진행상황 1/10</SFprocess>
                        <SFcloseBtn onClick={clickSample}>
                            <svg xmlns="http://www.w3.org/2000/svg" fill='white'  viewBox="0 0 384 512"><path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/>
                            </svg>
                        </SFcloseBtn>
                    </SFProcessContainer>
                </SFHeader>
                <SFMusicSheet>
                    <SFLyric></SFLyric>
                    <SFLyric></SFLyric>
                </SFMusicSheet>
            </SFContainer>
        </SampleFixed>
        }



        <PerfectScoreContainer>
        {
            <PerfectScore songId = {songId}></PerfectScore>
        }
        </PerfectScoreContainer>
        
        <OtherContainer>
            <OtherCol>
            <OtherTitle>같은 가수의 다른 곡들</OtherTitle>
            <OtherListContainer>
                {
                    [1,2,3,4].map((_,i) => <OtherList key={i}>
                        <Square />
                    </OtherList>
                    )

                }


            </OtherListContainer>
            </OtherCol>

            <OtherCol>
            <OtherTitle>비슷한 장르의 다른 곡들</OtherTitle>
            <OtherListContainer>
                {
                    [1,2,3,4].map((_,i) => <OtherList key={i}>
                        <Square/>

                    </OtherList>
                    )

                }

            </OtherListContainer>
            </OtherCol>

        </OtherContainer>


       </Container>
    </Wrapper>
   
  </Layout>
  )
  
}

