import React, { useEffect,useState } from "react";
import { styled} from 'styled-components';
import Layout from "../components/Layout";
import serverURL from "../asset/Url";
import { IData } from "../components/Chart";
import BigTitle from "../components/BigTitle";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

const Wrapper = styled.div`
    pading-top : 20px;
    display: flex;
    align-items: center;
    flex-direction : column;
    overflow: hidden;
    
`

const Header = styled.div`
    text-align : center;
    width : 200px;
    height : 70px;
    font-size  : 16px;
    
`;

const HeaderBtn = styled.div<{bgcolor : string}>`
    background-color : ${ prop => prop.bgcolor};
    border-radius : 7px;
    text-align : center;
    padding : 5px 10px;
    margin-top : 10px;    
`


const Grid = styled.div`
    margin-top  :5px;
    display : grid;
    grid-template-columns : repeat(var(--gridNum), var(--preferImgSize));
    grid-gap : 45px;
`

const ChartContainer = styled.div<{bgpath : string ; isclicked : string | undefined}>`

    border-radius : 10px;
    height :110px;
    width :110px;
    background-image : url(${prop => prop.bgpath});
    background-size : cover;
    background-position: center;
    position : relative;
    cursor : pointer;
    display : flex;
    align-items : center;
    justify-content : center;

    background-color: ${props => (props.isclicked ? "rgba(0,0,0,0.5)" : 'transparent')};
    background-blend-mode: multiply;

    
    
`

const SongTitle = styled.div`
    position : absolute;
    bottom : -50px;
    left : 0px;
    width : 100%;
    height : 3rem;
    padding-top : 5px;
    padding-left : 5px;
    text-align : center;
    font-size : 13px;
`;


function Prefer(){

    const [chartData, setChartData] = useState<IData[]>([]);
    const accessToken = useSelector((state: RootState) => state.accessToken.accessToken);
    const [count ,setCount] = useState(0);
    const [clicked, setClicked] = useState<number[]>([]);

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


        const clickPrefer = ( id : number) => {
            const index = clicked.findIndex((i) => i === id);
            if( index === -1) //추가
            {
                if(count === 10){
                    return ;
                }
                const temp  = id;
                setClicked((prev) => [...prev , temp]);
                setCount((cur) => cur+1);
            }
            else{ // 삭제
                setClicked([ ...clicked.slice(0,index), ...clicked.slice(index+1) ] );
                setCount((cur) => cur-1);
            }  
        }

        const handleSubmit = () => {
            const uploadPrefer = async () => {
                try {
                  const response = await fetch(`https://songssam.site:8443/member/user_list`, {
                    method: "POST",
                    headers: {
                      Authorization: `Bearer ${accessToken}`,
                      "Content-Type": "application/json",
                    },
                    body : JSON.stringify(clicked),
                  });
                  if (response.ok) {
                    console.log("요청 성공");
                  }
                } catch (error) {
                  console.error("선호 노래 업로드 실패:", error);
                }
              };
           
            uploadPrefer()
            alert("제출했습니다");
        }

    return(
        <Layout>
            <BigTitle title='선호 노래'/>
            <Wrapper>
                <Header>

                    <span> 선택한 곡  개수 {count} / 10 </span>
                    
                    <HeaderBtn bgcolor = {count === 10 ? "rgba(255, 165, 0,1)" : "rgba(255, 165, 0, 0.5)" }
                                style = {{cursor : count === 10 ?  "pointer" :  "not-allowed"}}
                                onClick={count === 10 ? handleSubmit : undefined}
                    >
                                
                        제출
                    </HeaderBtn> 

                    { count === 10 &&  <span>10개 모두 선택했습니다</span>}
                </Header>
                <Grid>
                {chartData?.map((song,i) => (
                    <ChartContainer  isclicked={clicked.some((id) => id === song.id) ? "true" : undefined} key={i} bgpath={song.imgUrl} onClick={() =>clickPrefer(song.id )}>
                       
                       <SongTitle>{song.title.length <= 20 ? song.title : song.title.slice(0,20) + '...' }</SongTitle>
                       
                       {clicked.some((id) => id=== song.id) &&
                        
                        <svg xmlns="http://www.w3.org/2000/svg" fill="green" height="2.5em" viewBox="0 0 512 512" >
                        <path d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM369 209L241 337c-9.4 9.4-24.6 9.4-33.9 0l-64-64c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0l47 47L335 175c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9z"/></svg>
                        
                        }


                    </ChartContainer>
                ))}

                </Grid>
            </Wrapper>
        </Layout>
    )

}

export default Prefer;