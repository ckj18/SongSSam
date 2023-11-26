import React, { useEffect,useState } from 'react';
import Layout from '../components/Layout';
import { styled } from 'styled-components';
import Chart from '../components/Chart';
import serverURL from "../asset/Url";
import { IData } from '../components/Chart';
import BigTitle from '../components/BigTitle';

const Wrapper = styled.div`
  margin : 0 auto;
  padding-bottom : 20px;
`


function Search() {

  
  const [chartData, setChartData] = useState<IData[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await (await fetch(`https://${serverURL}/song/chartjson`,
          {
            method: "GET",         
          }
        )).json();        
        
        setChartData(response);

      }catch(err){
        console.log("실패!");
        console.log(err);
      }
    };
    fetchData();

    }, []);

  return (
    <Layout>
      <Wrapper>
        <BigTitle title = '인기차트'/>
        <Chart  btnTitle ="커버곡 만들러 가기"  data={chartData} />
      </Wrapper>
    </Layout>
  )
};


export default Search;



