import { useContext } from "react";
import UserContext from "../context/UserContext";
import { Button, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { ReactComponent as Logo } from "../logo.svg";
import { useNavigate } from "react-router-dom";

import "../styles/HamburgerMenu.css"

function Header(props) {
    const { user } = useContext(UserContext);
    const navigate = useNavigate();
    return (
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark" className="my-menu" fixed="top">
            <Navbar.Brand href="/" className="logo-title">
                <Logo
                    alt=""
                    width="40"
                    height="50"
                    className="d-inline-block align-top"
                />
                <span>MyTicketManager</span>
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="responsive-navbar-nav" />
            <Navbar.Collapse id="responsive-navbar-nav" className="mynav">
                <Nav>
                    {
                        roleSpecificNavbar(user.logged, user.role)
                    }
                    <div className="mydropdown">
                        {
                            props.logged ?
                                <NavDropdown title={"Hi, " + props.name + "!"} id="collasible-nav-dropdown">
                                    <NavDropdown.Item href="/userInfo">User info</NavDropdown.Item>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item onClick={() => props.handleLogout()}>Logout</NavDropdown.Item>
                                </NavDropdown>
                                :
                                <Button variant='success' onClick={
                                    async e => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        navigate('/login');
                                    }
                                } className="login-signup-btn">Login/Signup</Button>
                        }
                    </div>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    );
}

function roleSpecificNavbar(logged, role) {
    if (logged) switch (role) {
        case 'customer':
            return (<CustomerNavOptions />);
        case 'manager':
            return (<ManagerNavOptions />);
        case 'expert':
            return (<ExpertNavOptions />);
        default:
            break;
    }
    return (<></>);
}

function CustomerNavOptions(props) {
    return (
        <>
            <Nav.Link href="/tickets">
                My tickets
            </Nav.Link>
            <Nav.Link href="/purchases">
                My purchases
            </Nav.Link>
        </>
    );
}

function ManagerNavOptions(props) {
    return (
        <>
            <Nav.Link href="/tickets">
                Tickets
            </Nav.Link>
            <Nav.Link href="/experts">
                Manage experts
            </Nav.Link>
        </>
    )
}

function ExpertNavOptions(props) {
    return (
        <>
            <Nav.Link href="/tickets">
                Tickets
            </Nav.Link>
        </>
    )
}

export default Header;