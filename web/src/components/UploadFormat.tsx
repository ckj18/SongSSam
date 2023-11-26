import React, { useRef,useState,useEffect } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import axios from "axios";



const UploadBox = styled.div<{dragging : string}>`
  background-color : white;
  padding-left : 45px;
`;

const UploadButton = styled.label<{dragging : string}>`
  width : 500px;
  height : 100px;
  border-radius : 10px;
  border : 4px dashed black;
  cursor : pointer;
  transition : background-color 0.1s ;
  background-color : ${(props) => (props.dragging === 'true' ? 'gray' : 'white')};
  text-align : center;
  padding-top : 35px;

  &:hover{
    background-color : gray;
  }

  
`

const UploadInput = styled.input`
  display : none;
  width : 500px;
  min-height : 100px;
  border-radius : 10px;
  
`;

const UploadForm = styled.form`
  display : flex;
  flex-direction : column;
`;

const UploadInputInfo = styled.div`
  width : 500px;

  span {
    font-size : 12px;
  }
`

const UploadInputInfo__title = styled.div`
  display : flex;
  margin-bottom : 20px;
  margin-top : 20px;
  border-bottom : 2px solid #D3D3D3;
  padding-bottom  :5px;
  padding-left : 10px;
  font-size  : 12px;
  font-weight : 500;
  color : gray;

  span{
    margin-right : 100px;
  }
`

const UploadBtn__container = styled.div`
  display : flex;
  overflow : hidden;
`

const UploadCancleBtn = styled.div`
  color  :  #FF0084;
  font-size : 12px;
  cursor : pointer;
  margin-right : 20px;
  
  &:hover{
    color : #26c9c3;
  }

`
const UploadBtn = styled(UploadCancleBtn)`
  color : blue;
`;

const SelectedFileContainer = styled.div`
`

const SelectedFileDiv = styled.div`
  display : flex;
  margin-bottom : 10px;
  justify-content : space-between;
  padding-left : 10px;
`

const ProgressRing = styled.svg`
  transform: rotate(-90deg);
`;

const ProgressCircle = styled.circle`
  transition: 0.35s stroke-dashoffset;
  transform-origin: 50% 50%;
`;


const ProgressContainer = styled.div`
  height : 100px;
  width : 300px;
  border : 1px solid black;
  padding : 10px 10px;
`

const Flex = styled.div`
  display : flex;
  align-items: center;
  margin-top : 10px;

  p{
    margin-left : 10px;
  }
`

const ProgressWrapper = styled.div`
  position : fixed;
  right : 0;
  bottom : 0;
  width : 300px;
  min-height : 100px;
`

const ProgressContainer__header = styled.div`
  display : flex;
  justify-content : space-between;
  margin-bottom : 15px;
  padding-right : 10px;

  svg {
    cursor : pointer;
  }
`


export default function UploadFormat (Id : {Id? :number}){

  const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);
  const uploadInput = useRef<HTMLInputElement>(null);
  const [selectedFile , setSelectedFile] = useState<File[]>([]);
  const [dragging, setDragging] = useState(false);
  const [uploadProgress, setUploadProgress] = useState({});

    
      const handleUpload = (file : Blob, songId: string ) => {

        console.log(songId);
        const fetchData = async () => {
          const formData = new FormData();
          const mp3File = new Blob([file] , {'type' : 'audio/mpeg'});
          formData.append('file', mp3File, songId);
          
          try {
            const response = await axios({
              url: `https://songssam.site:8443/song/upload?songId=${songId}`,
              method: "POST",
              data: formData,
              onUploadProgress: (progressEvent) => {
                if(progressEvent.total){
                  const progress = Math.round((progressEvent.loaded / progressEvent.total) * 100);
                  setUploadProgress(prev => ({...prev, [file.name]: progress,}));
                }
              }
            });
            if (response.status === 200) {
              console.log("노래 업로드 성공");
            }
          } catch (error) {
            console.log("노래 업로드 실패 :", error);
            alert("노래 업로드 실패");
          }
        };

        fetchData();
      };

      const handlePreprocess = (songId: string) => {
        
        const fetchData = async () => {
          try {
            const response = await axios.post(
              `https://songssam.site:8443/song/preprocess?songId=${songId}`, 
            );
            if (response.status === 200) {
              alert("전처리 요청 성공");
              console.log(response);
            }
          } catch (error) {
            alert("전처리 요청 실패 :");
            console.log(error);
          }
        };
        fetchData();
      };

      const handleDragLeave = (e :React.DragEvent<HTMLDivElement> ) => {
        setDragging((prev) => prev = false);
      }

      const handleDragOver = (e :React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(true);
       
      }

      const handleDragEnter = (e :React.DragEvent<HTMLDivElement> ) => {
        e.preventDefault();
        setDragging(true);
      }
    
      const handleSubmit = (e : React.FormEvent) => {
        e.preventDefault();
      }

      const deleteSelectedFile = (index : number) => {
        setSelectedFile((prev) => prev.filter((_,i) => i !== index));

      }

      const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        console.log(e);
        const file = e.target.files?.[0];
        if(file){
          if (file.type === 'audio/mpeg') {
            setSelectedFile((prev) => [...prev, file]);
          }else {
            alert('mp3 파일만 업로드 할 수 있습니다');
          }
        }
      };

      const handleDrop = (e :React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(false);
        if (e.dataTransfer.items) {
          for (let i = 0; i < e.dataTransfer.items.length; i++) {
            if (e.dataTransfer.items[i].kind === 'file') {
              const file = e.dataTransfer.items[i].getAsFile();
              if (file && file.type === 'audio/mpeg') {
                setSelectedFile((prev) => [...prev, file]);
              } else {
                alert('mp3 파일만 업로드 할 수 있습니다');
              }
            }
          }
        }
      }

      useEffect(() => {
        console.log(selectedFile);
      },[selectedFile])

      const deleteProgress = (key : string) => {
        setUploadProgress((prev) => {
          const newState: { [key: string]: any } = { ...prev };
          delete newState[key];
          return newState;
        });
      }

    return(
       
           <div>
            
            
            <UploadBox
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDragEnter= {handleDragEnter}
              dragging= {dragging.toString()}
              onDrop={handleDrop}
            >
              <UploadForm onSubmit={handleSubmit}>
                <UploadButton
                  dragging= {dragging.toString()}
                >
                  파일 선택
                <UploadInput 
                  ref={uploadInput} 
                  type="file" 
                  
                  onChange={handleFileChange}
                  accept="audio/mpeg"
                />
                </UploadButton>


                <UploadInputInfo>
                  <UploadInputInfo__title>
                    <span>파일이름</span>
                  </UploadInputInfo__title>
                  {
                    selectedFile.map((mp3,index) => 
                    <SelectedFileContainer>
                      <SelectedFileDiv key={index}>
                        {mp3.name}
                        <UploadBtn__container>
                          <UploadCancleBtn onClick={() => deleteSelectedFile(index)}>
                            삭제
                          </UploadCancleBtn>
                          <UploadBtn onClick={() =>{
                            const songId = Id && Id.Id ? Id.Id.toString() : (mp3.name).split('.')[0];
                            handleUpload(mp3, songId);
                          }}>
                            업로드
                          </UploadBtn>
                          <UploadBtn onClick={() => {
                            const songId = Id && Id.Id ? Id.Id.toString() : (mp3.name).split('.')[0];
                            handlePreprocess(songId);
                          }}>
                            전처리
                          </UploadBtn>
                        </UploadBtn__container>
                      </SelectedFileDiv>
                    </SelectedFileContainer>  
                  )}
                  
                </UploadInputInfo>
              </UploadForm>  
            </UploadBox>

            <ProgressWrapper>
              
              {Object.entries(uploadProgress).map(([fileName, progress]) => {
                const progressNumber = progress as number;
                const radius = 18;
                const circumference = 2 * Math.PI * radius;
                return (
                  <ProgressContainer key={fileName}>
                    <ProgressContainer__header>
                    <p style={{fontWeight : '500'}}>{fileName} </p>  
                    <svg 
                        xmlns="http://www.w3.org/2000/svg" 
                        height="1em" 
                        viewBox="0 0 384 512"
                        onClick={() => deleteProgress(fileName)}
                    >
                          <path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/>
                    </svg>
                    </ProgressContainer__header>
                    <Flex >
                    <ProgressRing width="40" height="40">
                      <ProgressCircle
                        className="progress-ring__circle"
                        stroke="green"
                        strokeWidth="4"
                        fill="transparent"
                        r={radius}
                        cx="20"
                        cy="20"
                        style={{
                          strokeDasharray: `${circumference} ${circumference}`,
                          strokeDashoffset: `${circumference - progressNumber / 100 * circumference}`
                        }}
                      />
                    </ProgressRing>
                    <p>파일 업로드: {progressNumber}%</p>
                    </Flex>
                  </ProgressContainer>
                );
              })}
             
            </ProgressWrapper>

            </div>
        
    )
}