import { useContext, useState, useEffect } from "react";
import UserContext from "../context/UserContext";
import API from "../API";
import "../styles/UserInfo.css";
import { useNavigate} from 'react-router-dom';
import {Col, Container, Row} from "react-bootstrap";

function UserInfoPage(props){
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();
    const [id, setId] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');

    //soluzione provvisoria: non abbiamo API e tabelle per il manager: rivedere come gestire
    useEffect(() => {
        if (!user.logged || user.role === 'manager' ) {
            navigate('/home');
        }else{
            fetchUserInfo().then((result) => {
                setId(result.id)
                setUsername(result.username)
                setEmail(result.email)
                setName(result.name)
                setSurname(result.surname)
            }).catch((error) => {
                console.error(error);
            });
        }
    }, [user.logged, navigate]);

    const fetchUserInfo = async () => {
        try {
            let userInfo;
            if (user.role === 'customer') {
                userInfo = await API.getProfileInfo(user.id);
                return userInfo
            } else if (user.role === 'expert') {
                userInfo = await API.getExpertInfo(user.id);
                return userInfo
            }
        } catch (error) {
            console.error(error);
        }
    };

    if(user.logged && user.role !== 'manager' ) {
        return (
            <Container className="user-info">
                <h1 className="user-info-h1">User Info</h1>
                <Row className="user-info-row">
                    <Col xs={12} sm={4}>
                        <strong>Id:</strong>
                    </Col>
                    <Col xs={12} sm={8}>
                        <span>{id}</span>
                    </Col>
                </Row>
                <Row className="user-info-row">
                    <Col xs={12} sm={4}>
                        <strong>Username:</strong>
                    </Col>
                    <Col xs={12} sm={8}>
                        <span>{username}</span>
                    </Col>
                </Row>
                <Row className="user-info-row">
                    <Col xs={12} sm={4}>
                        <strong>Email:</strong>
                    </Col>
                    <Col xs={12} sm={8}>
                        <span>{email}</span>
                    </Col>
                </Row>
                <Row className="user-info-row">
                    <Col xs={12} sm={4}>
                        <strong>Name:</strong>
                    </Col>
                    <Col xs={12} sm={8}>
                        <span>{name}</span>
                    </Col>
                </Row>
                <Row className="user-info-row">
                    <Col xs={12} sm={4}>
                        <strong>Surname:</strong>
                    </Col>
                    <Col xs={12} sm={8}>
                        <span>{surname}</span>
                    </Col>
                </Row>
            </Container>
        );
    }else{
        return null;
    }
}
export  default UserInfoPage