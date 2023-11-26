import { PayloadAction, createSlice } from "@reduxjs/toolkit";

export type accessTokenState = {
    accessToken : string | null,
}


const initialState : accessTokenState= {
    accessToken : null,
    
}


const accessTokenSlice = createSlice({
    name : 'accessToken',
    initialState,
    reducers : {
        setAccessToken(state,action : PayloadAction<string>){
            
            state.accessToken = action.payload;
        },
      
        deleteAccessToken(state){
            
            state.accessToken = "";
        },
       
    },

});



export const {setAccessToken  ,deleteAccessToken } = accessTokenSlice.actions;

export default accessTokenSlice.reducer;