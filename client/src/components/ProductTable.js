import API from '../API';
import React, { useEffect, useState } from 'react';
import TableWithFilterAndSort from "./TableWithFilterAndSort"

import "../styles/TableWithFilterAndSort.css"
import { Col, Container, Row } from 'react-bootstrap';

function ProductTable() {
    const columns = [
        {
            Header: "EAN",
            accessor: "ean",
        },
        {
            Header: "Name",
            accessor: "name",
            Filter: ({ column }) => <input {...column.filterProps} />,
        },
        {
            Header: "Brand",
            accessor: "Brand",
            Filter: ({ column }) => <input {...column.filterProps} />,
        }
    ];

    const [products, setProducts] = useState([]);

    async function load() {
        setProducts(await API.getProducts());
    }

    useEffect(() => {
        void load();
    }, []);

    return (
        <Container className='productTable-cnt'>
            <Row>
                <Col>
                    <h4 className='text-center'>Here you can find the list of products managed by our system</h4>
                    <div className='productTable'>
                        <TableWithFilterAndSort data={products} columns={['ean', 'name', 'brand']}/>
                    </div>
                </Col>
            </Row>
        </Container >
    );
};


export { ProductTable }