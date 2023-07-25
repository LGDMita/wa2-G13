import axios from 'axios';
import Product from "./product";
import Expert from "./expert";
import Profile from "./profile";
import Sector from "./sector";
import TokenManager from './TokenManager';
import Ticket from "./ticket";
import Manager from "./manager";

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
            tokenManager.removeAuthToken();
        }
        return Promise.reject(error);
    }
);

const getTickets = async () => {
    const response = await apiInstance.get('/API/tickets/');
    const rows = response.data;
    if (response.status === 200) {
        return rows.map(row => {
            return new Ticket(row.ticketId, row.profile, row.product, row.priorityLevel, row.expert, row.status, row.creationDate, row.messages);
        });
    } else {
        throw new Error(rows.detail);
    }
};

const getTicket = async (id) => {
    const response = await apiInstance.get(`/API/tickets/${id}`);
    const row = response.data;
    if (response.status === 200) {
        return new Ticket(row.ticketId, row.profile, row.product, row.priorityLevel, row.expert, row.status, row.creationDate, row.messages);
    } else {
        throw new Error(row.detail);
    }
};

const getExpertsBySector = async (sector) => {
    const response = await apiInstance.get(`/API/experts/?sectorName=${sector}`);
    const rows = response.data;
    if (response.status === 200) {
        return rows.map(row => {
            return new Expert(row.id, row.username, row.email, row.name, row.surname);
        });
    } else {
        console.log(rows)
        throw new Error(rows.detail);
    }
};

const getSectors = async () => {
    try {
        const response = await apiInstance.get(`/API/experts/sectors`);
        const rows = response.data;
        if (response.status === 200) {
            return rows.map(row => {
                return new Sector(row.sectorId, row.name);
            });
        } else {
            console.log(rows)
            throw new Error(rows.detail);
        }
    }
    catch (error) {
        console.log(error)
    }
};

const getExpertSectors = async (id) => {
    try {
        const response = await apiInstance.get(`/API/experts/${id}/sectors`);
        const rows = response.data;
        if (response.status === 200) {
            return rows.map(row => {
                return new Sector(row.sectorId, row.name);
            });
        } else {
            console.log(rows)
            throw new Error(rows.detail);
        }
    }
    catch (error) {
        console.log(error)
    }
};

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

const getProfileInfo = async (id) => {
    const response = await apiInstance.get(`/API/profiles/${id}`);
    const row = response.data;
    if (response.status === 200) {
        return new Profile(row.id, row.username, row.email, row.name, row.surname);
    } else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const getExpertInfo = async (id) => {
    const response = await apiInstance.get(`/API/experts/${id}`);
    const row = response.data;
    if (response.status === 200) {
        return new Expert(row.id, row.username, row.email, row.name, row.surname);
    } else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const getManagerInfo = async (id) => {
    const response = await apiInstance.get(`/API/managers/${id}`);
    const row = response.data;
    if (response.status === 200) {
        return new Manager(row.id, row.username, row.email, row.name, row.surname);
    } else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const signup = async (registrationData) => {
    try {
        await apiInstance.post('/API/signup', registrationData);
    } catch (error) {
        console.error('Error during signup:', error);
        throw new Error(error.response.status)
    }
};

const createExpert = async (registrationData) => {
    try {
        await apiInstance.post('/API/createExpert', registrationData);
    } catch (error) {
        console.error('Error while creating expert:', error);
        throw new Error(error.response.status)
    }
};

const modifyExpertWithSector = async (expertId, registrationData, sectorList) => {
    try {
        await apiInstance.put(`/API/modifyExpert/sectors/${expertId}`, {
            expertDTO: registrationData,
            sectorList: sectorList
        });
    } catch (error) {
        console.error('Error while editing expert:', error);
        throw new Error(error.response.status)
    }
};

const modifyProfile = async (id, profile) => {
    const response = await apiInstance.put(`/API/profiles/${id}`, profile);
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const modifyExpert = async (id, expert) => {
    const response = await apiInstance.put(`/API/experts/${id}`, expert);
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const modifyManager = async (id, manager) => {
    const response = await apiInstance.put(`/API/managers/${id}`, manager);
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const changeStatus = async (ticketId, status) => {
    const response = await apiInstance.put(`/API/tickets/${ticketId}/changeStatus`, { 'status': status });
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const changePriorityLevel = async (ticketId, priorityLevel) => {
    const response = await apiInstance.put(`/API/tickets/${ticketId}/changePriority`, { 'priorityLevel': priorityLevel });
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const changeExpert = async (ticketId, expertId) => {
    const response = await apiInstance.put(`/API/tickets/${ticketId}/changeExpert`, { 'expertId': expertId });
    if (response.status !== 200) {
        const row = response.data;
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const login = async (username, password, setUser) => {
    try {
        const response = await axios.post('/API/login', {
            username,
            password,
        });

        const token = response.data.jwtAccessToken;

        // Salva il token utilizzando il TokenManager
        tokenManager.setAuthToken(token);

        // Effettua ulteriori operazioni dopo il login, come la navigazione alla pagina successiva
        const user = tokenManager.getUser();
        //console.log("User is "+JSON.stringify(user));
        tokenManager.storeUser(user);
        setUser(user);
    } catch (error) {
        //console.error('Errore durante il login:', error);
        throw new Error(error.response.status)
    }
};

const sendMessage = async (ticketId, fromUser, text, files) => {
    const data = new FormData();
    data.append("fromUser", fromUser);
    data.append("text", text);
    files.forEach(f => data.append("attachments", f.file));
    const res = await apiInstance.postForm("/API/tickets/" + ticketId + "/messages", data)
    /*{
        fromUser:fromUser,
        text:text,
        attachments:files.map(f=>f.file)
    });*/
    console.log("Sent message, status ", res.status);
    if (res.status !== 200) {
        const ret = await res.data;
        console.log("Sent message ret ", ret);
        throw new Error({ status: res.status, detail: ret });
    }
}

const getMessages = async ticketId => {
    const res = await apiInstance.get("/API/tickets/" + ticketId + "/messages");
    const ret = await res.data;
    console.log("Messages ", ret, " status ", res.status);
    if (res.status !== 200) throw new Error({ status: res.status, detail: ret });
    else return ret;
}

const getTicketsOf = async (id, role) => {
    const queryParams = new URLSearchParams('?');
    let queryRole = "profile";
    switch (role) {
        case 'customer':
            break;
        case 'expert':
            queryRole = 'expert';
            break;
        case 'manager':
            queryRole = 'expert';
            break;
        default:
            break;
    }
    queryParams.append(queryRole + "Id", id);
    const url = "/API/tickets/?" + queryParams.toString();
    const res = await apiInstance.get(url);
    const ret = await res.data;
    //console.log("Tickets of status ",res.status,", details ",ret);
    if (res.status !== 200) throw new Error({ status: res.status, detail: ret });
    else return ret.map(row => new Ticket(row.ticketId, row.profile, row.product, row.priorityLevel, row.expert, row.status, row.creationDate, row.messages));
}

const getTicketsOfCustomerOfPurchase = async (customerId, productEan) => {
    const queryParams = new URLSearchParams('?');
    queryParams.append("profileId", customerId);
    queryParams.append("ean", productEan);
    const url = "/API/tickets/?" + queryParams.toString();
    const res = await apiInstance.get(url);
    const ret = await res.data;
    console.log("Url ", url, " Tickets purchase status ", res.status, ", details ", ret);
    if (res.status !== 200) throw new Error({ status: res.status, detail: ret });
    else return ret.map(row => new Ticket(row.ticketId, row.profile, row.product, row.priorityLevel, row.expert, row.status, row.creationDate, row.messages));
}

const newTicket = async (ean) => {
    const res = await apiInstance.post("/API/tickets", { ean: ean });
    const ret = await res.data;
    console.log("New ticket status ", res.status, ", details ", ret);
    if (res.status !== 201) throw new Error({ status: res.status, detail: ret });
    else return ret;
}

const getPurchasesOf = async () => {
    try {
        const res = await apiInstance.get("/API/customer/purchases");
        const ret = await res.data;
        if (res.status !== 200)
            throw new Error({ status: res.status, detail: ret });
        else return ret;
    } catch (error) {
        console.error('Error during fetching:', error);
        throw new Error(error.response.status)
    }
}

const changeTicketStatus = async (ticketId, newStatus) => {
    const res = await apiInstance.put("/API/tickets/" + ticketId + "/changeStatus", { status: newStatus });
    if (res.status !== 200) {
        const ret = await res.data;
        throw new Error({ status: res.status, detail: ret });
    }
}

const getTicketHistory = async ticketId => {
    const res = await apiInstance.get("/API/tickets/" + ticketId + "/history");
    const ret = await res.data;
    console.log("History returned status " + res.status, " and ret ", ret);
    if (res.status !== 200) throw new Error({ status: res.status, detail: ret });
    return ret;
}

const getExperts = async () => {
    const response = await apiInstance.get('/API/experts');
    const rows = response.data;
    if (response.status === 200) {
        return rows.map(row => {
            return new Expert(row.id, row.name, row.surname, row.email, row.username);
        });
    } else {
        throw new Error(rows.detail);
    }
};

const updateExpert = async (expert) => {
    try {
        await apiInstance.put(`/API/experts/${expert.id}`, expert);
    } catch (error) {
        throw new Error(error.response.data.detail);
    }
};

const getSectorsOfExpert = async (id) => {
    try {
        const response = await apiInstance.get(`/API/experts/${id}/sectors`)
        const rows = response.data;
        return rows.map(row => {
            return new Sector(row.sectorId, row.name);
        });
    } catch (error) {
        throw new Error(error.response.data.detail);
    }
}

const API = {
    getTickets,
    getTicket,
    getProducts,
    getProduct,
    getProfileInfo,
    getExpertsBySector,
    changePriorityLevel,
    changeStatus,
    changeExpert,
    getExpertInfo,
    getManagerInfo,
    signup,
    createExpert,
    modifyProfile,
    modifyExpert,
    modifyExpertWithSector,
    modifyManager,
    login,
    sendMessage,
    getMessages,
    getTicketsOf,
    getTicketsOfCustomerOfPurchase,
    newTicket,
    getPurchasesOf,
    changeTicketStatus,
    getTicketHistory,
    updateExpert,
    getExperts,
    getSectorsOfExpert,
    getSectors,
    getExpertSectors
};

export default API;