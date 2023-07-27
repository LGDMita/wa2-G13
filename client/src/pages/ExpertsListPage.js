import { useContext, useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { Container, Table, Row, Col, Button, Spinner } from 'react-bootstrap'

import UserContext from '../context/UserContext';
import API from "../API";

import "../styles/LoginPage.css"

function ExpertsListPage() {
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const { user } = useContext(UserContext);
    const [experts, setExperts] = useState([])

    useEffect(() => {
        if (user.role !== 'manager') {
            navigate('/home');
        } else {
            const fetchExperts = async () => {
                try {
                    const expertsData = await API.getExperts();
                    setExperts(expertsData);
                    setLoading(false);
                } catch (error) {
                    console.log(error);
                }
            };

            fetchExperts();
        }
    }, [user.role, navigate]);

    return (
        <Container>
            <br />
            <Row>
                <h4 className='text-center'>Here you can manage all the experts</h4>
            </Row>
            {loading ?
                <Container fluid>
                    <Row>
                        <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
                    </Row>
                </Container> :
                <>
                    <Row className="mb-3">
                        <Col className="text-end">
                            <Button variant="primary" onClick={() => navigate('/createExpert')}>
                                Add expert
                            </Button>
                        </Col>
                    </Row>
                    <Row>
                        <Table striped bordered hover>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Surname</th>
                                    <th>Email</th>
                                    <th>Username</th>
                                    <th>Modify</th>
                                </tr>
                            </thead>
                            <tbody>
                                {experts.map(expert => (
                                    <tr key={expert.id}>
                                        <td>{expert.name}</td>
                                        <td>{expert.surname}</td>
                                        <td>{expert.email}</td>
                                        <td>{expert.username}</td>
                                        <td>
                                            <Button variant="primary" onClick={() => navigate("/modifyExpert/" + expert.id)}>
                                                Modify
                                            </Button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Row>
                </>
            }
        </Container>
    );
}

export default ExpertsListPage;