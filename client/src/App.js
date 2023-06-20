import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';


import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState,useEffect } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { HomepagePage,LoginPage } from './pages';

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
            </Routes>
        </UserContext.Provider>
    );
}

export default App;
