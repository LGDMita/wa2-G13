import API from '../API';
import React, {useEffect} from 'react';
import Table from 'react-bootstrap/Table';


function ProductTable() {
    let products;
    useEffect(() => {
        const loadList = () => {
            try {
                // eslint-disable-next-line react-hooks/exhaustive-deps
                products = [];//API.getProducts();
            } catch (error) {
                alert(error);
            }
        };
        loadList();
    }, []);

    return (
        <div className="table-products">
            <h3>All products</h3>
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>EAN</th>
                    <th>NAME</th>
                    <th>BRAND</th>
                </tr>
                </thead>
                <tbody>
                {/*
                    products.map(p => {
                        return (
                            <tr>
                                <td>{p.ean}</td>
                                <td>{p.name}</td>
                                <td>{p.brand}</td>
                            </tr>
                        )
                    })*/
                }
                </tbody>
            </Table>
        </div>
    );

}

export {ProductTable}