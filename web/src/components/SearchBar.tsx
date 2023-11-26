import React from "react";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import styled from "styled-components";
import { ChangeEvent } from 'react';
import { useDispatch } from 'react-redux';
import { setLoadingTrue } from "../redux/loadingSlice";

const SearchBarForm = styled.form`
    width : 100%;
    position : relative;
    margin-bottom : 10px;
    
    
`

const SearchBar = styled.input` 
    width : 100%;
    height : 34px;
    padding : 2px 10px;
    outline : none;
    border : 1px solid rgba(0,0,0,.15);
    border-radius : 10px;
    margin-bottom : 10px;
    

    &:focus {
        outline : 2px solid var(--iconColor);
        border : transparent;
    }   

    &::placeholder{
        font-size : 12px; 
        
      }
`


const Svg = styled.svg`
    width : 14px; 
    height : 14px;
    position : absolute;
    top : 10px;
    right : 10px;
    z-index : 1;
    cursor : pointer;
    fill : #F9F9F9;
    
`


export default function SearchBBar ( ){

    const movePage = useNavigate();

    const handleChange = (e :ChangeEvent<HTMLInputElement>) => {
        setSearch(e.target.value);
    };

    const [search, setSearch] = useState("");
    const dispatch = useDispatch();  


    
    const handleSubmit = (e :React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if(search === "")
            return;

        dispatch(setLoadingTrue());
        movePage(`/search/${search}`);
        setSearch("");
    }

    return (
            <SearchBarForm onSubmit={handleSubmit}>
            <SearchBar placeholder="검색" value={search} onChange={handleChange}>
            </SearchBar>
            
            <Svg  fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z"></path>
            </Svg>
            </SearchBarForm> 
       
    )
    
}
