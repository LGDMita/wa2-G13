import { useState, useContext, useEffect } from "react";
import { Form, Button, Alert, Container, Row, Spinner, Modal } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import "../styles/CreateExpert.css";
import "../styles/Loading.css";
import API from "../API";
import RegistrationData from "../registrationData";
import UserContext from "../context/UserContext";

const SuccessAlert = ({ show, onClose }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Success</Modal.Title>
            </Modal.Header>
            <Modal.Body>New expert has been successfully created!</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

function CreateExpertPage() {
    const { user } = useContext(UserContext);
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [showSuccessAlert, setShowSuccessAlert] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!user.logged || user.role !== 'manager') {
            navigate('/login');
        }
    }, [user.logged, user.role, navigate]);

    if (user.logged && user.role === 'manager') {
        if (loading) {
            return (
                <Container fluid>
                    <Row>
                        <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
                    </Row>
                </Container>
            );
        } else {
            return (
                <div className="container-create-expert-parent">
                    <div className="container-create-expert">
                        <h1 className="container-create-expert-h1">Create new Expert</h1>
                        <Form className="container-create-expert-form" onSubmit={async e => {
                            e.preventDefault();
                            e.stopPropagation();
                            setLoading(true)
                            try {
                                await API.createExpert(new RegistrationData(username, password, email, name, surname));
                                setLoading(false)
                                setUsername('');
                                setPassword('')
                                setEmail('');
                                setName('');
                                setSurname('');
                                setShowSuccessAlert(true);
                            } catch (error) {
                                setError(error);
                                setLoading(false)
                            }
                        }}>
                            {showSuccessAlert && <SuccessAlert show={showSuccessAlert} onClose={() => setShowSuccessAlert(false)} />}
                            <Form.Group className="mb-3">
                                <Form.Label style={{ fontWeight: "bolder" }}>Username : </Form.Label>
                                <Form.Control type="text" placeholder="insert username" name="username" required
                                    value={username}
                                    onChange={e => setUsername(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="formBasicPassword">
                                <Form.Label style={{ fontWeight: "bolder" }}>Password : </Form.Label>
                                <Form.Control type="password" placeholder="insert password" name="password" required
                                    onChange={p => setPassword(p.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label style={{ fontWeight: "bolder" }}>Email : </Form.Label>
                                <Form.Control type="text" placeholder="insert email" name="email" required value={email}
                                    onChange={e => setEmail(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label style={{ fontWeight: "bolder" }}>Name : </Form.Label>
                                <Form.Control type="text" placeholder="insert name" name="name" required value={name}
                                    onChange={e => setName(e.target.value)} />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label style={{ fontWeight: "bolder" }}>Surname : </Form.Label>
                                <Form.Control type="text" placeholder="insert surname" name="surname" required
                                    value={surname}
                                    onChange={e => setSurname(e.target.value)} />
                            </Form.Group>
                            <Button className="mx-auto" variant="success" type="submit">Submit</Button> &nbsp;
                            <Button variant="secondary" type="button" onClick={() => navigate("/experts")}>Back</Button>
                            {error !== '' ? <Alert className="my-3" variant="danger">Error during expert creation: {error.message}</Alert> : <></>}
                        </Form>
                    </div>
                </div>
            );
        }
    } else {
        return null;
    }
}

export default CreateExpertPage