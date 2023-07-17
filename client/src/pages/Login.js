import {useContext, useEffect, useState} from "react";
import { Form, Button, Alert, Container, Spinner, Row} from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import UserContext from "../context/UserContext";
import API from "../API";
import "../styles/Loading.css";
function LoginPage(props){
    const navigate= useNavigate();
    const {user,setUser}= useContext(UserContext);
    const [username,setUsername]= useState('');
    const [password,setPassword]= useState('');
    const [error,setError]= useState(false);
    const [errorMessage,setErrorMessage]= useState('');
    const [loading, setLoading]= useState(false);

    useEffect(() => {
        if (user.logged) {
            navigate('/home');
        }
    }, [user.logged, navigate]);

    if (!user.logged) {
        if (loading) {
            return (
                <Container fluid>
                    <Row>
                        <Spinner animation="border" variant="dark" className="spin-load" size="lg"/>
                    </Row>
                </Container>
            );
        } else {
            return (
                <Form style={
                    {
                        "margin": 0,
                        "position": "absolute",
                        "top": "50%",
                        "left": "50%",
                        "msTransform": "translate(-50%, -50%)",
                        "transform": "translate(-50%, -50%)"
                    }
                } onSubmit={async e => {
                    e.preventDefault();
                    e.stopPropagation();
                    setLoading(true)
                    try {
                        console.log("Trying login");
                        await API.login(username, password, setUser);
                        console.log("Chiamata API login eseguita");
                        console.log("Username:", username);
                        console.log("Password:", password);
                        setLoading(false)
                        navigate("/home");
                    } catch (status) {
                        setLoading(false)
                        setError(true);
                        console.log(status)
                        if (parseInt(status.message) === 500) {
                            setErrorMessage("Failed to contact the server.")
                        } else if (parseInt(status.message) === 401) {
                            setErrorMessage('Username or password is incorrect.');
                        }
                    }
                }}>
                    <Form.Group className="mb-3">
                        <Form.Label style={{fontWeight: "bolder"}}>Username : </Form.Label>
                        <Form.Control type="text" placeholder="insert username" name="username" required
                                      value={username} onChange={e => setUsername(e.target.value)}/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label style={{fontWeight: "bolder"}}>Password : </Form.Label>
                        <Form.Control type="password" placeholder="insert password" name="password" required
                                      onChange={p => setPassword(p.target.value)}/>
                    </Form.Group>
                    <Button variant="success" type="submit">Submit</Button>
                    {error ? <Alert className="my-3" variant="danger">{errorMessage}</Alert> : <></>}
                </Form>
            );

        }
    }else{
        return null
    }
}

export default LoginPage;