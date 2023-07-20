import React, { useState, useEffect } from 'react';
import { FaArrowUp } from 'react-icons/fa';
import '../styles/ScrollToTopButton.css';

const ScrollToTopButton = () => {
    const [showButton, setShowButton] = useState(false);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    const handleScroll = () => {
        // Show the button when the user scrolls down 300 pixels or more
        setShowButton(window.scrollY > 300);
    };

    const handleScrollToTop = () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div
            className={`scroll-to-top-button ${showButton ? 'show' : ''}`}
            onClick={handleScrollToTop}
        >
            <FaArrowUp className="arrow-up-icon" />
        </div>
    );
};

export default ScrollToTopButton;
