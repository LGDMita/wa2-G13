import API from '../API';
import React, {useEffect, useState} from 'react';
import Table from 'react-bootstrap/Table';


function TicketList() {

    const [tickets, setTickets] = useState([]);
    async function load(){
        setTickets(await API.getTickets());
    }
    useEffect(() => {
        void load();
    }, []);

    return (
        <div className="table-products">
            <h3>All tickets</h3>
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
                </tr>
                </thead>
                <tbody>
                {
                    tickets.map(p => {
                        return (
                            <tr key={p.ticketId}>
                                <td>{p.profile.username}</td>
                                <td>{p.profile.email}</td>
                                <td>{p.product.brand}</td>
                                <td>{p.product.name}</td>
                                <td>{p.product.priorityLevel}</td>
                                <td>{p.status}</td>
                                <td>{p.creationDate}</td>
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