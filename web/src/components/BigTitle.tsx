import React from "react";
import styled from "styled-components";

const Title = styled.div`
    width : 100%;  
    background-color : white;
    padding-top : 60px;
    font-size : 34px;
    margin-bottom : 30px;
    padding-left : 45px;
    font-weight : 600;
   
`;


export default function BigTitle({title} : {title : string}) {

    return (
        
        <Title>
            <span>
            {title}
            </span>
        </Title>


    )

}