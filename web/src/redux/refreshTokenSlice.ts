import { PayloadAction, createSlice } from "@reduxjs/toolkit";

export type refreshTokenState = {
    refreshToken : string | null,
}


const initialState : refreshTokenState= {
  
    refreshToken : null,
}


const refreshTokenSlice = createSlice({
    name : 'refreshToken',
    initialState,
    reducers : {
        
        
        setRefreshToken(state,action : PayloadAction<string>){
            
            state.refreshToken = action.payload;;
        },
        
        deleteRefreshToken(state){
            
            state.refreshToken = "";
        },

        
       
    },

});



export const {setRefreshToken ,deleteRefreshToken } = refreshTokenSlice.actions;

export default  refreshTokenSlice.reducer;