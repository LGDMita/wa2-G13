import React, { useState, useEffect } from 'react';
import { Table, InputGroup, FormControl, Button, ButtonGroup } from 'react-bootstrap';
import { BsArrowUp, BsArrowDown, BsArrowRightCircleFill } from 'react-icons/bs'; // Import Bootstrap Icons
import { Link } from 'react-router-dom';

const TableWithFilterAndSort = (props) => {
    const [data, setData] = useState([]);
    useEffect(() => {
        setData(props.data);
    }, [props.data]);

    const [filter, setFilter] = useState('');
    const [sortKey, setSortKey] = useState(null);
    const [sortOrder, setSortOrder] = useState('asc');
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(30); // Change this value as needed

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
        setCurrentPage(1); // Reset to the first page when changing the filter
    };

    const handleSort = (key) => {
        if (sortKey === key) {
            // If clicking the same key again, reverse the order
            setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
        } else {
            // If clicking a different key, set the new key and default to ascending order
            setSortKey(key);
            setSortOrder('asc');
        }
    };

    const filteredData = data.filter((item) =>
        Object.values(item).some((value) => {
            if (value === null || value === undefined) return false;
            return value.toString().toLowerCase().includes(filter.toLowerCase());
        })
    );

    const sortedData = sortKey
        ? filteredData.sort((a, b) => {
            const keyA = a[sortKey];
            const keyB = b[sortKey];
            if (keyA < keyB) return sortOrder === 'asc' ? -1 : 1;
            if (keyA > keyB) return sortOrder === 'asc' ? 1 : -1;
            return 0;
        })
        : filteredData;

    // Calculate the index of the last item on the current page
    const indexOfLastItem = currentPage * itemsPerPage;
    // Calculate the index of the first item on the current page
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    // Get the current items to display on the current page
    const currentItems = sortedData.slice(indexOfFirstItem, indexOfLastItem);

    // Logic for pagination buttons
    const totalPages = Math.ceil(sortedData.length / itemsPerPage);

    const handleFirstPage = () => {
        setCurrentPage(1);
    };

    const handleLastPage = () => {
        setCurrentPage(totalPages);
    };

    const handlePrevPage = () => {
        setCurrentPage((prevPage) => Math.max(prevPage - 1, 1));
    };

    const handleNextPage = () => {
        setCurrentPage((prevPage) => Math.min(prevPage + 1, totalPages));
    };

    // Funzione che restituisce un'icona per indicare l'ordinamento attuale
    const getSortIcon = (columnKey) => {
        if (sortKey === columnKey) {
            return sortOrder === 'asc' ? <BsArrowUp /> : <BsArrowDown />;
        }
        return null;
    };

    return (
        <div className="table-responsive">
            <div>
                <InputGroup className="mb-3">
                    <FormControl
                        type="text"
                        value={filter}
                        onChange={handleFilterChange}
                        placeholder="Filter by value..."
                    />
                </InputGroup>
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            {props.columns.map(col =>
                                <th key={col} onClick={() => handleSort(col)}>
                                    {col.toUpperCase()} {getSortIcon(col)}
                                </th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        {currentItems.map((item) => (
                            <tr key={item[props.columns[0]]}>
                                {props.columns.map((col, index) => {
                                    if(props.actionLink)
                                        if(index === props.columns.length -1)
                                            return <td key={col}><Link to={`${props.actionLink}${item[props.columns[0]]}`}><Button><BsArrowRightCircleFill/></Button></Link></td>
                                        else
                                            return <td key={col}>{item[col]}</td>;
                                    else
                                        return <td>{item[col]}</td>;
                                })}
                            </tr>
                        ))}
                    </tbody>
                </Table>
                <div>
                    <ButtonGroup>
                        <Button onClick={handleFirstPage} disabled={currentPage === 1}>
                            First
                        </Button>
                        <Button onClick={handlePrevPage} disabled={currentPage === 1}>
                            Prev
                        </Button>
                        <Button onClick={handleNextPage} disabled={currentPage === totalPages}>
                            Next
                        </Button>
                        <Button onClick={handleLastPage} disabled={currentPage === totalPages}>
                            Last
                        </Button>
                    </ButtonGroup>
                </div>
            </div>
        </div>
    );
};

export default TableWithFilterAndSort;