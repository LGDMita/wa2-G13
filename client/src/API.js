import axios from 'axios';
import Product from "./product";
import Profile from "./profile";
import TokenManager from './TokenManager';

const SERVER_URL = 'http://localhost:8080';

// Crea un'istanza di Axios
const apiInstance = axios.create({
    baseURL: SERVER_URL,
});

const tokenManager = TokenManager();

// Aggiungi un interceptor per le richieste
apiInstance.interceptors.request.use(
    (config) => {
        const token = tokenManager.getAuthToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Aggiungi un interceptor per le risposte
apiInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // Puoi gestire gli errori di autenticazione qui
        if (error.response && error.response.status === 401) {
            console.log('Errore di autenticazione. Effettua nuovamente il login.');
            // Esegui il logout o altre azioni di gestione dell'autenticazione
        }
        return Promise.reject(error);
    }
);

const getProducts = async () => {
    const response = await apiInstance.get('/API/products/');
    const rows = response.data;
    if (response.status === 200) {
        return rows.map(row => {
            return new Product(row.ean, row.name, row.brand);
        });
    } else {
        throw new Error(rows.detail);
    }
};

const getProduct = async (ean) => {
    const response = await apiInstance.get(`/API/products/${ean}`);
    const row = response.data;
    if (response.status === 200) {
        return new Product(row.ean, row.name, row.brand);
    } else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const getUserInfo = async (email) => {
    const response = await apiInstance.get(`/API/profiles/${email}`);
    const row = response.data;
    if (response.status === 200) {
        return new Profile(row.email, row.name, row.surname);
    } else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const addProfile = async (profile) => {
    const response = await apiInstance.post('/API/profiles', profile);
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const updateProfile = async (email, profile) => {
    const response = await apiInstance.put(`/API/profiles/${email}`, profile);
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const login = async (username, password, setUser) => {
    try {
        //console.log("Trying login API");
        const response = await axios.post('/API/login', {
            username,
            password,
        });

        const token = response.data.jwtAccessToken;

        // Salva il token utilizzando il TokenManager
        tokenManager.setAuthToken(token);
        
        // Effettua ulteriori operazioni dopo il login, come la navigazione alla pagina successiva
        const user=tokenManager.getUser();
        user.pwd=password;
        //console.log("User is "+JSON.stringify(user));
        tokenManager.storeUser(user);
        setUser(user);
    } catch (error) {
        //console.error('Errore durante il login:', error);
        throw new Error(error.response.status)
    }
};

const logout = async () =>{
    return null;
}

const API = {
    getProducts,
    getProduct,
    getUserInfo,
    addProfile,
    updateProfile,
    login,
    logout
};

export default API;
