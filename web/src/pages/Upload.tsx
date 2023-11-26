import Layout from "../components/Layout";
import React, { useRef,useState,useEffect } from "react";
import styled from "styled-components";
import BigTitle from "../components/BigTitle";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import axios from "axios";
import UploadFormat from "../components/UploadFormat";

const Wrapper = styled.div`
  margin : 0 auto;
  padding-bottom : 20px;
  `


export default function Upload (){

    return(
        <Layout>
            <Wrapper>
            <BigTitle title = '노래 업로드 하기'/>
            
            <UploadFormat></UploadFormat>

            </Wrapper>
        </Layout>
    )
}