import { useContext, useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Modal, Row } from "react-bootstrap";
import dayjs from 'dayjs';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
import "../styles/PurchasesPage.css";
import { useNavigate } from "react-router";
import UserContext from "../context/UserContext";
import API from "../API";
import time from "../lib/time";
import { Spinner } from "react-bootstrap";

dayjs.extend(isSameOrBefore);

function PurchaseSelection(props) {
    // set of tickets for frontend testing purposes
    /*     const frontendTestPurchaseTickets = [
            {
                ticketId: 1,
                creationDate: "29/09/1999",
                status: "OPEN",
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
    const { user } = useContext(UserContext)
    const [ticketsOfPurchase, setTicketsOfPurchase] = useState([]);
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const handleNewTicket = async () => {
        try {
            //setError("Test test");
            const tick = await API.newTicket(props.purchase.product.ean);
            navigate("/tickets", { state: tick.ticketId });
        } catch (error) {
            setError(error.detail);
        }
    }

    useEffect(() => {
        const getTickets = async () => {
            try {
                const ticks = await API.getTicketsOfCustomerOfPurchase(user.id, props.purchase.product.ean);
                setTicketsOfPurchase(ticks);
            } catch (error) {
                setTicketsOfPurchase([]);
            }
        }
        getTickets();
    }, [user.id, props.purchase.product.ean])

    return (
        <div className="purchase-bodyopened">
            {ticketsOfPurchase.map(tick => {
                return (
                    <Card border={tick === props.selectedTicket ? "info" : "dark"} key={tick.ticketId} onClick={e => {
                        e.preventDefault();
                        e.stopPropagation();
                        props.setSelectedTicket(tick === props.selectedTicket ? undefined : tick);
                    }} className="ticket-card my-2">
                        <Card.Body>
                            Ticket created at {time.format(tick.creationDate)}
                            <span class="material-icons-round md-36 purchase-link" onClick={e => {
                                e.preventDefault();
                                e.stopPropagation();
                                // more routes
                                //navigate("/tickets/"+tick.ticketId);
                                // single route, communication between pages
                                navigate("/tickets", { state: tick.ticketId });
                            }}>
                                arrow_forward_ios
                            </span>
                        </Card.Body>
                    </Card>
                );
            })}
            <Button className="justify-content-center" disabled={!props.isWarrantyValid}
                variant={props.isWarrantyValid ? "outline-success" : "outline-danger"} onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    handleNewTicket();
                }}>Open a new ticket!</Button>
            {error !== "" &&
                <Modal show={true} onHide={() => setError("")} size="md" aria-labelledby="contained-modal-title-vcenter"
                    centered>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">Couldn't open a new ticket!</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>{error}</Modal.Body>
                </Modal>
            }
        </div>
    )
}

function PurchasesPage(props) {
    // set of purchases for frontend testing purposes
    /* const frontendTestPurchases = [
        {
            product: {
                ean: "1234567891012",
                name: "Iphone X",
            },
            datetime: "11/09/2012",
            warranty: {
                datetimeExpire: "4/09/2032",
            }
        }
    ] */
    const [purchases, setPurchases] = useState([]);
    const [selectedPurchase, setSelectedPurchase] = useState(undefined);
    const { user } = useContext(UserContext);
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);

    if (user.role !== "customer") navigate("/home");
    useEffect(() => {
        const getPurchases = async () => {
            try {
                if (user.role !== 'customer') {
                    navigate('/home');
                } else {
                    const purchs = await API.getPurchasesOf();
                    setPurchases(purchs);
                    setLoading(false);
                }
            } catch (error) {
                setPurchases([]);
            }
        }
        getPurchases();
    }, [navigate, user.role]);

    if (loading) {
        return (<Container fluid>
            <Row>
                <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
            </Row>
        </Container>);
    }
    else {
        if (purchases && purchases.length > 0) {
            return (
                <Container className="purchase-cnt">
                    <h4 className='text-center'>Here you can find the list of your purchases</h4><br />
                    <div className="purchase-list justify-content-center">
                        {purchases.map((purch) => {
                            const isWarrantyValid = purch.warranty ? time.isStillValid(purch.warranty.datetimeExpire) : false;
                            return (
                                <Card key={purch.product.ean} border={purch === selectedPurchase ? "info" : "dark"} className="purchase-card my-2">
                                    <Card.Header>
                                        <Row>
                                            <Col xs={8}>
                                                <Card.Title>{purch.product.name}</Card.Title>
                                            </Col>
                                            <Col xs={4}>
                                                <div className="purchase-status">
                                                    <Badge pill bg={isWarrantyValid ? "success" : "danger"}>
                                                        {isWarrantyValid ? "Warranty still valid until " + time.format(purch.warranty.datetimeExpire) : "No warranty on the product!"}
                                                    </Badge>
                                                </div>
                                            </Col>
                                        </Row>
                                    </Card.Header>
                                    <Card.Body>
                                        Purchased in {time.format(purch.datetime)}
                                        {selectedPurchase === purch &&
                                            <PurchaseSelection purchase={purch} isWarrantyValid={isWarrantyValid} />
                                        }
                                        <div className="purchase-footer">
                                            <span className="material-icons-round md-36 purchase-dropopen" onClick={e => {
                                                e.preventDefault();
                                                e.stopPropagation();
                                                setSelectedPurchase(purch === selectedPurchase ? undefined : purch);
                                            }}>
                                                keyboard_double_arrow_{selectedPurchase === purch ? "up" : "down"}
                                            </span>
                                        </div>
                                    </Card.Body>
                                </Card>
                            )
                        })
                        }
                    </div>
                </Container>
            );
        }
        else {
            return (
                <Container className="purchase-cnt-void">
                    <h2 className='text-center'>Still no purchases</h2>
                    <h5 className='text-center'>When a purchase will be registered to your account you will see it here.</h5>
                </Container>
            );
        }
    }
}

export default PurchasesPage;