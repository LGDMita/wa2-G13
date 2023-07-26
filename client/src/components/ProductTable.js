import API from '../API';
import React, { useEffect, useState } from 'react';
import TableWithFilterAndSort from "./TableWithFilterAndSort"
import { Spinner } from "react-bootstrap";

import "../styles/TableWithFilterAndSort.css"
import { Col, Container, Row } from 'react-bootstrap';

function ProductTable() {
    const [loading, setLoading] = useState(true);

    const [products, setProducts] = useState([]);

    async function load() {
        const productsTemp = await API.getProducts();
        const droppedEan = productsTemp.map(({ ean, ...rest }) => rest);
        setProducts(droppedEan);
        setLoading(false);
    }

    useEffect(() => {
        void load();
    }, []);

    return (
        <Container className='productTable-cnt'>
            <Row>
                <Col>
                    <h4 className='text-center'>Here you can find the list of products managed by our system</h4>
                    {loading ?
                        <Container fluid>
                            <Row>
                                <Spinner animation="border" variant="dark" className="spin-load" size="lg" />
                            </Row>
                        </Container> :
                        <div className='productTable'>
                            <TableWithFilterAndSort data={products} columns={['name', 'brand']} />
                        </div>
                    }
                </Col>
            </Row>
        </Container >
    );
};


export { ProductTable }