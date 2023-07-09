import React from 'react';
import { Row, Col, Form, Button, Container } from 'react-bootstrap';
import { ReactComponent as Logo } from "../logo.svg";

import "../styles/LoginPage.css"

export default function LoginPage() {
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
                                <Form.Control type="text" placeholder="Enter username" />
                            </Form.Group>
                            <br />
                            <Form.Group controlId="formPassword">
                                <Form.Label className='text-login'>Password</Form.Label>
                                <Form.Control type="password" placeholder="Enter password" />
                            </Form.Group>
                            <br />
                            <div className='text-center btn-login'>
                                <Button variant="primary" type="button">
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
                        </Form>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};