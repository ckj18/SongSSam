import { createSlice } from "@reduxjs/toolkit";

export type loadingState = {
    loading : true | false;
    
}


const initialState : loadingState= {
    loading : true,
}

const loadingSlice = createSlice({
    name : 'loadingToken',
    initialState,
    reducers : {
        setLoadingTrue(state){
            
            state.loading = true;
        },
      
        setLoadingfalse(state){
            
            state.loading = false;
        },
       
    },

});

export const {setLoadingfalse, setLoadingTrue} = loadingSlice.actions;

export default loadingSlice.reducer;

