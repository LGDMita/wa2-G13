import { useContext, useState } from "react";
import { Row, Col, Form, Button, Container } from 'react-bootstrap';
import ErrorMessage from '../components/ErrorMessage';
import { useNavigate } from 'react-router-dom';
import { ReactComponent as Logo } from "../logo.svg";

import UserContext from "../context/UserContext";
import API from "../API";

import "../styles/LoginPage.css"

function LoginPage(props) {
    const navigate = useNavigate();
    const { user, setUser } = useContext(UserContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async () => {
        try {
            console.log("Trying login");
            await API.login(username, password, setUser);
            navigate("/home");
        }
        catch (status) {
            setShowError(true);
            if (parseInt(status.message) === 500) {
                setErrorMessage("Failed to contact the server.")
            }
            else if (parseInt(status.message) === 401) {
                setErrorMessage('Username or password is incorrect.');
            }
        }
    }

    const handleErrorClose = () => {
        setShowError(false);
    };

    return (

        <div className='login-page-container d-flex align-items-center'>
            <Container>
                <Row>
                    <Col md={6} xs={12} className="text-center">
                        <Logo
                            alt=""
                            width="130"
                            height="150"
                            className="d-inline-block align-top"
                        />
                        <h1 className='text-login login-col'>MyTicketManager</h1>
                        <br />
                    </Col>
                    <Col md={6} xs={12}>
                        <Form>
                            <Form.Group controlId="formUsername">
                                <Form.Label className='text-login'>Username</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Enter username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                />
                            </Form.Group>
                            <br />
                            <Form.Group controlId="formPassword">
                                <Form.Label className='text-login'>Password</Form.Label>
                                <Form.Control
                                    type="password"
                                    placeholder="Enter password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </Form.Group>
                            <br />
                            <div className='text-center btn-login'>
                                <Button variant="primary" type="button" onClick={handleLogin}>
                                    Login
                                </Button>
                                <Button variant="info" type="button">
                                    Signup
                                </Button>
                                <Button variant="secondary" type="button">
                                    Password forgotten?
                                </Button>
                            </div>
                            <br />
                            {showError && <ErrorMessage message={errorMessage} onClose={handleErrorClose} />}
                        </Form>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default LoginPage;