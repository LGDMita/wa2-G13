import { Card, Col, Container, Row } from "react-bootstrap";
import { Chat } from "../components";
import { useContext, useEffect, useState } from "react";
import UserContext from "../context/UserContext";
import "../styles/TicketPage.css";
import API from "../API";
import { useLocation } from "react-router";

function MyTicketList(props) {
    const { user } = useContext(UserContext);
    return (
        <div className="ticket-list">
            <span className="material-icons-round md-36 purchase-dropopen" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.setOpenList(!props.openList);
            }}>
                keyboard_double_arrow_{props.openList ? "up" : "down"}
            </span>
            {
                props.openList && props.ticketList.map(tick => {
                    return (
                        <Card key={tick.ticketId} border={tick.ticketId === props.selectedTicket ? "info" : "dark"}
                            onClick={e => {
                                e.preventDefault();
                                e.stopPropagation();
                                // single route
                                props.setSelectedTicket(tick.ticketId === props.selectedTicket ? -1 : tick.ticketId);
                                // route when select
                                //navigate("/tickets/"+tick.ticketId);
                            }} className="ticket-card my-2">
                            <Card.Body>
                                Ticket for {tick.product.name} created
                                at {tick.creationDate}{user.role !== 'customer' && " by " + tick.profile.username}
                            </Card.Body>
                        </Card>
                    )
                })
            }
        </div>
    )
}

//single route
function TicketPage(props) {
    // set of tickets for frontend testing purposes
    /* const frontendTestTickets = [
        {
            ticketId: 1,
            creationDate: "29/09/1999",
            status: "open",
            profile: {
                username: "client",
            },
            product: {
                name: "Iphone X",
                brand: "Apple",
            },
        }, {
            ticketId: 2,
            creationDate: "30/09/1999",
            status: "IN PROGRESS",
            profile: {
                username: "client",
            },
            product: {
                name: "Samsung Galaxy 8",
                brand: "Samsung",
            },
        }, {
            ticketId: 3,
            creationDate: "10/10/1999",
            status: "IN PROGRESS",
            profile: {
                username: "client",
            },
            product: {
                name: "Toaster 2000",
                brand: "Toastcompany",
            },
        }, {
            ticketId: 4,
            creationDate: "16/11/1999",
            status: "CLOSED",
            profile: {
                username: "client",
            },
            product: {
                name: "Sony Bravia",
                brand: "Sony",
            },
        }
    ]; */
    const { user } = useContext(UserContext);
    const [refresh, setRefresh] = useState(false);
    const [ticketList, setTicketList] = useState([]);
    const location = useLocation();
    const [openList,setOpenList]=useState(true);
    // single route
    const [selectedTicket, setSelectedTicket] = useState(location.state ? location.state : -1);
    // to clear location state without rerender
    window.history.replaceState({}, document.title);
    // instead if we use a specific route when the ticket is opened
    //ticketList.find(tick=>tick.ticketId===props.selectedTicketId));

    useEffect(() => {
        const getTickets = async () => {
            try {
                const ticks = await API.getTicketsOf(user.id, user.role);
                setTicketList([...ticks]);
            } catch (error) {
                setTicketList([]);

            }
        }
        getTickets();
    }, [refresh]);

    if (ticketList && ticketList.length > 0) {
        return (
            <Container fluid className="ticket-page ticketList-cnt">
                <h4 className='text-center'>Here you can find the list of your tickets</h4><br />
                <Row>
                    <Col xs={12} md={selectedTicket !== -1 ? 4 : 12}>
                        <MyTicketList ticketList={ticketList} setTicketList={setTicketList} selectedTicket={selectedTicket}
                            setSelectedTicket={setSelectedTicket} openList={openList} setOpenList={setOpenList}/>
                    </Col>
                    {ticketList.find(t => t.ticketId === selectedTicket) && <Col xs={12} md={8}>
                        <Chat ticket={ticketList.find(t => t.ticketId === selectedTicket)} refresh={refresh}
                            setRefresh={setRefresh} />
                    </Col>}
                </Row>
            </Container>
        )
    }
    else {
        return (
            <Container className="ticketList-cnt-void">
                <h2 className='text-center'>Still no tickets</h2>
                <h5 className='text-center'>When a ticket will be opened, you will see it here.</h5>
            </Container>
        );
    }
}

export default TicketPage;