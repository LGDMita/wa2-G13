import API from '../API';
import {Button, Form} from "react-bootstrap";
import React, {useState} from "react";

import Profile from "../profile"

function EditProfile() {
    const [oldEmail, setOldEmail] = useState("");
    const [newEmail, setNewEmail] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");

    async function editProfile() {
        try {
            if (oldEmail !== "" && newEmail !== "" && name !== "" && surname !== "") {
                const profile = new Profile(newEmail, name, surname);
                await API.updateProfile(oldEmail, profile)
                alert("User successfully modified!")
            } else {
                alert(`Please, complete all the field before continue!`);
            }
        } catch (error) {
            alert(error);
        }
    }

    return (
        <div className="edit-profile">
            <h3>Edit profile</h3>
            <Form onSubmit={e => e.preventDefault()}>

                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Old email address</Form.Label>
                    <Form.Control type="email" placeholder="Email" onChange={e => setOldEmail(e.target.value)}/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>New email address (confirm the old one, if you don't want to change it)</Form.Label>
                    <Form.Control type="email" placeholder="Email" onChange={e => setNewEmail(e.target.value)}/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeholder="Name" onChange={e => setName(e.target.value)}/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicSurname">
                    <Form.Label>Surname</Form.Label>
                    <Form.Control type="text" placeholder="Surname" onChange={e => setSurname(e.target.value)}/>
                </Form.Group>

                <Button variant="primary" type="submit" onClick={() => editProfile()}>
                    Edit profile
                </Button>
            </Form>
        </div>
    );

}

export {EditProfile}