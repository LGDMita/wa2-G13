import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import UserContext from './context/UserContext';
import Header from './components/Header';
import { useState } from 'react';
import { Navigate, Route, Routes,useLocation } from 'react-router-dom';
import { HomepagePage,LoginPage, PurchasesPage, TicketPage,ExpertsPage } from './pages';
import TokenManager from './TokenManager';
import { ProductTable } from "./components/ProductTable";
import Logout from "./components/Logout";

function CheckHeader(props) {
    const location = useLocation();

    if (location.pathname.includes("/login")) {
        return null; // Hide the menu on the login page
    }

    return <Header handleLogout={props.handleLogout} />;
}

function App() {
    const tokenManager = TokenManager();
    const [user, setUser] = useState(tokenManager.retrieveUser());
    /*     const [loading, setLoading] = useState(true);
    
        useEffect(() => {
            setLoading(false);
        }, []); */

    const handleLogout = () => {
        tokenManager.removeAuthToken();
        setUser(tokenManager.retrieveUser());
    }

    return (
        <UserContext.Provider value={{ user, setUser }}>
            <CheckHeader handleLogout={handleLogout} />
            <Routes>
                <Route index element={<Navigate replace to='/home'/>}/>
                <Route path="/home" element={<HomepagePage/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/logout" element={<Logout />} />;
                <Route path="/tickets" element={<TicketPage/>} />;
                <Route path="/products" element={<ProductTable/>}/>;
                <Route path="/purchases" element={<PurchasesPage/>}/>;
                <Route path='/experts' element={<ExpertsPage />} />;
            </Routes>
        </UserContext.Provider>
    );
}

export default App;
