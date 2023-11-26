import React, { useEffect, useState,useRef } from 'react';
import Layout from '../components/Layout';
import { styled } from 'styled-components';
import "../styles/global.css";
import PerfectScore from '../components/PerfectScore';
import { useNavigate, useParams} from 'react-router-dom';
import { Link } from 'react-router-dom';
import UploadFormat from '../components/UploadFormat';
import { useSelector } from 'react-redux';
import { RootState, persistor } from '../redux/store';
import { IData } from '../components/Chart';


const Wrapper = styled.div` 
  width : 80%;
  margin-left : 10%;
  padding-top : 80px;
  
`

const Container = styled.div`
    width : 100%;
   

`;

const SongContainer = styled.div`
    display : flex; 
    margin-bottom  : 20px;

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
    width : 130px;
    height : 130px;
    border-radius : 10px;
   
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
const OtherList = styled.div<{bgpath : string}>`
    background-image: url(${props => props.bgpath});
    background-size : contain;
    background-repeat : no-repeat;
    cursor : pointer;
    position : relative;
    overflow : hidden ;
    border-radius : 10px;
`;

const OtherCol = styled.div`
    margin-top : 20px;
    margin-bottom : 50px;
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

const PerfectBtn = styled(SampleButton)<{loading  : string}>`
    display: ${(props) => (props.loading === 'true' ? 'none' : 'block')};
`

const OtherList__title = styled.div`
    position : absolute 
    bottom : -20px;
    left : 0px;
    text-align : center;
    padding-top : 10px;
    font-size : 12px;

`

export default function Detail() {

    const {title, singer,imgUrl,songId, originUrl,instUrl} = useParams();
    const [perfect,setPerfect] = useState(false);
    const [play , setPlay] = useState(false);
    const [audioSource , setAudioSource] = useState('');
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const [loading, setLoading] = useState(true); 
    const [recommendList , setRecommendList] = useState<IData[] | null>(null);
    
    const movePage = useNavigate();
    const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);

    const handlePerfect = () => {
        setPerfect((cur) => !cur);
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
        const downloadInstFile = async () => {
            
            try {
                if(instUrl !== 'null'){
                   const response = await fetch(`https://songssam.site:8443/song/download_inst?url=inst/${instUrl}`); 
                    const blob = await response.blob();
                    const audioURL = URL.createObjectURL(blob); 
                    setAudioSource(audioURL);
                }
                else{
                    setAudioSource('null');
                } 
               
            } catch (error) {
              console.error('파일 다운로드 중 오류 발생:', error);
            }
            finally{
                setLoading(false);
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

      const goToDetail = (song : IData) => {
        const {artist , title, id, imgUrl} = song;
        let originUrl = 'null';
        let instUrl   = 'null';
    
        if(song.originUrl !== null){
          originUrl = song.originUrl.split('/')[1];
        }
    
        if(song.instUrl !== null){
          instUrl = song.instUrl.split('/')[1];
        }
    
        movePage(`/detail/${artist}/${title}/${id}/${encodeURIComponent(imgUrl)}/${originUrl}/${instUrl}`);
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
                    </FFlex>  
                </PlayBtnContainer>
                </SongCol>
                
            </SongInfoContainer>
        </SongContainer>
    
        <SampleButton>
                    <span>샘플링이 필요합니다</span> 
        </SampleButton>
        <PerfectBtn onClick={handlePerfect} loading = {loading.toString()}>
                    <span>퍼펙트 스코어</span> 
        </PerfectBtn>
       
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
                    <PerfectScore songId = {songId} audioSource = {audioSource}/>
            </PerfectScoreContainer>
        }

        {
            accessToken &&
            
            <OtherContainer>
            <OtherCol>
            <OtherTitle>추천 곡</OtherTitle>
            <OtherListContainer>
                {
                    recommendList && recommendList.map((song,i) => 
                    <OtherList 
                        key={i} 
                        bgpath = {song.imgUrl} 
                        onClick={() => goToDetail(song)}>
                        <Square />
                        <OtherList__title >
                            {song.title}
                        </OtherList__title>
                    </OtherList>
                    )
                }
            </OtherListContainer>
            </OtherCol>
        </OtherContainer>
        }
        
       </Container>
        
        
    </Wrapper>
   
  </Layout>
  )
  
};

