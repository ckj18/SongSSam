import React, { useEffect, useState,useRef } from 'react';
import Layout from '../components/Layout';
import styled from 'styled-components';
import "../styles/global.css";
import { useSelector } from 'react-redux';
import { RootState } from '../redux/store';
import AudioContainer from '../components/AudioContainer';
import BigTitle from '../components/BigTitle';
import axios from 'axios';

const Wrapper = styled.div`
  width: 100%;
  position: relative;
  padding-left : 40px;
`;

const Profile = styled.div`
  width  : 600px;
  display: flex;
  margin-bottom: 40px;
  border : 1px solid var(--iconColor);
  border-radius: 15px;
  padding: 30px;
  background-color: white;
`;

const Square = styled.div<{ bgpath: string }>`
  width: var(--Profile-ImgSize);
  height: var(--Profile-ImgSize);
  border-radius: 50%;
  margin-right: var(--Profile-Margin-right);

  background-position: ${props => (props.bgpath === '/img/user-solid.svg' ? 'center bottom' : 'center')};
  background-repeat: no-repeat;
  background-size: cover;
  background-image: url(${props => props.bgpath});
  background-color: ${props => (props.bgpath === '/img/user-solid.svg' ? 'rgba(128, 128, 128, 0.7)' : "transparent")};
  background-blend-mode: multiply;
`;

const ProfileCol = styled.div`
  display: flex;
  flex-direction: column;
`;

const ProfileTitle = styled.div`
  margin-right: 50px;
  h3 {
    
    color: rgba(0, 0, 0, 0.4);
    font-size: var(--Profile-Title-grayFontSize);
    margin-bottom: 5px;
  }

  h1 {
    font-size: 30px;
    font-weight : 400;
  }

  margin-bottom: 30px;
`;

const ProfileEmail = styled(ProfileTitle)`
  margin-bottom: 0;
  h1 {
    font-size: 23px;
  }
`;

const ProfileInner = styled.div`
  padding-top: 10px;
`;

const AudioTitle = styled.div`
  margin-bottom : 20px;
  font-size : 20px;
`

interface IProfile {
  id: number;
  email: string;
  nickname: string;
  profileUrl: string;
  role: string;
}

interface IVocal {
  id: null;
  originUrl: string;
  spectr: [];
  createdAt: string;
  user: null;
  songId : number;
}

interface IMp3{
  mp3 : Blob,
  duration : number,
  songId : number,
}

const AudioWrapper = styled.div`
  border : 1px solid black;
  width : 500px;
  border-radius : 5px;
  margin-bottom : 10px;
`

const Flex = styled.div`
  display : flex;
`
const DeleteBtn = styled.div`
  margin-left : 15px;

  cursor : pointer;
  padding-top : 15px;
  text-align : center;
  font-size : 12px;
  
  color : blue;

  & : hover{
    color : #26c9c3;
  }
  
`

function MyPage() {
  const [profileData, setProfileData] = useState<IProfile>();
  const [vocalData, setVocalData] = useState<IVocal[]>([]);
  const [wavFile, setWavFile] = useState<IMp3[]>([]);
  const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);

  // 프로필 데이터 가져오기
  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        const res = await fetch(`https://songssam.site:8443/member/info`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        const data = await res.json();
        console.log(data);
        setProfileData(data.response);
      } catch (err) {
        console.log(err);
      }
    };

    fetchProfileData();
  }, [accessToken]);

  // 내가 녹음한 데이터 목록 가져오기
  useEffect(() => {
    const fetchVocalData = async () => {
      try {
        const res = await fetch(`https://songssam.site:8443/member/vocal_list`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        const data = await res.json();
        setVocalData(data.response);
      } catch (err) {
        console.log(err);
      }
    };

    fetchVocalData();
  }, [accessToken]);

  useEffect(() => {
    if (vocalData) {
      for (let vocal of vocalData) {
        
        downloadWavFile(vocal.originUrl , vocal.songId);
      }
    }
  }, [vocalData]);

  const getDuration = async (blob: Blob): Promise<number> => {
    const arrayBuffer = await blob.arrayBuffer();
    const audioContext = new (window.AudioContext)();
    const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);
    return audioBuffer.duration;
  };
  

  const downloadWavFile = async (originUrl: string , songId : number) => {
    console.log(songId);
    try {
      const response = await fetch(`https://songssam.site:8443/member/download?url=${originUrl}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }); 

      const blob = await response.blob();
      const duration  = await getDuration(blob);
      setWavFile(prev => [...prev, {mp3 : blob, duration : duration ,songId : songId }]);
    } catch (error) {
      console.error('파일 다운로드 중 오류 발생:', error);
    }
  };

  const handleDelete = async(songId : number) => {
    console.log(songId);
    try{
      const res = await axios.post(`https://songssam.site:8443/member/deleteVocalFile?songId=${songId}`,
      {},
        {
          headers : {
            Authorization: `Bearer ${accessToken}`
          }
        }
      );
      
      if(res.status === 200){
        console.log(res);
        const temp = wavFile.filter(i => i.songId !== songId);
        setWavFile(temp);
        alert("삭제를 성공했습니다");

      }
    }
    catch(e){
      alert("삭제를 실패했습니다")
    }    
  }



  const profileImage = profileData?.profileUrl !== undefined ? profileData.profileUrl : '/img/user-solid.svg';

  return (
    <Layout>

      <BigTitle title='내 정보'/>
      <Wrapper>
        
        <Profile>
          <Square bgpath={profileImage} />
          <ProfileCol>
            <ProfileInner>
              <ProfileTitle>
                <h3>이름</h3>
                <h1>{profileData?.nickname}</h1>
              </ProfileTitle>
              <ProfileEmail>
                <h3>카카오톡 이메일</h3>
                <h1>{profileData?.email}</h1>
              </ProfileEmail>
            </ProfileInner>
          </ProfileCol>
        </Profile>

        <AudioTitle>
          <span >녹음 목록</span>
        </AudioTitle>
        {wavFile
          ? wavFile.map((i, index) => (
            <Flex key = {index}>
              <AudioWrapper key={index}>
                <AudioContainer audioSource={URL.createObjectURL(i.mp3)} clipDurationTime={i.duration}></AudioContainer>
              </AudioWrapper>
               <DeleteBtn onClick={() => handleDelete(i.songId)}>
                <span>삭제</span>
              </DeleteBtn>
            </Flex> 
            ))
          : null}

      </Wrapper>

      
    </Layout>
  );
}

export default MyPage;
