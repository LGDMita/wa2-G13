import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HamburgerMenu from './components/HamburgerMenu';

function HomePage() {
    return <h1>Homepage</h1>;
}

function ContattiPage() {
    return <h1>Pagina Contatti</h1>;
}

function App() {
    return (
        <Router>
            <HamburgerMenu />
            <Routes>
                <Route path="/" exact component={HomePage} />
                <Route path="/contatti" component={ContattiPage} />
            </Routes>
        </Router>
    );
}

export default App;
