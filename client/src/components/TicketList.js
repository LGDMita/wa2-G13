import API from '../API';
import React, {useContext, useEffect, useState} from 'react';
import Table from 'react-bootstrap/Table';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button'
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

import TokenManager from '../TokenManager';
import UserContext from "../context/UserContext";

function TicketList() {

    const [tickets, setTickets] = useState([]);
    const [filterOption, setFilterOption] = useState('all');
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();


    async function load(){
        setTickets(await API.getTickets());
    }

    useEffect(() => {
        if (!user.logged) {
            navigate('/home');
        } else void load();
    }, [user.logged, user.role, navigate]);

    const handleOptionChange = (event) => {
        setFilterOption(event.target.value);
    };

    if(user.role === "manager") {
        return (
            <div className="table-products">
                <Form>
                    <Form.Check
                        inline
                        label="All tickets"
                        name="group1"
                        type={'radio'}
                        value={'all'}
                        checked={filterOption === 'all'}
                        onChange={handleOptionChange}
                    />
                    <Form.Check
                        inline
                        label="Open tickets"
                        name="group1"
                        type={'radio'}
                        value={'open'}
                        checked={filterOption === 'open'}
                        onChange={handleOptionChange}
                    />
                </Form>
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>PROFILE USERNAME</th>
                        <th>PROFILE EMAIL</th>
                        <th>PRODUCT BRAND</th>
                        <th>PRODUCT NAME</th>
                        <th>STATUS</th>
                        <th>CREATION DATE</th>
                        {filterOption === 'all' ?
                                <><th>PRIORITY LEVEL</th>
                                <th>EXPERT USERNAME</th>
                                <th>EXPERT EMAIL</th></>
                            : null}
                        <th>ASSIGN</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        tickets
                            .filter(t => filterOption === 'open' ? t.status === 'open' : true)
                            .map(t => {
                                return (
                                    <tr key={t.ticketId}>
                                        <td>{t.profile.username}</td>
                                        <td>{t.profile.email}</td>
                                        <td>{t.product.brand}</td>
                                        <td>{t.product.name}</td>
                                        <td>{t.status}</td>
                                        <td>{t.creationDate}</td>
                                        {filterOption === 'all' ?
                                            <><td>{t.priorityLevel}</td>
                                            <td>{t.expert.username}</td>
                                            <td>{t.expert.email}</td></>
                                            : null
                                        }
                                        <td>
                                            <Link to={`/tickets/manage/${t.ticketId}`}>
                                                <Button>Go</Button>
                                            </Link>
                                        </td>
                                    </tr>
                                )
                            })
                    }
                    </tbody>
                </Table>
            </div>
        );
    }

}

export {TicketList}