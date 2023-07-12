import React from "react";
import { Nav, Navbar, NavDropdown } from "react-bootstrap";
import { ReactComponent as Logo } from "../logo.svg";
import TokenManager from '../TokenManager';

import "bootstrap/dist/css/bootstrap.min.css";
import "../styles/HamburgerMenu.css"

export default function HamburgerMenu() {
  const tokenManager = TokenManager();

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
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="mr-auto">
          <Nav.Link href="/products">Prodotti</Nav.Link>
          {tokenManager.amIExpertOrManager() ? <Nav.Link href="/tickets">All tickets</Nav.Link> : null}
          {tokenManager.amIManager() ? <Nav.Link href="#pricing">All experts</Nav.Link> : null}
          <NavDropdown title="My Profile" id="collasible-nav-dropdown">
            <NavDropdown.Item href="/profile">My profile</NavDropdown.Item>
            <NavDropdown.Divider />
            <NavDropdown.Item href="/logout">Logout</NavDropdown.Item>
          </NavDropdown>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}
