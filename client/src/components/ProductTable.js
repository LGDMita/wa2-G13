import API from '../API';
import React, {useEffect, useState} from 'react';
import Table from 'react-bootstrap/Table';
import "../styles/Products.css";

function ProductTable() {

    const [products, setProducts] = useState([]);
    async function load(){
        setProducts(await API.getProducts());
    }
    useEffect(() => {
        void load();
    }, []);

    return (
        <div className="table-products">
            <h3>All products</h3>
            <Table className="m-3" striped bordered hover>
                <thead>
                <tr>
                    <th>EAN</th>
                    <th>NAME</th>
                    <th>BRAND</th>
                </tr>
                </thead>
                <tbody>
                {
                    products.map(p => {
                        return (
                            <tr key={p.ean}>
                                <td>{p.ean}</td>
                                <td>{p.name}</td>
                                <td>{p.brand}</td>
                            </tr>
                        )
                    })
                }
                </tbody>
            </Table>
        </div>
    );

}

export {ProductTable}