import Product from "./product";
import Profile from "./profile";

const SERVER_URL = 'http://localhost:8080';

const getProducts = async () => {
    const response = await fetch(`${SERVER_URL}/products/`)
    const rows = await response.json()
    if (response.ok) {
        return rows.map(row => {
            return new Product(row.ean, row.name, row.brand)
        });
    }
    else
        throw new Error(rows.detail);
};

const getProduct = async (ean) => {
    const response = await fetch(`${SERVER_URL}/products/${ean}`);
    const row = await response.json();
    if (response.ok) {
        return new Product(row.ean, row.name, row.brand)
    }
    else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const getUserInfo = async (email) => {
    const response = await fetch(`${SERVER_URL}/profiles/${email}`);
    const row = await response.json();
    if (response.ok) {
        return new Profile(row.email, row.name, row.surname)
    }
    else {
        throw new Error(`${response.status} - ${row.detail}`);
    }
};

const addProfile = async (profile) => {
    const response = await fetch(`${SERVER_URL}/profiles/`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({'email': profile.email, 'name': profile.name, 'surname': profile.surname})
    });
    if (!response.ok) {
        const row = await response.json();
        throw new Error(`${response.status} - ${row.detail}`);
    }
}

const updateProfile = async (email, profile) => {
    const response = await fetch(`${SERVER_URL}/profiles/${email}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({'email': profile.email, 'name': profile.name, 'surname': profile.surname})
    });
    if (!response.ok) {
        const row = await response.json();
        throw new Error(`${response.status} - ${row.detail}`);
    }
}


const API = {
    getProducts,
    getProduct,
    getUserInfo,
    addProfile,
    updateProfile
};

export default API;
