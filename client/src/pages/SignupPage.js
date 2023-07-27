import { useContext, useState, useEffect } from "react";
import { Row, Col, Form, Button, Container, Spinner } from 'react-bootstrap';
import ErrorMessage from '../components/ErrorMessage';
import { useNavigate } from 'react-router-dom';
import { ReactComponent as Logo } from "../logo.svg";
import RegistrationData from "../registrationData";

import UserContext from "../context/UserContext";
import API from "../API";

import "../styles/LoginPage.css"

function SignupPage(props) {
    const navigate = useNavigate();
    const { user, setUser } = useContext(UserContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user.logged) {
            navigate('/home');
        }
    }, [user.logged, navigate]);

    const handleSignup = async () => {
        setLoading(true)
        try {
            await API.signup(new RegistrationData(username, password, email, name, surname));
            window.alert("User successfully created!");
            await API.login(username, password, setUser);
            setLoading(false);
            props.setLogged(true);
            navigate('/home');
        } catch (error) {
            setLoading(false);
            if (parseInt(error.message) === 409)
                setErrorMessage("Invalid data: username or email already present in the system");
            else
                setErrorMessage(error.message);
            setShowError(true);
        }
    }

    const handleErrorClose = () => {
        setShowError(false);
    };

    return (

        <div className='signup-page-container d-flex align-items-center'>
            <Container>
                <Row>
                    <Col md={6} xs={12} className="text-center d-flex align-items-center">
                        <Row onClick={() => navigate("/home")}>
                            <Col xs={12} className="text-center">
                                <Logo
                                    alt=""
                                    width="130"
                                    height="150"
                                    className="d-inline-block align-top"
                                />
                            </Col>
                            <Col xs={12} className="text-center">
                                <h1 className='text-login login-col'>MyTicketManager</h1>
                            </Col>
                        </Row>
                    </Col>
                    <Col md={6} xs={12}>
                        {
                            loading ?
                                <Spinner animation="border" className="spin-load spinner-signup" />
                                :
                                <Form className="container-sign-up-form">
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
                                    <div className='text-center btn-login'>
                                        <Button variant="success" type="button" onClick={() => handleSignup()} >Submit</Button>
                                        <Button variant="secondary" type="button" onClick={() => navigate("/login")} >Back</Button>
                                    </div>
                                </Form>
                        }
                        <br />
                        {showError && <ErrorMessage message={errorMessage} onClose={handleErrorClose} />}
                    </Col>
                </Row>
            </Container>
        </div >
    );
}

export default SignupPage;