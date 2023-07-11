import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import HamburgerMenu from './components/HamburgerMenu';
import {LoginPage as LoginPage_} from './components/LoginPage';
import { useLocation } from "react-router-dom";

import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState,useEffect } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { HomepagePage,LoginPage } from './pages';
import TokenManager from './TokenManager';

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

function Logout(props) {
    const tokenManager = TokenManager();

    useEffect(() => {
        tokenManager.removeAuthToken();
        props.setLoggedIn(false);
    }, [tokenManager, props.setLoggedIn]);

    return (<Navigate to="/login" />);
}

function App() {
    //const [user,setUser]=useState({logged:false,role:undefined,username:'',pwd:''});
    /*<div className="main-div"> --> to have different main div*/
    /*return (
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
    );*/
    const [loggedIn, setLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);

    const handleLoad = () => {
        const tokenManager = TokenManager();
        if (tokenManager.amILogged()) {
            setLoggedIn(true);
        }
        setLoading(false);
    };

    useEffect(() => {
        handleLoad();
    }, []);

    if (!loading) {
        return (
            <>
                <LocationBasedHamburgerMenu />
                <Routes>
                    <Route path="/" exact
                        element={!loggedIn ?
                            <Navigate to="/login" /> :
                            <HomePage />}
                    />
                    <Route path="/contatti" exact
                        element={
                            !loggedIn ?
                                <Navigate to="/login" /> :
                                <ContattiPage />}
                    />
                    <Route path="/login" element={<LoginPage setLoggedIn={setLoggedIn} />} />
                    <Route path="/logout" element={<Logout setLoggedIn={setLoggedIn} />} />
                </Routes>
            </ >
        );
    }
}

export default App;
