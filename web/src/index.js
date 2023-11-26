import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { createGlobalStyle } from 'styled-components';
import { Provider } from 'react-redux';
import store, { persistor } from "./redux/store";
import { PersistGate } from 'redux-persist/integration/react';

const GbStyle = createGlobalStyle`


html, body, div, span, applet, object, iframe,
h1, h2, h3, h4, h5, h6, p, blockquote, pre,
a, abbr, acronym, address, big, cite, code,
del, dfn, em, img, ins, kbd, q, s, samp,
small, strike, strong, sub, sup, tt, var,
b, u, i, center,
dl, dt, dd, ol, ul, li,
fieldset, form, label, legend,
table, caption, tbody, tfoot, thead, tr, th, td,
article, aside, canvas, details, embed, 
figure, figcaption, footer, header, hgroup, 
menu, nav, output, ruby, section, summary,
time, mark, audio, video {
	margin: 0;
	padding: 0;
	border: 0;
	font-size: 100%;
	font: inherit;
	vertical-align: baseline;
}
article, aside, details, figcaption, figure, 
footer, header, hgroup, menu, nav, section {
	display: block;
}

*{
    box-sizing: border-box;
}

a{
    color : inherit;
    text-decoration: none;
}

body {

	line-height: 1;
}
ol, ul {
	list-style: none;
}
blockquote, q {
	quotes: none;
}
blockquote:before, blockquote:after,
q:before, q:after {
	content: '';
	content: none;
}
table {
	border-collapse: collapse;
	border-spacing: 0;
}


:root{
	--navigation-width : 260px;
	--PreferBox-height : 550px;
	--PreferBox-ml : 7%;
	--SampleFixed-ml : 20%;

	--bgColor : #586BFF;
    --iconColor : #008EF5;
    --deepBlue : #0077B6;
    --fontBlue : #010043;
 }

@media only screen and (min-width : 1500px){
	:root{
		--PreferBox-ml : 15%;
		--SampleFixed-ml : 30%;
	 }
	
}


@media only screen and (min-height : 900px)  {
	:root{
		--PreferBox-height : 800px;
	 }
	}

`


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
	<Provider store={store}>
		<PersistGate  persistor={persistor}>
			<GbStyle/>
			<App />
		</PersistGate>
	</Provider>
);
