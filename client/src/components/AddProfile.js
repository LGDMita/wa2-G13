import API from '../API';
import {Button, Form} from "react-bootstrap";
import React, {useState} from "react";

import Profile from "../profile"

function AddProfile() {
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');

    async function addProfile() {
        try {
            //const profile = await API.getUserInfo(email);
            /*if (!profile) {
                alert("User with this email already exist!")
            } else {*/
            const profile = new Profile(email, name, surname);
            await API.addProfile(profile);
            alert("User successfully added!")
            //}
        } catch (error) {
            alert(error);
        }
    }

    return (
        <div className="add-profile">
            <h3>Add profile</h3>
            <Form onSubmit={e => e.preventDefault()}>
                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Email address</Form.Label>
                    <Form.Control type="email" placeholder="Email" onChange={e => setEmail(e.target.value)}/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeholder="Name" onChange={e => setName(e.target.value)}/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicSurname">
                    <Form.Label>Surname</Form.Label>
                    <Form.Control type="text" placeholder="Surname" onChange={e => setSurname(e.target.value)}/>
                </Form.Group>

                <Button variant="primary" type="submit" onClick={() => addProfile()}>
                    Add profile
                </Button>
            </Form>
        </div>
    );

}

export {AddProfile}