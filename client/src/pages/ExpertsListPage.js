import { useContext, useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { Container, Table, Row, Col, Button } from 'react-bootstrap'

import UserContext from '../context/UserContext';
import API from "../API";

import "../styles/LoginPage.css"

function ExpertsListPage(props) {
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
                } catch (error) {
                    console.log(error);
                }
            };

            fetchExperts();
        }
    }, [user.role, navigate]);

    const handleEdit = (expert) => {
        try {
            API.updateExpert(expert);
            setExperts(API.getExperts());
        }
        catch (error) {
            console.log(error)
        }
    }

    return (
        <Container>
            <br />
            <Row className="mb-3">
                <Col className="text-end">
                    <Button variant="primary">
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
                            <th>Modifica</th>
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
                                    <Button variant="primary" onClick={() => handleEdit(expert)}>
                                        Modify
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </Row>
        </Container>
    );
}

export default ExpertsListPage;