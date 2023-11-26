import React, { useState,useRef, useEffect } from "react";
import styled from "styled-components";
import '../styles/global.css';

const AudioPlayer = styled.div`
    display : flex;
    width : 490px;
    --primary : #50bcdf;
    background-color : transparent;
    align-items : center;

`

const ForwardBackWard = styled.div`
    display : flex;
    background : transparent;
    border : none;
    flex-direction : column;
    justify-content : center;
    padding : 10px 10px;
    cursor : pointer;
    fill : black;

    &:hover{
        color : var(--primary);
    }
    &:hover svg{
        fill : var(--primary);
    }
`

const PlayPause = styled.div`
    display : flex;
    cursor : pointer;
    background : transparent;
    border : none;
    flex-direction : column;
    justify-content : center;
    padding : 10px 10px;

    svg{
        width : 20px;
        height : 20px;
    }

    &:hover{
        color : var(--primary);
    }
    &:hover svg{
        fill : var(--primary);
    }
`

const ProgressBar = styled.input`
    --bar-bg : #A3A096;
    --seek-before-width : 0;
    --seek-before-color : #000000;
    --knobby : #000000;
    --selectedKnobby : #26c9c3;
    appearance : none;

    background : var(--bar-bg);
    border-radius : 10px;
    position : relative;
    width : 100%;
    height : 3px;
    outline : none;

    &::-moz-range-track{
        background : var(--bar-bg);
        border-radius : 10px;
        position : relative;
        width : 100%;
        height : 3px;
        outline : none;
    }

    &::before{
        content : '';
        height : 3px;
        width : var(--seek-before-width);
        background-color : var(--seek-before-color);
        border-top-left-radius : 10px;
        border-bottom-left-radius : 10px;
        position : absolute;
        top : 0;
        left : 0;
        cursor : pointer;
        z-index : 2;
    }

    &::-webkit-slider-thumb{
        -webkit-appearance : none;
        height : 13px;
        width : 13px;
        border-radius : 50%;
        cursor : pointer;
        background-color : var(--knobby);
        box-sizing : border-box;
        position : relative;
        z-index : 3;
        border : none;
    }

    &:active::-webkit-slider-thumb{
        transform : scale (1.2);
        background : var(--selectedKnobby);
    }


    &::-moz-focus-outer{
        border : 0;
    }

    &::-moz-range-progress{
        background-color : var(--seek-before-color);
        border-top-left-radius : 10px;
        border-bottom-left-radius : 10px; 
        height : 3px;  
    }

    &::-moz-range-thumb{
        -webkit-appearance : none;
        height : 13px;
        width : 13px;
        border-radius : 50%;
        cursor : pointer;
        background-color : var(--knobby);
        box-sizing : border-box;
        position : relative;
        z-index : 3;
        border : transparent;
        
    }

    &:active::-moz-range-thumb{
        transform : scale(1.2);
        background : var(--selectedKnobby);
    }
`

const VolumeBar = styled.input`
    --bar-bg : #A3A096;
    --seek-before-width : 100%;
    --seek-before-color : #000000;
    --knobby : #000000;
    --selectedKnobby : #26c9c3;
    appearance : none;

    background : var(--bar-bg);
    border-radius : 10px;
    position : relative;
    width : 10%;
    height : 3px;
    outline : none;


    &::-moz-range-track{
        background : var(--bar-bg);
        border-radius : 10px;
        position : relative;
        width : 100%;
        height : 3px;
        outline : none;
    }

    &::before{
        content : '';
        height : 3px;
        width : var(--seek-before-width);
        background-color : var(--seek-before-color);
        border-top-left-radius : 10px;
        border-bottom-left-radius : 10px;
        position : absolute;
        top : 0;
        left : 0;
        cursor : pointer;
        z-index : 2;
    }

    &::-webkit-slider-thumb{
        margin-top : 1px;
        -webkit-appearance : none;
        height : 13px;
        width : 13px;
        border-radius : 50%;
        cursor : pointer;
        background-color : var(--knobby);
        box-sizing : border-box;
        position : relative;
        z-index : 3;
        border : none;
    }

    &:active::-webkit-slider-thumb{
        transform : scale (1.2);
        background : var(--selectedKnobby);
    }


    &::-moz-focus-outer{
        border : 0;
    }

    &::-moz-range-progress{
        background-color : var(--seek-before-color);
        border-top-left-radius : 10px;
        border-bottom-left-radius : 10px; 
        height : 3px;  
    }

    &::-moz-range-thumb{
        -webkit-appearance : none;
        height : 13px;
        width : 13px;
        border-radius : 50%;
        cursor : pointer;
        background-color : var(--knobby);
        box-sizing : border-box;
        position : relative;
        z-index : 3;
        border : transparent;
        
    }

    &:active::-moz-range-thumb{
        transform : scale(1.2);
        background : var(--selectedKnobby);
    }
`


const CurrentTime = styled.div`
    width : 70px;
    height : 20px;
    text-align : center;
    padding-top : 4px;
    font-size : 12px;
    
`
const DurationTime = styled.div`
    text-align : center;
    width : 70px;
    height : 20px;
    padding-top : 4px;
    font-size : 12px;

    
`

const MuteBtn = styled.div`

    cursor : pointer;
    margin-right : 10px;
    --selectedKnobby : #26c9c3;
    
    transition : all .1s;
    svg:hover{
        transform : scale(1.2);
        fill : var(--selectedKnobby);
    }

    svg{
        width : 20px;
        height : 20px;
    }
`

const ProgressBarContainer = styled.div`
    padding-bottom : 6px;
`


const AudioContainer = ({audioSource, clipDurationTime} : {audioSource : string , clipDurationTime : number}) => {

    const [isPlaying, setIsPlaying] = useState(false);
    const [duration,setDuration] = useState(0);
    const [currentTime, setCurrentTime] = useState(0);
    const [soundZero, setSoundZero] = useState(false);

    const volumeBarRef = useRef<HTMLInputElement | null>(null);
    const audioPlayerRef = useRef<HTMLAudioElement  | null>(null);
    const progressBarRef = useRef<HTMLInputElement | null>(null);
    let animationRef : any = null; 

    // 해당 오디오의 총 녹음된 음성의 재생 시간 표시
    useEffect(() => {
        const progressBar = progressBarRef.current;
        const audioPlayer =audioPlayerRef.current;

        if(audioPlayer && progressBar && clipDurationTime ) {
            const seconds = Math.floor(clipDurationTime);
            setDuration(seconds)
            progressBar.max = seconds.toString();
            return;
        }
    },[audioPlayerRef]);

 
    //시간을 00:00초로 표시
    const calculateTime = (secs : number) => {

        const minutes = Math.floor(secs/ 60) ;
        const returnedMinute = minutes < 10 ? `0${minutes}` : `${minutes}`;
        const seconds = Math.floor(secs% 60) ;
        const returnedSeconds = seconds < 10 ? `0${seconds}` : `${seconds}`;  
        return `${returnedMinute}:${returnedSeconds}`;
    }


    const togglePlayPause = () => {
        const prevValue = isPlaying;
        const audioPlayer = audioPlayerRef.current;
        setIsPlaying(!prevValue);
        
        if (audioPlayer ) {
            if (!prevValue) {
                audioPlayer.play();
                requestAnimationFrame(whilePlaying);
            }
             else {
                audioPlayer.pause();
                cancelAnimationFrame(animationRef);
            }
        }
    }
    
    const changeRange = () => {
        const progressBar = progressBarRef.current;
        const audioPlayer = audioPlayerRef.current;

        if(progressBar && audioPlayer) {
            audioPlayer.currentTime = parseFloat(progressBar.value);
            progressBar.style.setProperty('--seek-before-width',`${parseFloat(progressBar.value)/duration * 100}%`);
            setCurrentTime(parseFloat(progressBar.value)); 
        }
    }

    const whilePlaying = () => {

        const audioPlayer = audioPlayerRef.current;
        const progressBar = progressBarRef.current;

        if(audioPlayer && progressBar){
           progressBar.value = audioPlayer.currentTime.toString();
           animationRef = requestAnimationFrame(whilePlaying); 
           progressBar.style.setProperty('--seek-before-width',`${parseFloat(progressBar.value)/duration * 100}%`);
           setCurrentTime(parseFloat(progressBar.value)); 
           
        }
      }

      const changeVolume = () => {
        const volumeBar = volumeBarRef.current;
        const audioPlayer = audioPlayerRef.current;

        if(volumeBar && audioPlayer) {
            const volumeValue = parseFloat(volumeBar.value);
            volumeBar.style.setProperty('--seek-before-width',`${volumeValue}%`);
            audioPlayer.volume = volumeValue/100;
        }
      }

      const toggleMuteBtn= () => {

        const audio = audioPlayerRef.current;
        const prevValue = soundZero;
        setSoundZero(!prevValue);

        if(audio)
        {
            if(!prevValue) {
            audio.muted = true;
            }
            else{
            audio.muted = false;
            }
        }

      }

      const backTen = () => {
        const  progressBar=  progressBarRef.current ;
        if(progressBar) {
            progressBar.value = ( Number(progressBar.value)- 10 ).toString();
            changeRange();
        }
      }

      const forwardTen = () => {
        const  progressBar=  progressBarRef.current ;
        if(progressBar) {
            progressBar.value = ( Number(progressBar.value) + 10 ).toString();
            changeRange();
        }
      }

    return (
        <AudioPlayer >
            <audio ref={audioPlayerRef} 
                  
            >
            <source 
            src={audioSource}
            type="audio/mpeg" />
            </audio>
            
            <ForwardBackWard onClick={backTen} >
                <svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 0 512 512"><path d="M459.5 440.6c9.5 7.9 22.8 9.7 34.1 4.4s18.4-16.6 18.4-29V96c0-12.4-7.2-23.7-18.4-29s-24.5-3.6-34.1 4.4L288 214.3V256v41.7L459.5 440.6zM256 352V256 128 96c0-12.4-7.2-23.7-18.4-29s-24.5-3.6-34.1 4.4l-192 160C4.2 237.5 0 246.5 0 256s4.2 18.5 11.5 24.6l192 160c9.5 7.9 22.8 9.7 34.1 4.4s18.4-16.6 18.4-29V352z"/></svg>
            </ForwardBackWard>
            <PlayPause onClick={togglePlayPause}>
                {
                    isPlaying ? <svg height="14px" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M48 64C21.5 64 0 85.5 0 112V400c0 26.5 21.5 48 48 48H80c26.5 0 48-21.5 48-48V112c0-26.5-21.5-48-48-48H48zm192 0c-26.5 0-48 21.5-48 48V400c0 26.5 21.5 48 48 48h32c26.5 0 48-21.5 48-48V112c0-26.5-21.5-48-48-48H240z"/></svg> 
                    : <svg height="14px" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M73 39c-14.8-9.1-33.4-9.4-48.5-.9S0 62.6 0 80V432c0 17.4 9.4 33.4 24.5 41.9s33.7 8.1 48.5-.9L361 297c14.3-8.7 23-24.2 23-41s-8.7-32.2-23-41L73 39z"/></svg>
                }
               
            </PlayPause>
            <ForwardBackWard onClick={forwardTen}>

                <svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 0 512 512"><path d="M52.5 440.6c-9.5 7.9-22.8 9.7-34.1 4.4S0 428.4 0 416V96C0 83.6 7.2 72.3 18.4 67s24.5-3.6 34.1 4.4L224 214.3V256v41.7L52.5 440.6zM256 352V256 128 96c0-12.4 7.2-23.7 18.4-29s24.5-3.6 34.1 4.4l192 160c7.3 6.1 11.5 15.1 11.5 24.6s-4.2 18.5-11.5 24.6l-192 160c-9.5 7.9-22.8 9.7-34.1 4.4s-18.4-16.6-18.4-29V352z"/></svg>       
              
            </ForwardBackWard>


            <CurrentTime>{calculateTime(currentTime)}</CurrentTime>
            <ProgressBarContainer>
                <ProgressBar 
                    ref = {progressBarRef}
                    type="range" 
                    defaultValue='0'
                    onChange={changeRange}
                    >
                </ProgressBar>
            </ProgressBarContainer>

            <DurationTime >{calculateTime(clipDurationTime)}</DurationTime>
            
            <MuteBtn onClick={toggleMuteBtn}>
            {
                soundZero ?  
                
                <svg xmlns="http://www.w3.org/2000/svg" 
                    height="10px"   
                    viewBox="0 0 576 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352H64c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM425 167l55 55 55-55c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9l-55 55 55 55c9.4 9.4 9.4 24.6 0 33.9s-24.6 9.4-33.9 0l-55-55-55 55c-9.4 9.4-24.6 9.4-33.9 0s-9.4-24.6 0-33.9l55-55-55-55c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0z"/></svg>
                : 
                
                <svg xmlns="http://www.w3.org/2000/svg" 
                    height="10px"
                    viewBox="0 0 448 512"><path d="M301.1 34.8C312.6 40 320 51.4 320 64V448c0 12.6-7.4 24-18.9 29.2s-25 3.1-34.4-5.3L131.8 352H64c-35.3 0-64-28.7-64-64V224c0-35.3 28.7-64 64-64h67.8L266.7 40.1c9.4-8.4 22.9-10.4 34.4-5.3zM412.6 181.5C434.1 199.1 448 225.9 448 256s-13.9 56.9-35.4 74.5c-10.3 8.4-25.4 6.8-33.8-3.5s-6.8-25.4 3.5-33.8C393.1 284.4 400 271 400 256s-6.9-28.4-17.7-37.3c-10.3-8.4-11.8-23.5-3.5-33.8s23.5-11.8 33.8-3.5z"/></svg>
            
            }
            </MuteBtn>
            
            <VolumeBar ref={volumeBarRef} type="range" max='100' defaultValue="100"  onChange={changeVolume}></VolumeBar>
       
       </AudioPlayer>
    );
}

export default AudioContainer;
