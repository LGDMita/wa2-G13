import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import HamburgerMenu from './components/HamburgerMenu';
import { LoginPagee } from './components';
import { useLocation } from "react-router-dom";

import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState,useEffect } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { Chat, HomepagePage,LoginPage } from './pages';
import TokenManager from './TokenManager';


function App() {
    const tokenManager = TokenManager();
    const [user,setUser]=useState(tokenManager.retrieveUser());
    /*const [loading, setLoading] = useState(true);

    const handleLoad = () => {
        tokenManager.amILogged();
        const retr=tokenManager.retrieveUser();
        console.log("Just retr "+retr);
        console.log("Json str "+JSON.stringify(retr));
        setUser(retr);
        setLoading(false);
    };

    useEffect(() => {
        handleLoad();
    }, []);*/

    /*<div className="main-div"> --> to have different main div*/
    //if(!loading)
    return (
        <UserContext.Provider value={{user,setUser}}>
            <Header></Header>
            <Routes>
                <Route index element={<Navigate replace to='/home'/>}/>
                <Route path="/home" element={<HomepagePage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/tickets/:ticketId" element={<Chat/>}/>
            </Routes>
        </UserContext.Provider>
    );
}

export default App;
