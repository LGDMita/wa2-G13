import { useContext } from "react";
import { useNavigate } from 'react-router-dom';
import UserContext from "../context/UserContext";
import { Container, Nav, Navbar, Button, NavbarBrand, Anchor } from "react-bootstrap";
import API from "../API";


function Header(props){
    const {user,setUser}=useContext(UserContext);
    const navigate=useNavigate();
    return (
        <Navbar style={
            {
              "backgroundColor":"#057fb4"
            }
          } sticky="top">
            <Container fluid className="d-flex justify-content-between">
                {/*
                Common nav options
                */}
                <NavbarBrand className="justify-content-start">
                    <Anchor className="navbar-brand d-flex align-items-center text-white" href="/home">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-ticket-perforated-fill" viewBox="0 0 16 16">
                            <path d="M0 4.5A1.5 1.5 0 0 1 1.5 3h13A1.5 1.5 0 0 1 16 4.5V6a.5.5 0 0 1-.5.5 1.5 1.5 0 0 0 0 3 .5.5 0 0 1 .5.5v1.5a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 11.5V10a.5.5 0 0 1 .5-.5 1.5 1.5 0 1 0 0-3A.5.5 0 0 1 0 6V4.5Zm4-1v1h1v-1H4Zm1 3v-1H4v1h1Zm7 0v-1h-1v1h1Zm-1-2h1v-1h-1v1Zm-6 3H4v1h1v-1Zm7 1v-1h-1v1h1Zm-7 1H4v1h1v-1Zm7 1v-1h-1v1h1Zm-8 1v1h1v-1H4Zm7 1h1v-1h-1v1Z"/>
                        </svg>
                    </Anchor>
                </NavbarBrand>
                <Nav className="justify-content-center">
                {
                    //Role specific options
                    roleSpecificNavbar(user.logged,user.role)
                }
                </Nav>
                <Nav className="justify-content-end">
                    <Nav.Item>
                    {
                        //Login or logout simply
                        authButton(user.logged,navigate,props.handleLogout)
                    }
                    </Nav.Item>
                </Nav>
            </Container>
        </Navbar>
    );
}

function authButton(logged,navigate,logoutCallback){
    const handleLogout=async e=>{
        e.preventDefault();
        e.stopPropagation();
        await API.logout();
        logoutCallback();
        navigate("/home");
    }
    if(logged)  return  (<Button variant='danger' onClick={handleLogout}>Logout</Button>);
    else    return (<Button variant='success' onClick={async e=>{e.preventDefault();e.stopPropagation();navigate('/login');}}>Login</Button>);
}

function roleSpecificNavbar(logged,role){
    if(logged) switch (role) {
        case 'customer':
            return (<CustomerNavOptions/>);
        case 'manager':
            return (<ManagerNavOptions/>);
        case 'expert':
            return (<ExpertNavOptions/>);
        default:
            break;
    }
    return (<></>);
}

function CustomerNavOptions(props){
    return(
        <>
            <Nav.Link href="/tickets">
                My tickets
            </Nav.Link>
            <Nav.Link href="/purchases">
                My products
            </Nav.Link>
        </>
    );
}

function ManagerNavOptions(props){
    return(
        <>
            <Nav.Link href="/tickets">
                Tickets
            </Nav.Link>
        </>
    )
}

function ExpertNavOptions(props){
    return(
        <>
            <Nav.Link href="/tickets">
                Tickets
            </Nav.Link>
        </>
    )
}

export default Header;