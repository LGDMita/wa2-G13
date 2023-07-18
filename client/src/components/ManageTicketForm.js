import React, {useContext, useEffect, useState} from 'react';
import UserContext from "../context/UserContext";
import { useNavigate, useParams, Link } from 'react-router-dom';
import API from "../API";
import Card from 'react-bootstrap/Card';
import ListGroup from 'react-bootstrap/ListGroup';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button'
import Table from 'react-bootstrap/Table';

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

    async function handleSave(event){
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
        }else{
            event.preventDefault();
            window.alert("Select both an expert and a priority level to save the ticket, otherwise just click 'Cancel' to go back to the tickets list")
        }
    }

    const removeUnderscoresAndCapitalize = (str) => {
        return str.replace(/_/g, " ").replace(/\b\w/g, (c) => c.toUpperCase());
    }

    const reformatDate = (dateString) => {
        let dateObj = new Date(dateString);

        let year = dateObj.getFullYear();
        let month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
        let day = ("0" + dateObj.getDate()).slice(-2);
        let hours = ("0" + dateObj.getHours()).slice(-2);
        let minutes = ("0" + dateObj.getMinutes()).slice(-2);

        return `${year}-${month}-${day}, ${hours}:${minutes}`;
    }

    if(!ticket || !sectorExperts)
        return <div>Loading...</div>

    return (
        <div className="table-products">
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Customer</th>
                    <td>{ticket.profile.username}</td>
                </tr>
                <tr>
                    <th>Product Brand</th>
                    <td>{ticket.product.brand}</td>
                </tr>
                <tr>
                    <th>Product Name</th>
                    <td>{ticket.product.name}</td>
                </tr>
                <tr>
                    <th>Product Sector</th>
                    <td>{removeUnderscoresAndCapitalize(ticket.product.sector.name)}</td>
                </tr>
                <tr>
                    <th>Ticket Status</th>
                    <td>{removeUnderscoresAndCapitalize(ticket.status)}</td>
                </tr>
                <tr>
                    <th>Ticket Creation Date</th>
                    <td>{reformatDate(ticket.creationDate)}</td>
                </tr>
                <tr>
                    <th>Ticket Priority Level</th>
                    <td><Form.Select value = {selectedPriorityLevel ? selectedPriorityLevel.toString(): "null"} onChange={handlePriorityChange}>
                        <option value="null"></option>
                        <option value="0">0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                    </Form.Select></td>
                </tr>
                <tr>
                    <th>Expert (only experts of the related sector can be selected):</th>
                    <td><Form.Select value = {selectedExpert ? selectedExpert.toString(): "null"} onChange={handleExpertChange}>
                        <option value="null"></option>
                        {sectorExperts.map(exp => {
                            return <option value={exp.id.toString()}>{exp.username}</option>
                        })}
                    </Form.Select></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <Link to={'/tickets'} onClick={handleSave}>
                             <Button>Save</Button>
                        </Link>
                        <span>{' '}</span>
                        <Link to={'/tickets'}>
                             <Button>Cancel</Button>
                        </Link>
                    </td>
                </tr>
                </thead>
            </Table>
        </div>
    );
}

export {ManageTicketForm}