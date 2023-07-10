import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HamburgerMenu from './components/HamburgerMenu';
import {LoginPage as LoginPage_} from './components/LoginPage';
import { useLocation } from "react-router-dom";

import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState,useEffect } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { HomepagePage,LoginPage } from './pages';

function HomePage() {
    return <h1>Homepage</h1>;
}

function ContattiPage() {
    return <h1>Pagina Contatti</h1>;
}

function LocationBasedHamburgerMenu() {
    const location = useLocation();

    if (location.pathname.includes("/login")) {
        return null; // Hide the menu on the login page
    }

    return <HamburgerMenu />;
}

function App() {
    const [user,setUser]=useState({logged:false,role:undefined,username:'',pwd:''});
    /*<div className="main-div"> --> to have different main div*/
    return (
        <UserContext.Provider value={{user,setUser}}>
            <Header></Header>
            <Routes>
                <Route index element={<Navigate replace to='/home'/>}/>
                <Route path="/home" element={<HomepagePage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/contatti" element={<ContattiPage />} />
                <Route path="/login_" element={<LoginPage_ />} />
            </Routes>
        </UserContext.Provider>
    );
}

export default App;
