import styled from "styled-components";
import React from "react";

const Circle = styled.div`
    width: 48px;
    height: 48px;
    border-radius : 50%;
    border: 5px solid #FFF;
    border-bottom-color: #FF3D00;
    animation: rotation 1s linear infinite;

    @keyframes rotation {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
    } 
`

export default function LoadingCircle (){
    return (
        <Circle/>
    )
}