import {useContext, useEffect, useState, useRef} from "react";
import UserContext from "../context/UserContext";
import API from "../API";
import "../styles/Loading.css";
import "../styles/UserInfo.css";
import {useNavigate} from 'react-router-dom';
import {Col, Container, Row, Form, Button, Alert, Spinner} from "react-bootstrap";
import Profile from "../profile";
import Manager from "../manager";
import Expert from "../expert";
import {MdAddCircleOutline, MdRemoveCircleOutline} from 'react-icons/md'

function UserInfoPage(props){
    const {user, setUser}= useContext(UserContext);
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [error, setError] = useState(false);
    const [changed, setChanged]= useState(false);
    const [showMenu, setShowMenu] = useState(false);
    const [loading, setLoading]= useState(false);

    useEffect(() => {
        if (!user.logged) {
            navigate('/home');
        }else{
            setChanged(false)
            fetchUserInfo().catch((error) => {
                console.error(error);
            });
        }
    }, [user.logged, changed]);

    const togglePopupMenu = () => {
        setShowMenu(!showMenu);
    };

    const fetchUserInfo = async () => {
        try {
            let userInfo;
            if (user.role === 'customer') {
                userInfo = await API.getProfileInfo(user.id);
            } else if (user.role === 'expert') {
                userInfo = await API.getExpertInfo(user.id);
            } else if (user.role === 'manager'){
                userInfo = await API.getManagerInfo(user.id);
            }
            const updatedUser = {
                ...user,
                username: userInfo.username,
                email: userInfo.email,
                name: userInfo.name,
                surname: userInfo.surname,
            };
            setUser(updatedUser);
        } catch (error) {
            console.error(error);
        }
    };

    if (user.logged) {
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
                <div className="user-info-container">
                    <Container className="user-info">
                        <h1 className="user-info-h1">User Info</h1>
                        <Row className="user-info-row">
                            <Col xs={12} sm={4}>
                                <strong>Username:</strong>
                            </Col>
                            <Col xs={12} sm={8}>
                                <span>{user.username}</span>
                            </Col>
                        </Row>
                        <Row className="user-info-row">
                            <Col xs={12} sm={4}>
                                <strong>Email:</strong>
                            </Col>
                            <Col xs={12} sm={8}>
                                <span>{user.email}</span>
                            </Col>
                        </Row>
                        <Row className="user-info-row">
                            <Col xs={12} sm={4}>
                                <strong>Name:</strong>
                            </Col>
                            <Col xs={12} sm={8}>
                                <span>{user.name}</span>
                            </Col>
                        </Row>
                        <Row className="user-info-row">
                            <Col xs={12} sm={4}>
                                <strong>Surname:</strong>
                            </Col>
                            <Col xs={12} sm={8}>
                                <span>{user.surname}</span>
                            </Col>
                        </Row>
                    </Container>

                    <Container className="modify-user-info">
                        <h1 className="mx-auto">Modify User Info</h1>
                        <button className="open-menu-button" onClick={togglePopupMenu}>
                            {showMenu ? <MdRemoveCircleOutline className="button-icon"/> :
                                <MdAddCircleOutline className="button-icon"/>}
                        </button>
                        {showMenu && (
                            <div className="popup-menu">
                                <Form onSubmit={async e => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    setLoading(true)
                                    try {
                                        if (user.role === 'customer') {
                                            await API.modifyProfile(user.id, new Profile(user.id, username, email, name, surname))
                                        } else if (user.role === 'expert') {
                                            await API.modifyExpert(user.id, new Expert(user.id, username, email, name, surname))
                                        } else if (user.role === 'manager') {
                                            await API.modifyManager(user.id, new Manager(user.id, username, email, name, surname))
                                        }
                                        setChanged(true);
                                        setLoading(false);
                                        setUsername('');
                                        setEmail('');
                                        setName('');
                                        setSurname('');
                                        togglePopupMenu()
                                    } catch (error) {
                                        console.log(error)
                                        setError(true);
                                        setLoading(false);
                                    }
                                }}>
                                    <Form.Group className="mb-3">
                                        <Form.Label style={{fontWeight: "bolder"}}>Username : </Form.Label>
                                        <Form.Control type="text"
                                                      placeholder="insert the new username or confirm the old one"
                                                      name="username" required value={username}
                                                      onChange={e => setUsername(e.target.value)}/>
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label style={{fontWeight: "bolder"}}>Email : </Form.Label>
                                        <Form.Control type="email"
                                                      placeholder="insert the new email or confirm the old one"
                                                      name="email" required
                                                      onChange={p => setEmail(p.target.value)}/>
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label style={{fontWeight: "bolder"}}>Name : </Form.Label>
                                        <Form.Control type="text"
                                                      placeholder="insert the new name or confirm the old one"
                                                      name="name" required value={name}
                                                      onChange={e => setName(e.target.value)}/>
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label style={{fontWeight: "bolder"}}>Surname : </Form.Label>
                                        <Form.Control type="text"
                                                      placeholder="insert the new surname or confirm the old one"
                                                      name="surname" required value={surname}
                                                      onChange={e => setSurname(e.target.value)}/>
                                    </Form.Group>
                                    <Button variant="success" type="submit">Submit</Button>
                                    {error ? <Alert className="my-3" variant="danger">Something went
                                        wrong!</Alert> : <></>}
                                </Form>
                            </div>
                        )}
                    </Container>
                </div>
            );
        }
    } else{
        return null;
    }
}
export  default UserInfoPage