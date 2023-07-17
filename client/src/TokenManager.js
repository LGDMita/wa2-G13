import jwt_decode from "jwt-decode";

const TokenManager = () => {
    let token = null;

    const setAuthToken = (newToken) => {
        token = newToken;
        // Salva il token nel localStorage
        console.log(token)
        localStorage.setItem('jwtToken', newToken);
    };

    const getAuthToken = () => {
        // Ritorna il token memorizzato nella variabile di stato
        return token;
    };

    const amILogged = () => {
        //console.log("Am I logged?");
        if (token) {
            //console.log("I have token");
            let decodedToken = jwt_decode(token);
            let currentDate = new Date();
            if (decodedToken.exp * 1000 < currentDate.getTime()) {
                //console.log("Token expired, expiry date "+decodedToken.exp);
                removeAuthToken();
                return false;
            } else {
                //console.log("Token not expired");
                return true;
            }
        }
        else {
            return false
        }
    };

    const removeAuthToken = () => {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("userData");
        token = null;
        storedToken = null;
    }

    // Controlla se c'è un token salvato nel localStorage al caricamento della pagina
    let storedToken = localStorage.getItem('jwtToken');

    if (storedToken) {
        token = storedToken;
    }

    // get decoded token
    const getDecodedToken= () => token ? jwt_decode(token) : {id:'', logged:false, role:undefined, username:'', email: '', name: '', surname:''}

    const getUser= () => {
        if(token){
            const decodedToken= jwt_decode(token);
            let userRole="";
            switch (decodedToken.resource_access["spring-client"].roles[0]) {
                case "client":
                    userRole = "customer";
                    break;
                case "manager":
                    userRole = "manager";
                    break;
                case "expert":
                    userRole = "expert";
                    break;
                default:
                    break;
            }
            return {
                id: decodedToken.sub,
                logged: true,
                role: userRole,
                username: decodedToken.preferred_username,
                email: '',
                name: '',
                surname:'',
            }
        }
        return {id:'', logged:false, role:undefined, username:'', email: '', name: '', surname:''}
    }
    const storeUser = user => {
        localStorage.setItem("userData", JSON.stringify(user));
    }
    const retrieveUser= ()=>{
        if(token) return JSON.parse(localStorage.getItem("userData"));
        else return {id:'', logged:false, role:undefined, username:'', email: '', name: '', surname:''};
    }
    return { setAuthToken, getAuthToken, amILogged, removeAuthToken, getDecodedToken, getUser, storeUser, retrieveUser };
};

export default TokenManager;
