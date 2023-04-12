import API from '../API';
import {Button, InputGroup, Form} from "react-bootstrap";
import React, {useState} from "react";

function SingleProduct() {
    const [ean, setEan] = useState(null);

    async function getProduct() {
        try {
            if (ean !== null && ean !== "") {
                const product = await API.getProduct(ean);
                if (product.ean !== undefined) {
                    alert(`Product with ean ${ean} found!\nName: ${product.name}\nBrand: ${product.brand}`);
                }
            }
            else {
                alert(`Please, insert EAN before continue!`);
            }
        } catch (error) {
            alert(error);
        }
    }

    return (
        <div className="single-product">
            <h3>Search single product</h3>
            <InputGroup className="mb-3">
                <Form.Control
                    placeholder="Insert EAN value"
                    aria-label="Insert EAN value"
                    aria-describedby="basic-addon2"
                    required={true}
                    onChange={e => setEan(e.target.value)}
                />
                <Button variant="outline-secondary" id="button-addon2" onClick={() => getProduct()}>
                    Search
                </Button>
            </InputGroup>
        </div>
    );

}

export {SingleProduct}