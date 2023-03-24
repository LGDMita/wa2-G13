import { Product } from "./product";
import { Profile } from "./profile";

const SERVER_URL = 'http://localhost:8080';

const getProducts = async () => {
    const response = await fetch(`${SERVER_URL}/API/products/`);
    const rows = await response.json();
    if (response.ok) {
        return rows.map(row => {
            return new Product(row.ean, row.name, row.brand)
        });
    }
    else
        throw new Error(rows.msg);
};

const getProduct = async (ean) => {
    const response = await fetch(`${SERVER_URL}/API/products/${ean}`);
    const row = await response.json();
    if (response.ok) {
        return new Product(row.ean, row.name, row.brand)
    }
    else {
        throw new Error(`${response.status} - ${row.msg}`);
    }
};

const getUserInfo = async (email) => {
    const response = await fetch(`${SERVER_URL}/API/profiles/${email}`);
    const row = await response.json();
    if (response.ok) {
        return new Profile(row.email, row.name, row.surname)
    }
    else {
        throw new Error(`${response.status} - ${row.msg}`);
    }
};

const addProfile = async (profile) => {
    const response = await fetch(`${SERVER_URL}/API/profiles/`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ "profile": profile })
    });
    if (!response.ok) {
        const row = await response.json();
        throw new Error(`${response.status} - ${row.msg}`);
    }
}

const updateProfile = async (email) => {
    const response = await fetch(`${SERVER_URL}/API/profiles/`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ "email": email })
    });
    if (!response.ok) {
        const row = await response.json();
        throw new Error(`${response.status} - ${row.msg}`);
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
