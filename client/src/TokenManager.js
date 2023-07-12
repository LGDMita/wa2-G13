import jwt_decode from "jwt-decode";

const TokenManager = () => {
    let token = null;

    const setAuthToken = (newToken) => {
        token = newToken;
        // Salva il token nel localStorage
        localStorage.setItem('jwtToken', newToken);
    };

    const getAuthToken = () => {
        // Ritorna il token memorizzato nella variabile di stato
        return token;
    };

    const amILogged = () => {
        if (token) {
            let decodedToken = jwt_decode(token);
            let currentDate = new Date();
            if (decodedToken.exp * 1000 < currentDate.getTime()) {
                removeAuthToken();
                return false;
            } else {
                return true;
            }
        }
        else {
            return false
        }
    };

    const amIManager = () => {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.realm_access.roles.includes("app_manager");
    }

    const amIExpertOrManager = () => {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.realm_access.roles.includes("app_manager") || payload.realm_access.roles.includes("app_expert");
    }

    const removeAuthToken = () => {
        localStorage.removeItem("jwtToken");
        token = null;
        storedToken = null;
    }

    // Controlla se c'è un token salvato nel localStorage al caricamento della pagina
    let storedToken = localStorage.getItem('jwtToken');

    if (storedToken) {
        token = storedToken;
    }

    return {
        setAuthToken,
        getAuthToken,
        amILogged,
        removeAuthToken,
        amIManager,
        amIExpertOrManager
    };
};

export default TokenManager;
