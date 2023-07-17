import { Card, Col, Container, Row } from "react-bootstrap";
import { Chat } from "../components";
import { useContext, useEffect, useState } from "react";
import UserContext from "../context/UserContext";
import "../styles/TicketPage.css";
import API from "../API";
import { Route, Routes, useLocation, useNavigate, useParams } from "react-router";
import TicketHistory from "./TicketHistory";

function MyTicketList(props){
    const {user,setUser}=useContext(UserContext);
    //console.log("ticket list ",props.ticketList);
    // route when select
    //const navigate=useNavigate();
    return(
        <div className="ticket-list">
            {
                props.ticketList.map(tick=>{
                    return(
                        <Card key={tick.ticketId} border={tick.ticketId===props.selectedTicket?"info":"dark"} onClick={e=>{
                            e.preventDefault();
                            e.stopPropagation();
                            // single route
                            props.setSelectedTicket(tick.ticketId===props.selectedTicket?-1:tick.ticketId);
                            // route when select
                            //navigate("/tickets/"+tick.ticketId);
                        }} className="ticket-card my-2">
                            <Card.Body>
                                Ticket for {tick.product.name} created at {tick.creationDate}{user.role!=='customer' && " by "+tick.profile.username}
                            </Card.Body>
                        </Card>
                    )
                })
            }
        </div>
    )
}

//single route
function TicketPage(props){
//route different when selected ticket
//function TicketPageBody(props){
    // set of tickets for frontend testing purposes
    const frontendTestTickets=[
        {
            ticketId:1,
            creationDate:"29/09/1999",
            status:"OPEN",
            profile:{
                username:"client",
            },
            product:{
                name:"Iphone X",
                brand:"Apple",
            },
        },{
            ticketId:2,
            creationDate:"30/09/1999",
            status:"IN PROGRESS",
            profile:{
                username:"client",
            },
            product:{
                name:"Samsung Galaxy 8",
                brand:"Samsung",
            },
        },{
            ticketId:3,
            creationDate:"10/10/1999",
            status:"IN PROGRESS",
            profile:{
                username:"client",
            },
            product:{
                name:"Toaster 2000",
                brand:"Toastcompany",
            },
        },{
            ticketId:4,
            creationDate:"16/11/1999",
            status:"CLOSED",
            profile:{
                username:"client",
            },
            product:{
                name:"Sony Bravia",
                brand:"Sony",
            },
        }
    ];
    const {user,setUser}=useContext(UserContext);
    const [refresh,setRefresh]=useState(false);
    const [ticketList,setTicketList]=useState([]);
    const location=useLocation();
    // single route
    const [selectedTicket,setSelectedTicket]=useState(location.state?location.state:-1);
    // to clear location state without rerender
    window.history.replaceState({},document.title);
    // instead if we use a specific route when the ticket is opened
    //ticketList.find(tick=>tick.ticketId===props.selectedTicketId));
    useEffect(()=>{
        const getTickets=async()=>{
            try {
                const ticks=await API.getTicketsOf("032d838b-1b21-4154-8aac-315f2b0cc88c",user.role);
                setTicketList([...ticks]);
            } catch (error) {
                setTicketList([]);

            }
        }
        getTickets();
    },[refresh]);
    return(
        <Container fluid className="ticket-page">
            <Row>
                <Col xs={selectedTicket!==-1? 4 : 12}>
                    <MyTicketList ticketList={ticketList} setTicketList={setTicketList} selectedTicket={selectedTicket} setSelectedTicket={setSelectedTicket}/>
                </Col>
                {ticketList.find(t=>t.ticketId===selectedTicket) && <Col xs={8}>
                    <Chat ticket={ticketList.find(t=>t.ticketId===selectedTicket)} refresh={refresh} setRefresh={setRefresh}/>
                </Col>}
            </Row>
        </Container>
    )
}
/*
if we have different routes when ticket is selected
function TicketPageBodySelectedTicket(props){
    const {ticketId}=useParams();
    return(
        <TicketPageBody selectedTicketId={ticketId}/>
    )
}
*/
/*function TicketPage(props){
    return(
        <Routes>
            <Route index element={<TicketPageBody selectedTicketId={-1}/>}/>
            <Route path=":ticketId" element={<TicketPageBodySelectedTicket/>}/>
        </Routes>
    )
    return(
        <Container fluid className="ticket-page">
            <Row>
                <TicketHistory/>
            </Row>
        </Container>
    )
}*/

export default TicketPage;