import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import HamburgerMenu from './components/HamburgerMenu';
import LoginPage from './components/LoginPage';
import { useLocation } from "react-router-dom";
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
    const [loggedIn, setLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);

    const handleLoad = () => {
        const tokenManager = TokenManager();
        if (tokenManager.amILogged()) {
            setLoggedIn(true);
            tokenManager.amIManager();
        }
        setLoading(false);
    };

    useEffect(() => {
        handleLoad();
    }, []);

    if (!loading) {
        return (
            <Router>
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
            </Router >
        );
    }
}

export default App;
