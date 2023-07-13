import {useState, useContext, useEffect} from "react";
import { Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import API from "../API";
import RegistrationData from "../registrationData";
import UserContext from "../context/UserContext";

function CreateExpertPage(props){
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [error, setError] = useState(false);
    useEffect(() => {
        if (user.role!=='manager') {
            navigate('/home');
        }
    }, [user.logged, navigate]);

    if(user.role==='manager') {
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
                try {
                    await API.createExpert(new RegistrationData(username, password, email, name, surname));
                    navigate('/homepage');
                } catch (error) {
                    setError(true);
                }
            }}>
                <Form.Group className="mb-3">
                    <Form.Label style={{fontWeight: "bolder"}}>Username : </Form.Label>
                    <Form.Control type="text" placeholder="insert username" name="username" required value={username}
                                  onChange={e => setUsername(e.target.value)}/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label style={{fontWeight: "bolder"}}>Password : </Form.Label>
                    <Form.Control type="password" placeholder="insert password" name="password" required
                                  onChange={p => setPassword(p.target.value)}/>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label style={{fontWeight: "bolder"}}>Email : </Form.Label>
                    <Form.Control type="text" placeholder="insert email" name="email" required value={email}
                                  onChange={e => setEmail(e.target.value)}/>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label style={{fontWeight: "bolder"}}>Name : </Form.Label>
                    <Form.Control type="text" placeholder="insert name" name="name" required value={name}
                                  onChange={e => setName(e.target.value)}/>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label style={{fontWeight: "bolder"}}>Surname : </Form.Label>
                    <Form.Control type="text" placeholder="insert surname" name="surname" required value={surname}
                                  onChange={e => setSurname(e.target.value)}/>
                </Form.Group>
                <Button variant="success" type="submit">Submit</Button>
                {error ? <Alert className="my-3" variant="danger">Something went wrong!</Alert> : <></>}
            </Form>
        );
    }else{
        return null;
    }
}

export default CreateExpertPage