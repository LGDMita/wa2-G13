import React, {useContext, useEffect, useState} from 'react';
import UserContext from "../context/UserContext";
import { useNavigate, useParams, Link } from 'react-router-dom';
import API from "../API";
import Card from 'react-bootstrap/Card';
import ListGroup from 'react-bootstrap/ListGroup';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button'

function ManageTicketForm() {

    const [ticket, setTicket] = useState(null);
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();
    const {ticketId} = useParams();
    const [sectorExperts, setSectorExperts] = useState(null);
    const [selectedPriorityLevel, setSelectedPriorityLevel] = useState(null);
    const [selectedExpert, setSelectedExpert] = useState(null)

    async function load(){
        await API.getTicket(ticketId).then(res => {
            setTicket(res);
            if(res.expert) setSelectedExpert(res.expert.id);
            if(res.priorityLevel) setSelectedPriorityLevel(res.priorityLevel);
            API.getExpertsBySector(res.product.sector.name).then(res => setSectorExperts(res))
        });
    }

    useEffect(() => {
        if (!user.logged && user.role !== 'manager') {
            navigate('/home');
        } else void load()
    }, [navigate, user.logged, user.role]);


    const handlePriorityChange = (event) => {
        setSelectedPriorityLevel(event.target.value);
    };

    const handleExpertChange = (event) => {
        setSelectedExpert(event.target.value);
    };

    async function handleSave(){
        let changing_expert = false;
        let changing_priority = false
        if(selectedExpert !== null && selectedExpert !== 'null') {
            changing_expert = true;
        }
        if(selectedPriorityLevel !== null && selectedPriorityLevel !== 'null') {
            changing_priority = true;
        }
        if(ticket.status === 'open' && changing_expert && changing_priority) {
            await API.changePriorityLevel(ticketId, parseInt(selectedPriorityLevel));
            await API.changeExpert(ticketId, selectedExpert);
            await API.changeStatus(ticketId, "in_progress");
        }
    }

    if(!ticket || !sectorExperts)
        return <div>Loading...</div>

    return (
        <Card style={{ width: '18rem' }}>
            <ListGroup variant="flush">
                <ListGroup.Item>Customer: {ticket.profile.username}</ListGroup.Item>
                <ListGroup.Item>Product Brand: {ticket.product.brand}</ListGroup.Item>
                <ListGroup.Item>Product Name: {ticket.product.name}</ListGroup.Item>
                <ListGroup.Item>Product Sector: {ticket.product.sector.name}</ListGroup.Item>
                <ListGroup.Item>Ticket Status: {ticket.status}</ListGroup.Item>
                <ListGroup.Item>Ticket Creation Date: {ticket.creationDate}</ListGroup.Item>
                <ListGroup.Item>Ticket Priority Level:
                    <Form.Select value = {selectedPriorityLevel ? selectedPriorityLevel.toString(): "null"} onChange={handlePriorityChange}>
                        <option value="null"></option>
                        <option value="0">0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                    </Form.Select>
                </ListGroup.Item>
                <ListGroup.Item>Expert:
                    <Form.Select value = {selectedExpert ? selectedExpert.toString(): "null"} onChange={handleExpertChange}>
                        <option value="null"></option>
                        {sectorExperts.map(exp => {
                            return <option value={exp.id.toString()}>{exp.username}</option>
                        })}
                    </Form.Select>
                </ListGroup.Item>
                <ListGroup.Item>
                    <Link to={'/tickets'} onClick={handleSave}>
                        <Button>Save</Button>
                    </Link>
                    <span>{' '}</span>
                    <Link to={'/tickets'}>
                        <Button>Cancel</Button>
                    </Link>
                </ListGroup.Item>
            </ListGroup>
        </Card>
    );
}

export {ManageTicketForm}