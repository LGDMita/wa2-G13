// TokenManager.js

import { useState, useEffect } from 'react';

const TokenManager = () => {
    const [token, setToken] = useState(null);

    const setAuthToken = (newToken) => {
        setToken(newToken);
        // Salva il token nel localStorage
        localStorage.setItem('jwtToken', newToken);
    };

    const getAuthToken = () => {
        // Ritorna il token memorizzato nella variabile di stato
        return token;
    };

    useEffect(() => {
        // Controlla se c'è un token salvato nel localStorage al caricamento della pagina
        const storedToken = localStorage.getItem('jwtToken');

        if (storedToken) {
            setToken(storedToken);
        }
    }, []);

    return { setAuthToken, getAuthToken };
};

export default TokenManager;
