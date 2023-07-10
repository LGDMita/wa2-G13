import { useContext, useState } from "react";
import { Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import UserContext from "../context/UserContext";
import API from "../API";
function LoginPage(props){
    const navigate=useNavigate();
    const {user,setUser}=useContext(UserContext);
    const [role,setRole]=useState('');
    const [username,setUsername]=useState('');
    const [password,setPassword]=useState('');
    const [error,setError]=useState(false);
    return(
        <Form style={
            {
                "margin": 0,
                "position": "absolute",
                "top": "50%",
                "left": "50%",
                "msTransform": "translate(-50%, -50%)",
                "transform": "translate(-50%, -50%)"
            }
        } onSubmit={async e=>{
            e.preventDefault();
            e.stopPropagation();
            try {
                //await API.login(username,password);
                setUser({logged:true,role:role,username:username,password:password});
                navigate('/home');
            } catch (error) {
                setError(true);
            }
        }}>
            <Form.Group className="mb-3">
                <Form.Label style={{fontWeight:"bolder"}}>Username : </Form.Label>
                <Form.Control type="text" placeholder="insert username" name="username" required value={username} onChange={e=>setUsername(e.target.value)} />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formBasicPassword">
                <Form.Label style={{fontWeight:"bolder"}}>Password : </Form.Label>
                <Form.Control type="password" placeholder="insert password" name="password" required onChange={p=>setPassword(p.target.value)}/>
            </Form.Group>
            <Form.Group>
                <Form.Select style={{fontWeight:"bolder"}} value={role} required onChange={r=>setRole(r.target.value)}>
                    <option value=''>Select your role!</option>
                    <option value="customer">Customer</option>
                    <option value="expert">Expert</option>
                    <option value="manager">Manager</option>
                </Form.Select>
            </Form.Group>
            <Button variant="success" type="submit">Submit</Button>
            {error?<Alert className="my-3" variant="danger">Something went wrong!</Alert>:<></>}
        </Form>
    );
}

export default LoginPage;