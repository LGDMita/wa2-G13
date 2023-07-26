import API from '../API';
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import TableWithFilterAndSort from "./TableWithFilterAndSort"
import { Col, Container, Row, Spinner } from 'react-bootstrap';

import UserContext from "../context/UserContext";

function TicketList() {

    const [tickets, setTickets] = useState([]);
    const { user } = useContext(UserContext);
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);

    async function load() {
        await API.getTickets().then(res => {
            setTickets(
                res.map(t => {
                    return {
                        ticket_id: t.ticketId,
                        creation_date: reformatDate(t.creationDate),
                        status: t.status.replace(/_/g, " "),
                        customer_email: t.profile.email,
                        customer_name: t.profile.name,
                        customer_surname: t.profile.surname,
                        customer_username: t.profile.username,
                        product_brand: t.product.brand,
                        product_name: t.product.name,
                        product_ean: t.product.ean,
                        product_sector: t.product.sector.name.replace(/_/g, " "),
                        expert_email: t.expert ? t.expert.email : null,
                        expert_name: t.expert ? t.expert.name : null,
                        expert_surname: t.expert ? t.expert.surname : null,
                        expert_username: t.expert ? t.expert.username : null,
                        priority_level: (t.priorityLevel || t.priorityLevel === 0) ? t.priorityLevel : null,
                    }
                }))
        })
        //setTickets(await API.getTickets());
    }

    useEffect(() => {
        if (!user.logged && user.role !== 'manager') {
            navigate('/home');
        } else {
            load();
            setLoading(false);
        }
    }, [user.logged, user.role, navigate]);

    const reformatDate = (dateString) => {
        let dateObj = new Date(dateString);

        let year = dateObj.getFullYear();
        let month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
        let day = ("0" + dateObj.getDate()).slice(-2);
        let hours = ("0" + dateObj.getHours()).slice(-2);
        let minutes = ("0" + dateObj.getMinutes()).slice(-2);

        return `${year}-${month}-${day}, ${hours}:${minutes}`;
    }

    if (loading) {
        return (<Container fluid>
            <Row>
                <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
            </Row>
        </Container>);
    }
    else {
        return (
            <div className='productTable-cnt'>
                <Row>
                    <Col>
                        <h4 className='text-center'>Here you can find the list of all tickets</h4>
                        <Container className='productTable' style={{ fontSize: "11pt" }}>
                            <TableWithFilterAndSort data={tickets.length > 0 ? tickets : []} columns={['ticket_id', 'creation_date', 'status', 'customer_email', 'product_ean', 'product_sector', 'expert_email', 'priority_level', 'manage', 'history']} actionLinks={['/tickets/manage/', '/tickets/history/']} />
                        </Container>
                    </Col>
                </Row>
            </div >
        );
    }
}

export default TicketList;