

import { useContext, useEffect, useState } from "react";
import { Badge, Button, Card, Col, Modal, Row } from "react-bootstrap";
import dayjs from 'dayjs';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
import "../styles/PurchasesPage.css";
import { useNavigate } from "react-router";
import UserContext from "../context/UserContext";
import API from "../API";
import time from "../lib/time";
dayjs.extend(isSameOrBefore);

function PurchaseSelection(props){
    // set of tickets for frontend testing purposes
    const frontendTestPurchaseTickets=[
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
    const {user,setUser}=useContext(UserContext)
    const [ticketsOfPurchase,setTicketsOfPurchase]=useState([]);
    const [error,setError]=useState("");
    const navigate=useNavigate();
    const handleNewTicket=async ()=>{
        try {
            //setError("Test test");
            const tick=await API.newTicket(undefined,props.purchase.product.ean);
            navigate("/tickets",{state:tick.ticketId});
        } catch (error) {
            setError(error.detail);
        }
    }
    useEffect(()=>{
        const getTickets=async()=>{
            try {
                const ticks=await API.getTicketsOfCustomerOfPurchase("032d838b-1b21-4154-8aac-315f2b0cc88c",props.purchase.product.ean);
                setTicketsOfPurchase(ticks);
            } catch (error) {
                setTicketsOfPurchase([]);
            }
        }
        getTickets();
    },[])
    return(
        <div className="purchase-bodyopened">
            {ticketsOfPurchase.map(tick=>{
                return(
                    <Card border={tick===props.selectedTicket?"info":"dark"} onClick={e=>{
                        e.preventDefault();
                        e.stopPropagation();
                        props.setSelectedTicket(tick==props.selectedTicket?undefined:tick);
                    }} className="ticket-card my-2">
                        <Card.Body>
                            Ticket created at {time.format(tick.creationDate)}
                            <span class="material-icons-round md-36 purchase-link" onClick={e=>{
                                e.preventDefault();
                                e.stopPropagation();
                                // more routes
                                //navigate("/tickets/"+tick.ticketId);
                                // single route, communication between pages
                                navigate("/tickets",{state:tick.ticketId});
                            }}>
                                arrow_forward_ios
                            </span>
                        </Card.Body>
                    </Card>
                );
            })}
            <Button className="justify-content-center" disabled={!props.isWarrantyValid} variant={props.isWarrantyValid?"outline-success":"outline-danger"} onClick={e=>{
                e.preventDefault();
                e.stopPropagation();
                handleNewTicket();
            }}>Open a new ticket!</Button>
            {error!=="" &&
                <Modal show={true} onHide={()=>setError("")} size="md" aria-labelledby="contained-modal-title-vcenter" centered>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">Couldn't open a new ticket!</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>{error}</Modal.Body>
                </Modal>
            }
        </div>
    )
}

function PurchasesPage(props){
    // set of purchases for frontend testing purposes
    const frontendTestPurchases=[
        {
            product:{
                ean:"1234567891012",
                name:"Iphone X",
            },
            datetime:"11/09/2012",
            warranty:{
                datetimeExpire:"4/09/2032",
            }
        }
    ]
    const [purchases,setPurchases]=useState(frontendTestPurchases);
    const [selectedPurchase,setSelectedPurchase]=useState(undefined);
    useEffect(()=>{
        const getPurchases=async ()=>{
            try {
                //console.log("Calling get purchases");
                const purchs=await API.getPurchasesOf();
                //console.log("Got ",purchs);
                setPurchases(purchs);
            } catch (error) {
                //console.log("Got error ",error.status);
                setPurchases([]);
            }
        }
        getPurchases();
    },[]);
    return(
        <div className="purchase-list">
            {
                purchases.map(purch=>{
                    const isWarrantyValid=time.isStillValid(purch.warranty.datetimeExpire);
                    return(
                        <Card border={purch===selectedPurchase?"info":"dark"} className="purchase-card my-2">
                            <Card.Header>
                                <Row>
                                    <Col xs={8}>
                                        <Card.Title>{purch.product.name}</Card.Title>
                                    </Col>
                                    <Col xs={4}>
                                        <div className="purchase-status">
                                            <Badge pill bg={isWarrantyValid?"success":"danger"}>
                                                {isWarrantyValid?"Warranty still valid until "+time.format(purch.warranty.datetimeExpire):"No warranty on the product!"}
                                            </Badge>
                                        </div>
                                    </Col>
                                </Row>
                            </Card.Header>
                            <Card.Body>
                                Purchased in {time.format(purch.datetime)}
                                {selectedPurchase===purch &&
                                    <PurchaseSelection purchase={purch} isWarrantyValid={isWarrantyValid}/>
                                }
                                <div className="purchase-footer">
                                    <span className="material-icons-round md-36 purchase-dropopen" onClick={e=>{
                                        e.preventDefault();
                                        e.stopPropagation();
                                        setSelectedPurchase(purch==selectedPurchase?undefined:purch);
                                    }}>
                                        keyboard_double_arrow_{selectedPurchase===purch?"up":"down"}
                                    </span>
                                </div>
                            </Card.Body>
                        </Card>
                    )
                })
            }
        </div>
    )
}

export default PurchasesPage;