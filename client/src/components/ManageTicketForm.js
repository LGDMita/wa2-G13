import React, {useContext, useEffect, useState} from 'react';
import UserContext from "../context/UserContext";
import { useNavigate } from 'react-router-dom';
import API from "../API";

function ManageTicketForm() {
    const [ticket, setTicket] = useState({});
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();

    async function load(){
        setTicket(await API.getTickets());
    }

    useEffect(() => {
        if (!user.logged && user.role !== 'manager') {
            navigate('/home');
        } else void load();
    }, [user.logged, user.role, navigate]);
}

export {ManageTicketForm}