import { useParams } from "react-router-dom";
import Layout from "../components/Layout";
import React, { useEffect,useState } from "react";
import Chart, { IData } from "../components/Chart";
import styled from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../redux/store";
import BigTitle from "../components/BigTitle";
import LoadingCircle from "../components/LoadingCircle";
import { setLoadingfalse } from "../redux/loadingSlice";

const Wrapper = styled.div`
margin : 0 auto;
padding-bottom : 20px;
  
`

const CircleWrapper = styled.div`
 position : fixed;
 
 width : 100vw;
 height : 100vh;
 top : 0;
 left : 0;
 background-color : transparent;
 display : flex;
 justify-content : center;
 align-items : center;
`

export default function Search(){


    const makeTitle = () => {
        return ` "${target}" 에 대한 검색 결과`
    }

    const dispatch = useDispatch();
    const {target} = useParams();

    const [search,setSearch] = useState<IData[]>([]);
    const loading = useSelector((state : RootState) => state.loadingToken.loading);

    useEffect(() => {
        const fetchData = async () => {
          try {
            const response = await (await fetch(`https://songssam.site:8443/song/search?target=${target}&mode=0`,
              {
                method: "GET",
              }
            )).json();

            setSearch(response);
            console.log(search);
            dispatch(setLoadingfalse());
            
          } catch (error) {
            console.error('Error fetching data:', error);
          }
        };
    
        fetchData(); 
      }, [target,loading]);
      
      
    return(
        <Layout>
            <BigTitle title={makeTitle()}/>
            <Wrapper>

              {
              loading ?
              <CircleWrapper>
                <LoadingCircle/>
              </CircleWrapper>
                :
              <Chart  btnTitle ="커버곡 만들러 가기"  data={search} />
            }
            </Wrapper>
        </Layout>
    )

}