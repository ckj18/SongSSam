import React, { useEffect, useState,useRef } from 'react';
import Layout from '../components/Layout';
import { styled} from 'styled-components';
import "../styles/global.css";
import PerfectScore from '../components/PerfectScore';
import {useParams} from 'react-router-dom';
import { Link } from 'react-router-dom';
import UploadFormat from '../components/UploadFormat';
import { useDispatch, useSelector } from 'react-redux';
import { RootState} from '../redux/store';
import { IAI, IData} from '../asset/Interface';
import { MakeString } from '../asset/functions';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { clearInst, donwnLoadInst } from '../redux/instSlice';
import { clearSongSlice, setSongSlice } from '../redux/songSlice';
import { AnimatePresence , motion } from 'framer-motion';


const Wrapper = styled.div` 
  margin : 0 auto;
  padding : 0 40px;
  padding-top : 60px;
`

const Container = styled.div`
    width : 100%;
`;

const SongContainer = styled.div`
    display : flex; 
    
    margin-bottom  : 50px;

    @media screen and (max-width : 890px){
        display : block;
    }
`;

const SongInfoContainer = styled.div`
    display  : flex;
    justify-content : start;
`

const SongImg = styled.img`
    width : 270px;
    height : 270px;
    background-color : gray;
    border-radius : 10px;
    margin-right : 50px;
`;

const SongCol = styled.div`
    display : flex;
    flex-direction : column;
    width : 100%;

    @media screen and (max-width : 900px){
        margin-top : 10px;
        align-self : initial;
        align-items : center;
        justify-content : center;
    }  
`;


const SongInfoContainerTop = styled.div`
    padding-top : 100px;
    margin-bottom : 80px;

    @media screen and (max-width : 890px){
        padding-top : 10px;
        margin-bottom : 10px;
    }
`


const SongTitle = styled.div`
    font-size : 26px;
    font-weight : 600;
    margin-bottom : 10px;
    display : flex;
`
const Singer = styled.div`
    font-size : 26px;
    color : var(--iconColor);
    cursor : pointer;
    display : flex;
    
    &:hover{
      text-decoration : underline;
        text-decoration-color: inherent;
        text-decoration-skip: spaces;
        text-underline-offset: 5px; 
        text-decoration-thickness: 2px;  
    }
`

const PlayBtnContainer = styled.div`
    cursor : pointer;
`

const PlayBtn = styled.div`
    width : 80px;
    height : 30px;
    background-color : var(--iconColor);
    border-radius : 5px;
    font-size :  14px;
    color :white;
    padding-top : 8px;
    padding-left : 14px;

    &:hover{
        text-decoration : underline;
    }
`

const SampleButton = styled.div`
    margin : auto; 
    margin-top : 20px;
    
    text-align : center;
    font-size : 25px;
    width : 100%;
    padding-top : 10px;
    padding-bottom : 10px;
    background-color : #F6BA42;
    color : black;
    border-radius : 10px;
    
    flex-shrink : 1;
    cursor : pointer;

    &:hover{
        text-decoration : underline;
        text-decoration-color: inherent;
        text-decoration-skip: spaces;
        text-underline-offset: 5px; 
        text-decoration-thickness: 2px;

    }
`
const AlertContainer = styled(SampleButton) `
    display: none;
    @media screen and (max-width : 890px){
        display : block;
    }
`

const Square = styled.div`
    width : 120px;
    height : 120px;
    border-radius : 10px;
   
`

const OtherContainer = styled(motion.div)`
    overflow : hidden;
`

const OtherTitle = styled(motion.div)`
    font-size : 20px;
    font-weight : 500;
`
const OtherListContainer = styled(motion.div)`
    display : grid;
    grid-template-columns : repeat(4,100px);
    grid-gap : 100px;
`
const OtherList = styled(motion.div)<{bgpath : string}>`
    background-image: url(${props => props.bgpath});
    background-size : contain;
    background-repeat : no-repeat;
    cursor : pointer;
    position : relative;
    overflow : hidden ;
    border-radius : 10px;
`;

const OtherCol = styled.div`
    margin-top : 0px;
    position : relative;
    height : 200px;
`;

const PerfectScoreContainer = styled.div`
    margin-top : 10px;
    display : block;
    @media screen and (max-width : 890px){
        display : none;
    }
`

const UploadLabel = styled.label`
    width : 80px;
    height : 30px;
    background-color  : red;
    color : white;
    font-size : 13px;
    padding-top : 9px;
    padding-left : 11px;
    border-radius : 5px;
    margin-left : 10px;
    cursor : pointer;
`;

const UploadInput = styled.input`
    display : none;
`;

const FFlex = styled.div`
    display : flex;
`



const OtherList__title = styled.div`
    position : absolute ;
    bottom : 0px;
    left : 0px;
    display : flex;
    justify-content : center;
    align-items : center;
    width : 110px;
    font-size : 12px;

`

const PerfectBtn = styled(PlayBtn)`
    color : white;
    background-color : #414F6C;
    padding : 0;
    text-align : center;
    padding-top: 8px;
    margin-left  :10px;
`

const AIBtn = styled(PlayBtn)`
    background-color : #2964A7;
    padding : 0;
    text-align : center;
    padding-top : 8px;
    margin-left : 10px;
    width : 110px;
`

const AIContainer = styled.div`
    position : fixed;
    top : 23%;
    left : 28%;
    width : 700px;
    height : 350px;
    background-color : white;
    border-radius : 10px;
    z-index : 2;
    border : 1px solid var(--iconColor);
    padding : 20px 20px 10px 20px;
`

const Overlay = styled.div`
  position : fixed;
  top : 0;
  left : 0;
  width : 100%;
  height : 100%;
  background-color : transparent;
  cursor : pointer;
  
`

const AIContainer__Title = styled.div`
    font-size : 20px;
    width : 100%;
    position : relative;
    margin-bottom : 20px;
`

const AIContainer__Grid = styled.div`
    display : grid;
    grid-template-columns : repeat(2, 1fr);
    gap : 10px;
`

const AIContainer__Grid__Container = styled.div`
    border : 1px dashed var(--iconColor);
    height : 260px;
`

const AIContainer__Grid__Container__flex = styled.div`
    display : flex;
    height : 100%;
    width : 100%;
    flex-direction : column;
    justify-content : center;
    align-items : center;
    cursor : pointer
`

const AI_Img = styled.img`
    width : 140px;
    height : 170px;
    border-radius : 50%;
    margin-bottom : 20px;
    padding-top : 10px;

`

const AI_NAME = styled.div`
    font-size : 15px;
    font-weight : bold;
`

const CloseBtn = styled.div`
  position : absolute ;
  top : -5px;
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

const WholeWrapper = styled.div`
  position : fixed;
  top : 0;
  left : 0;
  width : 100vw;
  height : 100vh;
  background-color : rgba(133, 133, 133,0.5);
  z-index : 2
`

const Manual= styled.div`
  padding-top : 20px;
 
`;
const ManualBody = styled.div`
  div{
    margin-bottom : 5px;
  }
`;
const ManualTitle = styled.div`
  font-size : 18px;
  padding-bottom : 10px;

  div{
    padding-bottom  :10px;
  }
`;

const ManualContainer = styled.div`
  margin-bottom  :30px;
  border-radius : 10px;
  width : 750px;
  background-color : #F9F9F9;
  display : flex;
  padding-top : 20px;
  margin-top : 10px;
  img {
    padding-left : 10px;
    padding-bottom : 10px;
    height : 200px;
    width : 200px;
    margin-right : 20px;
  }
`

const sliderVar = {
    invisible  : (back : boolean) => (
        {
        x : back ? -100 : 100,
        y : 0,
        opacity : 0,
        }
    ),
    visible : {
        x : 0,
        y : 0,
        opacity : 1,
        
    },
    exit : (back : boolean) => (
        {
        x : back ? 100 : -100,
        y : 0,
        opacity : 0,
    }),
}

const MaleListContainer = styled(OtherListContainer)`
  position : relative ; 
`

const LeftArrow = styled.div`
  cursor : pointer;
  position : absolute ;
  top : 0;
  left : 0;
  background-color : rgba(0,0,0,0.7);
  height : 120px;
  width : 30px;
  z-index : 1;
  display : flex;
  justify-content : center;
  align-items : center;
  opacity : 0;
  transition: opacity 0.3s ease; 

  svg{
    fill : white;
  }

  &:hover{
    opacity : 1;
    }
`

const RightArrow = styled.div`

    cursor : pointer;
    position : absolute ;
    top : 0;
    left : 680px;
    background-color : rgba(0,0,0,0.7);
    height : 120px;
    width : 30px;
    z-index : 1;
    display : flex;
    justify-content : center;
    align-items : center;
    opacity : 0;
    transition: opacity 0.3s ease; 

    svg{
        fill : white;
    }

    &:hover{
        opacity : 1;
    }
`

const RecommendCol = styled.div`
    margin-bottom : 50px;
    overflow : hidden;
    position : relative;
`

const RecommendTitle = styled(OtherTitle)`
    margin-bottom : 30px;
`

export default function Detail() {

    const {title, singer,imgUrl,songId, originUrl,instUrl} = useParams();
    const [perfect,setPerfect] = useState(false);
    const [play , setPlay] = useState(false);
    const [audioSource , setAudioSource] = useState('');
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const [recommendList , setRecommendList] = useState<IData[] | null>(null);
    const [AI, setAI] = useState(false);
    const [AIData,setAIData] = useState<IAI[]>([]);
    const [targetAI, setTargetAI] = useState(null);
    const [maleRecommend, setMaleRecommend] = useState<IData[] | null>([]);
    const [femaleRecommend, setFemaleRecommend] = useState<IData[] | null>([]);
    const [maleIndex, setmaleIndex] = useState(0);
    const [femaleIndex, setFemaleIndex] = useState(0);
    const [back,setBack] = useState(false);

    const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);
    const movePage = useNavigate();
    const dispatch = useDispatch();

    const handlePerfect = () => {
        setPerfect((cur) => !cur);
    }
    const handleAI = () => {
       
        if(originUrl === 'null'){
            alert("노래 업로드가 필요합니다");
            return;
        }
        setAI((cur) => !cur);
    }

    const closeAI =()=> {
        setAI((cur) => !cur);
    }



    useEffect(() => {

        const handleDownloadMp3 = async () => {
            try {
              const URL = `https://songssam.site:8443/song/download?url=origin/${originUrl}`;
              const newAudio = new Audio(URL);
              audioRef.current = newAudio;

              return () => {
                newAudio.pause();
                newAudio.src = '';
              };
            } catch (error) {
              console.error('Error fetching MP3:', error);
              return Promise.reject(error);
            }
          };
      
          handleDownloadMp3();
          return () => {
            if (audioRef.current) {
                audioRef.current.pause();
                audioRef.current.src = '';
            }
          };

    },[]);

    const handlePlay = () => {
        if(audioRef.current){
            setPlay(prev => !prev);
            if(!play){
                audioRef.current.pause();
            }
            else{
                audioRef.current.play();
            }
        }
    };

    useEffect (() => {
        dispatch(clearInst());
        dispatch(clearSongSlice());
        const downloadInstFile = async () => {
            try {
                if(instUrl !== 'null'){
                   const response = await fetch(`https://songssam.site:8443/song/download_inst?url=inst/${instUrl}`); 
                    const blob = await response.blob();
                    const audioURL = URL.createObjectURL(blob); 
                    dispatch(setSongSlice(audioURL));
                    dispatch(donwnLoadInst());
                }
                else{
                    setAudioSource('null');
                    dispatch(donwnLoadInst());
                }
            } catch (error) {
              console.error('파일 다운로드 중 오류 발생:', error);
            }
          };
          downloadInstFile();
      },[instUrl]);
    
      useEffect( () => {
        const getRecommendList = async() => {
         try{
            const res = await fetch('https://songssam.site:8443/member/user_recommand_list',{
                headers :{
                    Authorization : `Bearer ${accessToken}`
                }
            });
            const JSON = await res.json();
            setRecommendList(JSON);
            }
            catch(e){
                console.log(e);
            }   
        }
        getRecommendList();
        
      },[accessToken]);

      useEffect(()=>{
        const fetchData = async () => {
            try{
                const data = await(await fetch('https://songssam.site:8443/ddsp/sampleVoiceList')).json();
                setAIData(data);
            }
            catch(e){
                console.log(e);
            }
        }
        fetchData();
      },[]);

      const clickAI = (id : number) => {
        const data = {
            targetVoiceId : id,
            targetSongId : songId,
        }
        const requestAI = async() => {
            try{
                const res = axios.post('https://songssam.site:8443/ddsp/makesong',data).then(() => alert("성공"));
            }
            catch(e){
                console.log(e);
            }
        }
        requestAI();
      };


      useEffect(() => {
        const fetchMaleData = async() => {
            const res = await fetch('https://songssam.site:8443/ddsp/recommand_ptr?ptrId=52');
            const data = await res.json();
            setMaleRecommend(data);
        }

        const fetchFemaleData = async() => {
            const res = await fetch('https://songssam.site:8443/ddsp/recommand_ptr?ptrId=53');
            const data = await res.json();
            setFemaleRecommend(data);
        };

        fetchFemaleData();
        fetchMaleData();
      },[]);

      const goLeft = () => {
        setBack(true);
        if(maleIndex === 0){
            setmaleIndex(2);
        }
        else{
            setmaleIndex(prev => prev-1);
        }
      }

      const goRight = () => {
        setBack(false);
        if(maleIndex === 2){
            setmaleIndex(0);
        }
        else{
            setmaleIndex(prev => prev+1);
        }
      }

      const goFLeft = () => {
        setBack(true);
        if(femaleIndex === 0){
            setFemaleIndex(2);
        }
        else{
            setFemaleIndex(prev => prev-1);
        }
      }

      const goFRight = () => {
        setBack(false);
        if(femaleIndex === 2){
            setFemaleIndex(0);
        }
        else{
            setFemaleIndex(prev => prev+1);
        }
      }

  return (
  <Layout>
     <Wrapper>
       <Container>
        <SongContainer>
            <SongImg src={imgUrl} alt='Song Image'/>
            <SongInfoContainer>
                <SongCol>
                    <SongInfoContainerTop>
                        <SongTitle>
                            <span>{title}</span>
                        </SongTitle>
                        <Singer>
                            <Link to ={`/search/${singer}`}>
                                <span>{singer}</span>
                            </Link>
                        </Singer>
                    </SongInfoContainerTop>
                    <PlayBtnContainer>
                    <FFlex> 
                        {
                            originUrl === 'null' ? 
                            <UploadLabel>
                                <span>노래 업로드</span>
                                <UploadInput type='file' accept='audio/mpeg'/>
                            </UploadLabel>
                            :
                            <PlayBtn onClick={handlePlay}>노래 듣기</PlayBtn>
                        }

                        <PerfectBtn onClick={handlePerfect}>노래 부르기</PerfectBtn>
                        <AIBtn onClick={handleAI}>AI 커버곡 만들기</AIBtn>

                    </FFlex>  
                </PlayBtnContainer>
                </SongCol>
                
            </SongInfoContainer>
        </SongContainer>
    
       
       {
        originUrl ==='null '  ? 
        
        <SampleButton >
            <span>파일 업로드가 필요합니다</span> 
        </SampleButton>
        :
        null
       }

        <div style={{marginTop : '10px'}}>
        {
            originUrl === 'null' ?
             <UploadFormat Id={Number(songId)}/>
            :
            null
        }
        </div>

        {
            perfect && 
            <PerfectScoreContainer > 
                    <AlertContainer> <span>화면이 좁습니다</span> </AlertContainer>
                    <PerfectScore songId = {songId} />
            </PerfectScoreContainer>
        }

        {
            AI &&
            <WholeWrapper>
            <Overlay onClick={closeAI}></Overlay>
            <AIContainer >
                <AIContainer__Title>
                    <span>원하는 목소리를 선택해주세요</span>
                    <CloseBtn onClick={closeAI} >
                        <svg xmlns="http://www.w3.org/2000/svg" fill='white'  viewBox="0 0 384 512"><path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/>
                        </svg>
                    </CloseBtn>
                </AIContainer__Title>
                <AIContainer__Grid>
                    {AIData?.map((AI) => 
                    <AIContainer__Grid__Container 
                        key={AI.id}
                        onClick={() => clickAI(AI.id)}
                    >
                        <AIContainer__Grid__Container__flex>
                            <AI_Img src = {AI.id === 52 ? 
                                'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxASEA8SDxASFhAREBcQEBAQFRUPFRAXFRIXFxYVFRgaHSggGBslGxYVITEhJikrLjAuFyAzODUtNykuLisBCgoKDg0OGxAQGy0mICUtLS8vLS0tLS8tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAOEA4QMBIgACEQEDEQH/xAAcAAEAAgMBAQEAAAAAAAAAAAAABgcBBAUDCAL/xABIEAABAwICBwQHBAYHCQEAAAABAAIDBBEFEgYTITFBUWEHInGRFDJCYoGhsUNScqIIIySCkrIVFzRTY3PBJTN0g6Sz0eHwFv/EABoBAQACAwEAAAAAAAAAAAAAAAAEBQIDBgH/xAA2EQACAQICBgcHBAMBAAAAAAAAAQIDEQQFEiExQWGhMlFxgZGx8BMVIlLB0eEjM0JTFiRyBv/aAAwDAQACEQMRAD8AvFERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBaGLYtT00RlqZmRRjYXyHKL8hzPQbVnGMSjpqeaomNooYzI88bNF7DmTuA5lfP0MNVj1U+qrHuZSxuLI2NPqA2Oqi4XtbM/jcdLa6tWFKDnUdkjKEJTkox2k4xftxoYyRTU801tz3kUzHeBdd3m1aWH9vUDnWnoHsb96GZs5/hc1n1XRwzAaSnAEEETfeyhzz4vNyfNbVbQwzNLZo2PafZkaHfVUrz+mpdB27Vfw/JYLLZ26Wvs9eRMNHdJKSvi1lJMHtGx7drXxk8HtO1p/8AguyvmjGGf0PXRVOHVDb3OamL8zgPajkF7ujdzO0G3GxU3ru3ama1uoopnPI2iV7IWg8QC3MXbegV3RqRqwU4bHwsV84uEnGW1FwIqNk7WcamP7LhjGg7iY55/n3QvI6a6Uu3UzG9NSxv870lVpx6Uku9BQk9ifgy90VEf/s9Km76Zp/5LHfyvXq3tXxqH+04a1wG8tinh/NdwXkatOTtGSfeg4SW1PwZeS5+L4rBSxOmqZWRxN3vebC/ADiSeQ2qscL7dKVxAqqOaM3sTE5k4HMkHIR5EqF12Ivx3EiZphFSxX1MJcGuDL27jTvkdvc7bbdwCyqTVODnPUkeRWk1FbWTXF+3WlY61NSSygH15Xtpmu6tFnO8wFjC+3alebVNHLHt9aGRlSB1Nww+QK38MwinpmhsELGAcQO8ernHaT4lfrEMLp5xaeGOQe+0OI6g7weoVF/kFPS1Qduu6v4fksPds7dJX9bybaP6R0lazPRzskaPWANns6PYbOb8QuuvnLHtHpsLkbX4ZI5rYz32ElxjBIFj9+M7AQd30u7QnSNmIUUNSwBpcC2WMG+rkbsc3w4joQrqhXp14KpTd15cGQalOVOWjLad9ERbTAIiIAiIgCIiAIiIAiIgCIiArbt7q3MwoMG6eqijd4NDpPrG1c3QulbHh9G1o3wtkd1dIM7vm4rt9t2HOmwiZzQSaeWOosPutJa8+Aa9x+CqOm0rrJqeloKGNwlEQikkbbOQ3Z3T7DctruO3w41maYapiKcYQaSvdt7ErbWS8HVjSm29ttXHgTfSTTOlo7tJ1kw+xjIu38btzPr0USE2M4p6l4KU8QTCwj8XryfDu+C72jGgEMFpKm01R61nbY2HfsB9Y+8fIKZqjeJw2F1YeOlL55bO5dXHmWHsqtbXUdl1L6shGFdmlKyxqHvmdxAJhZfwacx/iUqoMIpoBaCCJnVjGgnxdvK3UUKvjcRW/cm336vBaiTToU6fRQREUWxuFkREsDTr8Jp5xaeCKTq9jXEeBtcfBRTFuzWkkBMDnwu4C+uZfwcb+RU3WVKoYyvQd6c2u801KFOfSSKsJxnDNpvPSt8Z2Afzx/y+KlujWmtLV5WX1U5+ykI73+W7c7w2HopKohpRoFBUZpILQ1G+7RaOQ+80bj7w+amrE4bFasTFRl88VbxXr7xnRq0ddJ3Xyv6P7kpq6dskckbxdsjCxwPEOFj9VH/0dqlw/pKnJ7rHRSD8R1jHH4iNvkovhmmFVQufS4ix7ixh1Tz3n3sclz9ownZm3jjfhMP0d8Oc2nral1/10zImki19U0lzhzGaUjxaVc5VhKmG9opO6bTTWx6nr8rkHGV41dFrbrvwLfREVsQgiIgCIiAIiIAiIgCIiAIiq/tq0wNNA2ipnH0qqb3yz1o4ibbLbczzdo6B3GyJXBGu0bTGbE6n+jMMJMGYtnlabCctPeueELeftHmLX7ejOjsNFFkj2yOsZZiO9If9GjgP9dq1NBtGxRQDMB6RKAZnD2eUY6D5m55KSLks1zH28nSpv4FzfX2dRdYPC+zWlLa+RlYRFTFgEREAREQBERAEREAWVhF6Dk6RYBDWRGOUWI2xyj1ozzHMcxxUb0C0rmweqOH4if2VzrxycIS8m0jT/dON8w9k3PNTpcDTHR1tbTluwTMu6B52WP3Sfuu3H4HgrbLMxeHkqdR/A+XHs6/EgYzCKotKPS8y3mm+7cv0qm7E9LXyMdhtUSJ6Zp1Gb1nRsOV0Z95h+R91WyuvKQIiIAiIgCIiAIiIAiIgNetqWRRySyOyxxsdI9x9lrRcnyC+fdFM+J4nU4lUDuMfeJp2hrrWiZ+4yx8SCrB7dca1GGalps+rkER56tvfkPhsa0/jXK0Jwz0ehp2EWe5uuk55n94g+As34KszfEujhmo7ZavuSsFS9pV17FrO8FhF5VRfq5NWAZNW4xh24uynKD0vZcalrsX7dlc9roqRmrMaLjmdiGa+0NbM0X42DRbyXnrsZ54j5VKvPccv7Ycyu94r5JF5JdUd6TjI9rEfKp/8LqaNV+MGqgaTVuYZGiRszX5Ml++XF42Wbc38FjLJZRi37WHierHpu2hIt1YRFSXuWARFlAEVP6RPxk1U4/bcusdqxAJNXkzdzLk2erbrzXN1OM/dxLyqVdxyZuKftYFe8ek7aEi8kVG5MZHDEvKpXpBUY2HNDDiGa/dDmzEE8Lhwtbx2LL3HL+2HM894L5JF3LBXnBmytzgZ8ozW3ZrbbfFeio2rMsVrK407p5KGtpcTptjhIDJbYC9o49HszNPh1V9YPiMdTTw1ERvHNG2VnOzgDY9RuPgq50lwwVNJPD7T2Es6Pb3mH+IBfn9H7GzLRT0rz3qWUOYDvEc1yB8Htk8wuwyfEuth9F7Yau7cUWOpaFS62PX37y1kRFakMIiIAiIgCIiAIiICiu2d/pOM4dR72MazMP8AOlvJ+SNqmigeKO12lk5O6HYOmSla36uKnq5jP53qwh1K/i/wW+Wx+CUuPkYREVAWZlYRRHtIxiampWahxa+WXVmQb2jI5xy8ibWv4rdh8O69WNKO1uxrrVFTg5vcS5FTug2kdU2tgjdNJJHM/VvZI50m8GzmlxJBB2+F1cS347Aywk1GTTurq32NeGxCrx0krBERQiQFlYRAZWEVU9puNVAq9QyR7Io42OyscWZy4XLjbfyt0UvBYJ4ur7NNLVe5oxFdUYaT1lroq+7K8Znl9Ihme57YmsfG55Jc3MXAtzHaRsuL8irAWGLwjw1V0pa7HtCsqsFNBERRzcCoP2cSeiaSVNPezKgStaNw2htQzyAcB4lThV/iL9TpNhsm7O6EE/jL4j8iFeZDO1eUeuPk7lbmUf00+p+Z9AoiLqinCIiAIiIAiIgCIiAoGh26UYlfgZvrEFPVAiNXpXXNPtl/54Y5B9FPlyeer/ZX/K+pdZc/0n2swiIqUsAtPFsMhqYnRTtzMdt5EEbnNPArcRZRk4tSi7NHjSasyOYFoXR0smtjD3SC+R0zs2S4scoAAvbZdSRYUf0o0sgoQA+75nDMyFuw2+88+y35ngFv/XxdVK7lL1yRqtToQ6kSBYJ5qvKbHNIKoaykw86o7WnUuII6Pe4B/iAoRpLX1ks8grs7ZWHK6FwLBFsHdDL7OfW6tKWQ15P9SSS8SHUzKml8Kb5F9rKozRTE8SbKI6DWyvc0/s4a6ZpA9rJ7NrjvbFMKnGNIqUayqoLxDa46okNHV0bjk8SvKuQ14v4JJ8n67xDMqbXxJosJcbSDRelrMpnYc7BZsjDkdbkTxG/fzXjorpVBXNOS7JWi74XG5A+80+03r5qQKrftsLVtrjJevAm3hWh1pnLwPAqekYWU7LBxu9ziXOeRuuT9Ny6aItVScpycpO7e9mcYqKtFWQREWBkZVd6Zj/bWDn/Fp/lVhWIq70kbrNIMJjG9r6c/9Q55+TVb5Iv9tdj8iBmH7Pej6DCyiLrykCIiAIiIAiIgCIiAojtHZ6NpLSTHY2dsLieG3NA7yGUqbrh/pCYQ51NSVbL3p5TE8jZlbLYtcfB7Gj99bmA4iKimgnH2kQc4cnWs4fBwIXOZ/R6FVcU/NFpls+lDvN9ERc4WwREQH5e6wJO4C5+Cg/ZHgrMSrquvqwHiJ7XRxv2tMj7llxxDGNFh1B4KcuFwQdx2FV/2d463BcRqqSsu2mnLQJeDMpOqkPNhaSCeBHQrov8Az7jeov5WXhv52KrM9K0Xu+p9A2UC0/7NIMSe2ZspgqgMjpA0SNlaNwkbcXI4EEb7G9hacU87JGtfG5rmOF2vYQ5rhzBGwqt+1XtGZSRGnoZ2mtcQHOZlkFM3eS69xnI2Bp535X6VX3FUzvaB6B0+Fsdq3GSokAEtQ8BpIG5jGj1G322234ndaX2CgPZZpwyvpmR1EzfT47tlYbMdMAe7K1vHZa9txvuuFOZ5msa573Naxou5ziGtaOZJ2AIwUR2mYYzC8Xo6qlAZHUnO+NuxoLXtbMAODXNe0253PK08sq902xhmM4xSxUvepqY5dYL2eA8PmkHu2a1o5kdQrDXMZ+46dNb7O/ZfV9S2yy+jLqujCIi58tAiIgCgmjrPSdK2kbW02Yk8hFBl/wC49TOvq2wxSSv9WNjnu8Gi6j/6P+HukkxCvlHee7UNdzc92tm+eq8iuhyClec6nUrd71vkVeZz1Rj3l0BZRF0pUhERAEREAREQBERAcvSTCWVlJUU0nqzROZfflPsuHUOAPwVIdmtc+GWow6oGWWKRzmNPBzTaVg8sw5i5X0GqZ7adGJIZY8WowQ+MtFTl9kt2MmPMW7jumXhdR8Vh44ijKk9+zg93ribaNV0pqaJMsLlaNY1HWU7ZWWDvVljvcxvG9vhxB4hdZcJUhKnJxmrNbTooyUldbDCIiwMwuTj+j9PWMDZ27W+pI3uvjvyPLobhdZFnTqSpyUoOzRjKCkrSV0VtJ2WuBcI62zHb2uiNz42eA7yC7ujegdLSubI4mWZu1rngNaw82sG49STZSxFNq5piqkNCU9XBJeRHhg6MHdRIVj/Z1TzvMkDzC8m5aGh8d+YbcFp8DboucezaeQtFRiDnsadgyvkI/DmfZvirGRZU82xdOOip6uKT80eSwVGTu0cnR7R6no2FsDTd1tZI7a+S2655dBsXWRFBqVJ1JOc3dskxioK0dgREWsyCLK0MaxSKmgfNKe6wbAN7zwY3qTsWUIynJRirt7DFyUVd7CIdqWLERxUcVzLUOBc1u05Q4ZG+Ln2/hPNW9oLgAoMPpqbZnYzNMR7Uj+9IfDMSB0AVW9kmAS19dJi1Y3uRvOoB9V8o2DL7kY2D3urSryXd4PDLDUY09+/t3/Y5yvWdWo5eHYERFJNQREQBERAEREAREQBeFRC17HMe0OY9pY5rtocCLEEcQV7ogPnrSjR+pwGs9IpQ5+HyuDbE3DQfsZDwI25X/wDu80wTGIaqISwOu3c5p2Ojdxa4cD9d42KyK6ijmjfFMxr45G5XseMwcORCpDSjQCtwqV1XhDnyU+98P+8fE3fle37WMcx3h5uVbmGXRxS0k7TW/c+D9aiVhsU6Ls9cScLCiWjOndNUhrZSIZzsyuPckPuPP0O3xUtXI18PVoS0Kis/Wx7y8p1YVFeLuERFpNgREQBERAERZQGFlYUb0l00paQObmEk4+xjI7p/xHbmfXottGjUrT0KauzXUqRhHSk7I7WJYhFTxOlneGxtG0neeQaOJPABQDDcPqtIKwDvRYfA7vu+4DvA4OmcPg0fm2dHtEsQxyVlTXOdDQ747DLnbygaef8AeOv0vuF4YNhMFLDHBTRtZFGLNa35kk7XE7yTtK63LssjhvjnrnyXZx4+GqxS4rFut8K1R8z2w2higijhhYGRRsDGMG5oG7x8VtoitCGEREAREQBERAEREAREQBERAEREBAtM+y6hry6Ro1FS7aZYmgtkP+LHsDvEWPVVzUYFpBhPqNNTSt3asOqGgfg2SR/DYF9BrFlhUpxqR0ZpNcT2MnF3i7FB4b2m07u7UwvicDZxZ+uaCOYsHDwsVJ6HSSimtqqqEk+y5wY7+F1ip5jWitBV/wBqpIZHWtnc0CQeDxZw81CsT7EsMkuYZKiHo14mb5PBPzVVVyTDTd4XjzXP7k2GYVY7bM3Wm4uNo5jaF+lEn9h08ZJpsTtyvE+I9Llkm3yWv/VTjzT3MTjtz9Kq2fIMP1UOWQP+NRd6/JvWZ9cOf4Joh67uahf9VWPn18Tjt/xVW/5FgXu3sRqpLek4oCOWrkm8s8gSOQS/lUXcn9bB5mt0H4nbrsfo4b62phaRwL2k+Q2qM4n2m0kYOoZJK7gT+pZ8S7vflUmwzsPw5ljNNUy+6C2Fp/hGb8ymmC6GYbSWNNRwteN0pbrJB+++7vmpdLI8PHpty5LkaJ5hVfRsuZTVNSaQ4rsiiNNTO9p2amYQfeP6x/7osp5od2R0VJlkqSKmcG4MjcsLDv7sdzc9XE9AFY4CyrWnShSjowikuBDlOU3eTuYAssoi2GIREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQBERAEREAREQH/2Q==' 
                                :
                                 'https://img1.daumcdn.net/thumb/R1280x0.fjpg/?fname=http://t1.daumcdn.net/brunch/service/user/cnoC/image/eXfGgW51usY6AQ6eiB2AVRXo7II.jpg'} />
                            <AI_NAME>{AI.id === 52 ? '남자 TTS' : '여자 TTS'}</AI_NAME>
                        </AIContainer__Grid__Container__flex>    
                    </AIContainer__Grid__Container>)}
                </AIContainer__Grid>
            </AIContainer>
            </WholeWrapper>
        }

        <OtherTitle>이런 곡은 어떠세요</OtherTitle>
        {
            (accessToken && recommendList ) ?
            
        <OtherContainer>
            <OtherCol>

            <OtherListContainer>
                {
                    recommendList && recommendList.map((song,i) => 
                    <OtherList 
                        key={i} 
                        bgpath = {song.imgUrl} 
                        onClick={() => movePage(MakeString(song))}>
                        <Square />
                        <OtherList__title >
                            {song.title.length <6 ? song.title : song.title.slice(0,6) + '...'}
                        </OtherList__title>
                    </OtherList>
                    )
                }
            </OtherListContainer>
            </OtherCol>
        </OtherContainer>

        :
        <ManualContainer>
        <img src='https://t1.daumcdn.net/cfile/tistory/994BD3505D1B23A101'/>
        <Manual>
            <ManualTitle>
                <div>이런! 추천 리스트가 없어요 ㅠㅠ </div> 
                <div> 추천 리스트를 만들어보세요</div>
            </ManualTitle>
            <ManualBody>
                
                   <div> <span>1. 카카오톡 로그인 후 <Link style={{textDecoration : 'underline', fontWeight : 'bold', color : 'blue'}} to ='/prefer'>선호하는 노래 조사</Link> 페이지 누르고 선호 장르 10개 고르기 <br/></span> </div>
                   <div> <span>2. 노래 페이지 들어간 후, 노래 부르기 버튼 누르고 녹음 시작 버튼 누르기 <br/></span></div>
                   <div> <span>3. Inst에 맞춰 열심히 노래를 부른 후 녹음 종료 버튼 클릭<br/></span></div>
                   <div> <span>4. 생성된 audio 옆에 파일 전송 버튼 누르기</span></div>
                
            </ManualBody>
        </Manual>
        </ManualContainer>
        }

        <RecommendTitle>남자 목소리로 이런 노래도 만들어보세요</RecommendTitle>

        <OtherContainer>
            <RecommendCol>
                <LeftArrow onClick={goLeft}>
                    <svg xmlns="http://www.w3.org/2000/svg" height="40" width="40" viewBox="0 0 320 512"><path d="M9.4 233.4c-12.5 12.5-12.5 32.8 0 45.3l192 192c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L77.3 256 246.6 86.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0l-192 192z"/></svg>
                </LeftArrow>  
                <RightArrow onClick={goRight}>
                    <svg xmlns="http://www.w3.org/2000/svg" height="40" width="40" viewBox="0 0 320 512"><path d="M310.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-192 192c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L242.7 256 73.4 86.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l192 192z"/></svg>
                </RightArrow>
            <AnimatePresence custom={back}>
                <MaleListContainer
                    custom={back}
                    variants = {sliderVar}
                    initial = 'invisible'
                    animate = 'visible'
                    exit = 'exit'
                    transition = {{type  : 'tween',}}
                    key={maleIndex}
                >
                    
                   
                    {
                        maleRecommend && maleRecommend.slice(maleIndex*4,maleIndex*4+4).map((song,i) => 
                        <OtherList 
                            key={i}
                            bgpath = {song.imgUrl} 
                            onClick={() => movePage(MakeString(song))}>
                            <Square />
                            <OtherList__title >
                                <span>{song.title.length <6 ? song.title : song.title.slice(0,6) + '...'}</span>
                            </OtherList__title>
                        </OtherList>
                        )
                    }
                 
                </MaleListContainer>
            </AnimatePresence>
            
            </RecommendCol>
        </OtherContainer>

        <RecommendTitle>여자 목소리로 이런 노래도 만들어보세요</RecommendTitle>

        <OtherContainer>
            <RecommendCol>
                <LeftArrow onClick={goFLeft}>
                    <svg xmlns="http://www.w3.org/2000/svg" height="40" width="40" viewBox="0 0 320 512"><path d="M9.4 233.4c-12.5 12.5-12.5 32.8 0 45.3l192 192c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L77.3 256 246.6 86.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0l-192 192z"/></svg>
                </LeftArrow>  
                <RightArrow onClick={goFRight}>
                    <svg xmlns="http://www.w3.org/2000/svg" height="40" width="40" viewBox="0 0 320 512"><path d="M310.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-192 192c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L242.7 256 73.4 86.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l192 192z"/></svg>
                </RightArrow>
            <AnimatePresence custom={back}>
                <MaleListContainer
                    custom={back}
                    variants = {sliderVar}
                    initial = 'invisible'
                    animate = 'visible'
                    exit = 'exit'
                    transition = {{type  : 'tween',}}
                    key={femaleIndex}
                >
                   
                    {
                        femaleRecommend && femaleRecommend.slice(femaleIndex*4,femaleIndex*4+4).map((song,i) => 
                        <OtherList 
                            key={i}
                            bgpath = {song.imgUrl} 
                            onClick={() => movePage(MakeString(song))}>
                            <Square />
                            <OtherList__title >
                                <span>{song.title.length <6 ? song.title : song.title.slice(0,6) + '...'}</span>
                            </OtherList__title>
                        </OtherList>
                        )
                    }
                 
                </MaleListContainer>
            </AnimatePresence>
            
            </RecommendCol>
        </OtherContainer>

       </Container>
    </Wrapper>
   
  </Layout>
  )
  
};

