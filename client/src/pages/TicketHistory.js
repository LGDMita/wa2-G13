import { useEffect, useState, useContext } from "react";
import API from "../API";
import { useNavigate, useParams } from "react-router-dom";
import { Card, Container, Button, Row, Spinner } from "react-bootstrap";
import time from "../lib/time";

import UserContext from "../context/UserContext";

const STATUS_TRADUCTIONS = {
    'in_progress': 'IN PROGRESS',
    'open': 'OPEN',
    'closed': 'CLOSED',
    'reopened': 'REOPENED',
    'resolved': 'RESOLVED',
}

function TicketHistory() {
    const { user } = useContext(UserContext);
    const [history, setHistory] = useState([]);
    const navigate = useNavigate();
    if (user.role !== "manager") navigate("/home");
    const { ticketId } = useParams();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const getHistory = async () => {
            try {
                //if (!location.state) throw new Error({ status: 0, detail: "No id provided" });
                const history = await API.getTicketHistory(ticketId);
                setHistory(history);
                setLoading(false);
            } catch (error) {
                setHistory([]);
            }
        }
        getHistory();
    }, [])

    if (loading) {
        return (<Container fluid>
            <Row>
                <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
            </Row>
        </Container>);
    }
    else {
        return (
            <Container className="ticket-history-container product-table my-3">
                <h2 className="text-center mb-4">History of ticket n. {ticketId}</h2>
                {history.map(h => (
                    <Card key={h.historyId} className="ticket-history-card my-2">
                        <Card.Body>
                            {h.oldStatus !== h.newStatus && "From " + STATUS_TRADUCTIONS[h.oldStatus] + " to " + STATUS_TRADUCTIONS[h.newStatus] + " : " + time.format(h.dateTime)}
                            {h.oldStatus === h.newStatus && STATUS_TRADUCTIONS[h.newStatus] + " : " + time.format(h.dateTime)}
                        </Card.Body>
                    </Card>
                ))}
                <Button variant="secondary" type="button" onClick={() => navigate("/tickets")}>Back</Button>
            </Container>
        );
    }
}

export default TicketHistory;