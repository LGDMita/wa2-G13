import API from '../API';
import {Button, InputGroup, Form} from "react-bootstrap";
import React, {useState} from "react";

function SingleProfile() {
    const [email, setEmail] = useState('');

    function getProfile() {
        try {
            const profile = API.getUserInfo(email);
            if (!profile) {
                alert(`User with email ${email} found!\nName: ${profile.name}\nSurname: ${profile.surname}`);
            } else {
                alert(`Product with ean ${email} NOT found!`)
            }
        } catch (error) {
            alert(error);
        }
    }

    return (
        <div className="single-profile">
            <h3>Search single profile</h3>
            <InputGroup className="mb-3">
                <Form.Control
                    placeholder="Insert email value"
                    aria-label="Insert email value"
                    aria-describedby="basic-addon2"
                    required={true}
                    onChange={e => setEmail(e.target.value)}
                />
                <Button variant="outline-secondary" id="button-addon2" onClick={() => getProfile()}>
                    Search
                </Button>
            </InputGroup>
        </div>
    );

}

export {SingleProfile}