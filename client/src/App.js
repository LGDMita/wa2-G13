import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import {TicketList} from "./components/TicketList";
import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { HomepagePage,LoginPage, PurchasesPage, TicketPage } from './pages';
import TokenManager from './TokenManager';
import {ProductTable} from "./components/ProductTable";


function App() {
    const tokenManager = TokenManager();
    const [user,setUser]=useState(tokenManager.retrieveUser());
    const handleLogout = ()=>{
        tokenManager.removeAuthToken();
        setUser(tokenManager.retrieveUser());
    }
    return (
        <UserContext.Provider value={{user,setUser}}>
            <Header handleLogout={handleLogout}/>
            <Routes>
                <Route index element={<Navigate replace to='/home'/>}/>
                <Route path="/home" element={<HomepagePage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/tickets" element={<TicketPage/>} />;
                <Route path="/products" element={<ProductTable/>}/>;
                <Route path="/purchases" element={<PurchasesPage/>}/>;
            </Routes>
        </UserContext.Provider>
    );
}

export default App;
