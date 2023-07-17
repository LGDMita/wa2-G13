import React from 'react';
import { useEffect } from "react";
import { Navigate } from 'react-router-dom';
import TokenManager from '../TokenManager';

export default function Logout(props) {
    const tokenManager = TokenManager();

    useEffect(() => {
        tokenManager.removeAuthToken();
    }, [tokenManager]);

    return (<Navigate to="/login" />);
};