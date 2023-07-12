import React from 'react';
import { Row, Col, Form, Button, Container } from 'react-bootstrap';
import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { ReactComponent as Logo } from "../logo.svg";
import API from '../API';
import TokenManager from '../TokenManager';
import ErrorMessage from './ErrorMessage';

import "../styles/LoginPage.css"

export default function LoginPagee(props) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            await API.login(username, password);
            props.setLoggedIn(true);
            navigate("/")
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
    };

    const handleErrorClose = () => {
        setShowError(false);
    };

    const handleLoad = () => {
        const tokenManager = TokenManager();
        if (tokenManager.amILogged()) {
            props.setLoggedIn(true);
            navigate("/");
        }
    };

    useEffect(() => {
        handleLoad(); // Esegui la funzione handleLoad al caricamento di LoginPage
    }, []);

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
};