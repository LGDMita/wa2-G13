import { useState, useContext, useEffect } from "react";
import { Form, Button, Alert, Container, Row, Spinner, Modal } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import "../styles/CreateExpert.css";
import "../styles/Loading.css";
import API from "../API";
import UserContext from "../context/UserContext";
import Expert from "../expert";

const SuccessAlert = ({ show, onClose, message, goToList }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Success</Modal.Title>
            </Modal.Header>
            <Modal.Body>{message}</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={onClose}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

const ConfirmDeleteAlert = ({ show, onClose, onConfirm }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>Confirm Deletion</Modal.Title>
            </Modal.Header>
            <Modal.Body>Are you sure you want to delete this expert?</Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onClose}>
                    Cancel
                </Button>
                <Button variant="danger" onClick={onConfirm}>
                    Confirm Delete
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
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [error, setError] = useState(false);
    const [loading, setLoading] = useState(true);
    const [showSuccessAlert, setShowSuccessAlert] = useState(false);
    const [showDeleteAlert, setShowDeleteAlert] = useState(false);
    const [availableSectors, setAvailableSectors] = useState([]);
    const [newSector, setNewSector] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (!user.logged || user.role !== 'manager') {
            navigate('/home');
        } else {
            const fetchExpertData = async () => {
                try {
                    const expertData = await API.getExpertInfo(expertId);
                    const sectors = await API.getSectors();
                    const expertSector = await API.getExpertSectors(expertId);

                    let updatedAvailableSectors = []
                    if (expertSector !== []) {
                        updatedAvailableSectors = sectors.map(sector => ({
                            ...sector,
                            checked: expertSector.some(es => es.sectorId === sector.sectorId)
                        }));
                    }

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
        const isSelected = availableSectors.find(item => item.sectorId === sectorId).checked;
        const updatedSectorsArray = availableSectors.map((sector) => {
            if (sector.sectorId === sectorId) {
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

    const handleDeleteExpert = async () => {
        try {
            await API.deleteExpert(expertId);
            setMessage("Expert has been succesfully deleted!");
            setShowSuccessAlert(true);
        }
        catch (error) {
            console.error(error);
            setShowDeleteAlert(false);
            setError(true);
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
                            await API.modifyExpertWithSector(expertId, new Expert(expertId, username, email, name, surname),
                                availableSectors.filter((s) => s.checked === true).map((sector) => ({ sectorId: sector.sectorId, name: sector.name })));
                            setLoading(false)
                            setMessage("New expert has been successfully modified!");
                            setShowSuccessAlert(true);
                        } catch (error) {
                            setLoading(false)
                            setError(true);
                        }
                    }}>
                        {showSuccessAlert && <SuccessAlert show={showSuccessAlert} onClose={() => navigate("/experts")} message={message} />}
                        {showDeleteAlert && <ConfirmDeleteAlert show={showDeleteAlert} onClose={() => setShowDeleteAlert(false)} onConfirm={handleDeleteExpert} />}
                        {error ? <Alert className="my-3" variant="danger">Something went wrong!</Alert> : <></>}
                        <Form.Group className="mb-3">
                            <Form.Label style={{ fontWeight: "bolder" }}>Username : </Form.Label>
                            <Form.Control type="text" placeholder="insert username" name="username" required
                                value={username} disabled
                                onChange={e => setUsername(e.target.value)} />
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
                                        key={`availableSector_${sector.sectorId}`}
                                        type="checkbox"
                                        id={`availableSector_${sector.sectorId}`}
                                        label={sector.name}
                                        value={sector.sectorId}
                                        onChange={() => handleSectorSelection(sector.sectorId)}
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
                        <Button className="mx-auto" variant="danger" type="button" onClick={() => setShowDeleteAlert(true)}>Delete</Button> &nbsp;
                        <Button variant="secondary" type="button" onClick={() => navigate("/experts")}>Back</Button>
                    </Form>
                </div >
            </div >
        );
    }
}

export default ModifyExpertPage