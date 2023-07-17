import { useContext } from "react";
import UserContext from "../context/UserContext";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { ReactComponent as Logo } from "../logo.svg";

import "bootstrap/dist/css/bootstrap.min.css";
import "../styles/HamburgerMenu.css"

function Header(props) {
    const { user } = useContext(UserContext);
    return (
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark" className="my-menu">
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
                    <Nav.Link href="/products">Prodotti</Nav.Link>
                    {
                        roleSpecificNavbar(user.logged, user.role)
                    }
                    <NavDropdown title="My Profile" id="collasible-nav-dropdown" className="mydropdown">
                        <NavDropdown.Item href="/profile">My profile</NavDropdown.Item>
                        <NavDropdown.Divider />
                        <NavDropdown.Item href="/logout">Logout</NavDropdown.Item>
                    </NavDropdown>
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