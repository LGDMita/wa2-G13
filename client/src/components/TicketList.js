import API from '../API';
import React, {useEffect, useState} from 'react';
import Table from 'react-bootstrap/Table';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button'
import { Link } from 'react-router-dom';

import TokenManager from '../TokenManager';

function TicketList() {

    const [tickets, setTickets] = useState([]);
    const [filterOption, setFilterOption] = useState('all');

    async function load(){
        setTickets(await API.getTickets());
    }
    useEffect(() => {
        void load();
    }, []);

    const handleOptionChange = (event) => {
        setFilterOption(event.target.value);
    };

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
                    <th>PRIORITY LEVEL</th>
                    <th>STATUS</th>
                    <th>CREATION DATE</th>
                    <th>ASSIGN</th>
                </tr>
                </thead>
                <tbody>
                {
                    tickets
                        .filter(t => filterOption === 'open' ? t.status === 'open': true)
                        .map(t => {
                            return (
                                <tr key={t.ticketId}>
                                    <td>{t.profile.username}</td>
                                    <td>{t.profile.email}</td>
                                    <td>{t.product.brand}</td>
                                    <td>{t.product.name}</td>
                                    <td>{t.product.priorityLevel}</td>
                                    <td>{t.status}</td>
                                    <td>{t.creationDate}</td>
                                    <td>
                                        <Button>
                                            <Link to={`/tickets/manage/${t.ticketId}`}>
                                                Go
                                            </Link>
                                        </Button>
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

export {TicketList}