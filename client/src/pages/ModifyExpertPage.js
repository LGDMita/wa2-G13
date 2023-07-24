import { useState, useContext, useEffect } from "react";
import { Form, Button, Alert, Container, Row, Spinner, Modal } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import "../styles/CreateExpert.css";
import "../styles/Loading.css";
import API from "../API";
import RegistrationData from "../registrationData";
import UserContext from "../context/UserContext";

const SuccessAlert = ({ show, onClose }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Success</Modal.Title>
            </Modal.Header>
            <Modal.Body>New expert has been successfully modified!</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

function ModifyExpertPage() {
    const { expertId } = useParams();
    const { user } = useContext(UserContext);
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [error, setError] = useState(false);
    const [loading, setLoading] = useState(true);
    const [showSuccessAlert, setShowSuccessAlert] = useState(false);
    const [availableSectors, setAvailableSectors] = useState([]);
    const [newSector, setNewSector] = useState("");

    useEffect(() => {
        if (!user.logged || user.role !== 'manager') {
            navigate('/home');
        } else {
            const fetchExpertData = async () => {
                try {
                    const expertData = await API.getExpertInfo(expertId);
                    const sectors = await API.getSectors();
                    const expertSector = await API.getExpertSectors(expertId);

                    const updatedAvailableSectors = sectors.map(sector => ({
                        ...sector,
                        checked: expertSector.some(expertSector => expertSector.id === sector.id)
                    }));

                    setAvailableSectors(updatedAvailableSectors);
                    setUsername(expertData.username);
                    setEmail(expertData.email);
                    setName(expertData.name);
                    setSurname(expertData.surname);
                    setLoading(false);
                } catch (error) {
                    console.log(error);
                    navigate("/experts")
                }
            };

            fetchExpertData();
        }
    }, [user.logged, user.role, navigate, expertId]);

    const handleSectorSelection = (sectorId) => {
        const isSelected = availableSectors.find(item => item.sectorId === sectorId);
        const updatedSectorsArray = availableSectors.map((sector) => {
            if (sector.id === sectorId) {
                return {
                    ...sector,
                    checked: !isSelected,
                };
            }
            return sector;
        });
        setAvailableSectors([...updatedSectorsArray]);
    }

    const handleAddNewSector = () => {
        if (newSector.trim() !== "") {
            setAvailableSectors((prevSectors) => [
                ...prevSectors,
                { id: -1, name: newSector, checked: true },
            ]);
            setNewSector("");
        }
    };

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
            <div className="container-create-expert-parent">
                <div className="container-create-expert">
                    <h1 className="container-create-expert-h1">Modify existing Expert</h1>
                    <Form className="container-create-expert-form" onSubmit={async e => {
                        e.preventDefault();
                        e.stopPropagation();
                        setLoading(true)
                        try {
                            await API.modifyExpertWithSector(expertId, new RegistrationData(username, password, email, name, surname),
                                availableSectors.filter((s) => s.checked === true).map((sector) => ({ id: sector.id, name: sector.name })));
                            setLoading(false)
                            setUsername('');
                            setPassword('')
                            setEmail('');
                            setName('');
                            setSurname('');
                            setShowSuccessAlert(true);
                        } catch (error) {
                            setLoading(false)
                            setError(true);
                        }
                    }}>
                        {showSuccessAlert && <SuccessAlert show={showSuccessAlert} onClose={() => setShowSuccessAlert(false)} />}
                        {error ? <Alert className="my-3" variant="danger">Something went wrong!</Alert> : <></>}
                        <Form.Group className="mb-3">
                            <Form.Label style={{ fontWeight: "bolder" }}>Username : </Form.Label>
                            <Form.Control type="text" placeholder="insert username" name="username" required
                                value={username} disabled
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

                        <Form.Group className="mb-3">
                            <Form.Label style={{ fontWeight: "bolder" }}>
                                Available Sectors :
                            </Form.Label>
                            <div
                                className="scrollable-container"
                                style={{ maxHeight: "200px", overflowY: "auto" }}
                            >
                                {availableSectors.map((sector) => (
                                    <Form.Check
                                        key={`availableSector_${sector.id}`}
                                        type="checkbox"
                                        id={`availableSector_${sector.id}`}
                                        label={sector.name}
                                        value={sector.id}
                                        onChange={() => handleSectorSelection(sector.id)}
                                        checked={sector.checked}
                                    />
                                ))}
                            </div>
                        </Form.Group>


                        <Form.Group className="mb-3">
                            <Form.Label style={{ fontWeight: "bolder" }}>Add New Sector:</Form.Label>
                            <div className="d-flex">
                                <Form.Control
                                    type="text"
                                    placeholder="Enter new sector name"
                                    value={newSector}
                                    onChange={(e) => setNewSector(e.target.value)}
                                /> &nbsp;
                                <Button variant="primary" onClick={handleAddNewSector}>
                                    Add
                                </Button>
                            </div>
                        </Form.Group>
                        <Button className="mx-auto" variant="success" type="submit">Submit</Button> &nbsp;
                        <Button variant="secondary" type="button" onClick={() => navigate("/experts")}>Back</Button>
                    </Form>
                </div>
            </div>
        );
    }
}

export default ModifyExpertPage