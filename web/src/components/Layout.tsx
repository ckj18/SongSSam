import React from "react";
import Header from "./Header";
import styled from "styled-components";
import LeftMenu from "./LeftMenu";

const Wrapper = styled.div`

position : relative;
width : 100%;
height : 100vh;
min-height : 100vh;

`

const Container = styled.div`
    left : var(--LeftMenu-width);
    position : absolute ;
    top : var(--navigation-height);
    
    width : calc(100vw - var(--LeftMenu-width)) ;
    min-height : 92vh;
    background-color : #FFFFFF;
    
`;

function Layout(props : {children : React.ReactNode}){

    return(
        <Wrapper>
            <LeftMenu/>
            <div>
                <Header/>
                <Container>
                    {props.children}
                </Container>
            </div>
        </Wrapper>
    )

}

export default Layout;