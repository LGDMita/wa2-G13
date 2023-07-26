import { useContext, useEffect, useState } from "react";
import UserContext from "../context/UserContext";
import API from "../API";
import "../styles/Loading.css";
import "../styles/UserInfo.css";
import { useNavigate } from 'react-router-dom';
import { Col, Container, Row, Form, Button, Alert, Spinner, Modal } from "react-bootstrap";
import Profile from "../profile";
import Manager from "../manager";
import Expert from "../expert";
import { MdAddCircleOutline, MdRemoveCircleOutline } from 'react-icons/md'
import SectorsContext from "../context/SectorsContext";

const SuccessAlert = ({ show, onClose }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Success</Modal.Title>
            </Modal.Header>
            <Modal.Body>Your password have been successfully changed!</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

function UserInfoPage(props) {
    const { user, setUser } = useContext(UserContext);
    let { sectors } = useContext(SectorsContext);
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [error, setError] = useState('');
    const [errorPassword, setErrorPassword] = useState('');
    const [showMenu, setShowMenu] = useState(false);
    const [loading, setLoading] = useState(false);
    const [sectorsState, setSectorsState] = useState([]);

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showSuccessAlert, setShowSuccessAlert] = useState(false);

    useEffect(() => {
        if (!user.logged) {
            navigate('/login');
        } else {
            fetchUserInfo().catch((error) => {
                console.error(error);
            });
        }
    }, [user.logged, user.role]);

    const togglePopupMenu = () => {
        setShowMenu(!showMenu);
    };

    const fetchUserInfo = async () => {
        setLoading(true)
        try {
            let userInfo;
            if (user.role === 'customer') {
                userInfo = await API.getProfileInfo(user.id);
            } else if (user.role === 'expert') {
                userInfo = await API.getExpertInfo(user.id);
                sectors = await API.getSectorsOfExpert(user.id);
                setSectorsState(sectors)
            } else if (user.role === 'manager') {
                userInfo = await API.getManagerInfo(user.id);
            }
            const updatedUser = {
                ...user,
                username: userInfo.username,
                email: userInfo.email,
                name: userInfo.name,
                surname: userInfo.surname,
            };
            setName(userInfo.name);
            setSurname(userInfo.surname);
            setEmail(userInfo.email);
            setUsername(userInfo.username);
            setUser(updatedUser);
            setLoading(false);
        } catch (error) {
            console.error(error);
        }
    };

    const handleChangePassword = async () => {
        if (newPassword !== confirmPassword) {
            setErrorPassword({ message: "New password and confirm password do not match." });
            return;
        }

        if (newPassword.length < 8 || newPassword.length > 32) {
            setErrorPassword({ message: "New password must be between 8 and 32 chars." });
            return;
        }

        try {
            await API.changePassword(user.id, user.username, oldPassword, newPassword);
            setErrorPassword('');
            setShowSuccessAlert(true);
        } catch (error) {
            setErrorPassword(error);
        }
    };

    if (user.logged) {
        if (loading) {
            return (
                <Container fluid>
                    <Row>
                        <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
                    </Row>
                </Container>
            );
        } else {
            return (
                <div className="user-info-container">
                    <Container className="user-info">
                        {showSuccessAlert && <SuccessAlert show={showSuccessAlert} onClose={() => setShowSuccessAlert(false)} />}
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
                        {user.role === 'expert' && (
                            <Row className="user-info-row">
                                <Col xs={12} sm={4}>
                                    <strong>Sectors:</strong>
                                </Col>
                                <Col xs={12} sm={8}>
                                    <span>{sectorsState.length > 0 ? sectorsState.map(sector => sector.name).join(", ") : "No sectors available"}</span>
                                </Col>
                            </Row>
                        )
                        }
                    </Container>

                    <Container className="modify-user-info">
                        <div className="modify-user-info-h1-button">
                            <h1>Modify User Info</h1>
                            <button className="open-menu-button" onClick={togglePopupMenu}>
                                {showMenu ? <MdRemoveCircleOutline className="button-icon" /> :
                                    <MdAddCircleOutline className="button-icon" />}
                            </button>
                        </div>
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
                                        setUsername('')
                                        setEmail('')
                                        setName('')
                                        setSurname('')
                                        setLoading(false)
                                        window.alert("Modifications correctly saved. LOGIN is required!")
                                        props.handleLogout()
                                        //navigate("/login")
                                    } catch (error) {
                                        setError(error);
                                        setLoading(false);
                                    }
                                }}>
                                    <Form.Group className="mb-4">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Username : </Form.Label>
                                        <Form.Control type="text"
                                            placeholder="insert the new username or confirm the old one"
                                            name="username" required value={username}
                                            onChange={e => setUsername(e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-4">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Email : </Form.Label>
                                        <Form.Control type="email"
                                            placeholder="insert the new email or confirm the old one"
                                            name="email" required value={email}
                                            onChange={p => setEmail(p.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-4">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Name : </Form.Label>
                                        <Form.Control type="text"
                                            placeholder="insert the new name or confirm the old one"
                                            name="name" required value={name}
                                            onChange={e => setName(e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-5">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Surname : </Form.Label>
                                        <Form.Control type="text"
                                            placeholder="insert the new surname or confirm the old one"
                                            name="surname" required value={surname}
                                            onChange={e => setSurname(e.target.value)} />
                                    </Form.Group>
                                    <Button variant="success" type="submit">Change info</Button>
                                    {error !== "" ? <Alert className="my-3" variant="danger">Error during modification: {error.message}</Alert> : <></>}
                                </Form>
                                <br />
                                <h5>Change your password</h5>
                                <Form>
                                    <Form.Group className="mb-4">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Old password : </Form.Label>
                                        <Form.Control type="password"
                                            placeholder="Insert your old password"
                                            name="old-password" required
                                            onChange={e => setOldPassword(e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-4">
                                        <Form.Label style={{ fontWeight: "bolder" }}>New password : </Form.Label>
                                        <Form.Control type="password"
                                            placeholder="Insert your new password"
                                            name="new-password" required
                                            onChange={e => setNewPassword(e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-5">
                                        <Form.Label style={{ fontWeight: "bolder" }}>Confirm password : </Form.Label>
                                        <Form.Control type="password"
                                            placeholder="Confirm your new password"
                                            name="confirm-password" required
                                            onChange={e => setConfirmPassword(e.target.value)} />
                                    </Form.Group>
                                    <Button variant="success" type="button" onClick={handleChangePassword}>Change new password</Button>
                                    {errorPassword !== "" ? <Alert className="my-3" variant="danger">Error during password change: {errorPassword.message}</Alert> : <></>}
                                </Form>
                            </div>
                        )}
                    </Container>
                </div>
            );
        }
    } else {
        return null;
    }
}
export default UserInfoPage