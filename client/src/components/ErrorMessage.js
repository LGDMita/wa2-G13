import React, { useState } from 'react';
import { Alert } from 'react-bootstrap';

const ErrorMessage = ({ message, onClose }) => {
    const [show, setShow] = useState(true);

    const handleClose = () => {
        setShow(false);
        onClose();
    };

    if (show) {
        return (
            <Alert variant="danger" dismissible onClose={handleClose} className='login-alert'>
                {message}
            </Alert>
        );
    }

    return null;
};

export default ErrorMessage;
