import { Card, Col, Container, Row } from "react-bootstrap";
import { Chat } from "../components";
import { useContext, useEffect, useState } from "react";
import UserContext from "../context/UserContext";
import "../styles/TicketPage.css";
import API from "../API";

function MyTicketList(props){
    const {user,setUser}=useContext(UserContext);
    return(
        <div className="ticket-list">
            {
                props.ticketList.map(tick=>{
                    return(
                        <Card border={tick===props.selectedTicket?"info":"dark"} onClick={e=>{
                            e.preventDefault();
                            e.stopPropagation();
                            props.setSelectedTicket(tick==props.selectedTicket?undefined:tick);
                        }} className="ticket-card my-2">
                            <Card.Body>
                                Ticket for {tick.product.name} created at {tick.creationDate}{user.role!=='CUSTOMER' && " by "+tick.profile.username}
                            </Card.Body>
                        </Card>
                    )
                })
            }
        </div>
    )
}

function TicketPage(props){
    const {user,setUser}=useContext(UserContext);
    const [refresh,setRefresh]=useState(false);
    const [ticketList,setTicketList]=useState([
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
    ]);
    const [selectedTicket,setSelectedTicket]=useState(undefined);
    /*useEffect(()=>{
        const getTickets=async()=>{
            try {
                const ticks=await API.getTickets();
                setTicketList(ticks);
            } catch (error) {
                setTicketList([]);

            }
        }
    },[refresh]);*/
    return(
        <Container fluid className="ticket-page">
            <Row>
                <Col xs={selectedTicket? 4 : 12}>
                    <MyTicketList ticketList={ticketList} setTicketList={setTicketList} selectedTicket={selectedTicket} setSelectedTicket={setSelectedTicket}/>
                </Col>
                {selectedTicket && <Col xs={8}>
                    <Chat ticket={selectedTicket} refresh={refresh} setRefresh={setRefresh}/>
                </Col>}
            </Row>
        </Container>
    )
}

export default TicketPage;