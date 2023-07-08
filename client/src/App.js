import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HamburgerMenu from './components/HamburgerMenu';
import LoginPage from './components/LoginPage';
import { useLocation } from "react-router-dom";


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
    return (
        <Router>
            <LocationBasedHamburgerMenu />
            <Routes>
                <Route path="/" exact element={<HomePage />} />
                <Route path="/contatti" element={<ContattiPage />} />
                <Route path="/login" element={<LoginPage />} />
            </Routes>
        </Router>
    );
}

export default App;
