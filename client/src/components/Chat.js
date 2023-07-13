import { Button, Col, Container, Modal, Row } from "react-bootstrap";
import { MessageBox,MessageList as MessageList_ } from "react-chat-elements";
import { useContext, useState } from "react";
import UserContext from "../context/UserContext";
import "../styles/Chat.css";
import { GallerySlider } from ".";

function Message(props){
    return(
        <div className={"message-box message-"+props.message.position}>
            <div className="message-header">
                {props.message.title}
            </div>
            <div className="message-body">
                {props.message.text}
            </div>
            <div className="message-footer">
                {props.message.data.length>0 && <div className="message-files">
                    <GallerySlider add={false} files={props.message.data}/>
                </div>}
                <div className="message-time">{props.message.datetime}</div>
            </div> 
        </div>
    )
}

function MessageList(props){
    return(
        <div className="messages-container">
            {
                props.messages.map(m=>{
                    return(
                        <Row>
                            <Message message={m}/>
                        </Row>
                    )
                })
            }
        </div>
    );
}

function NewMessage(props){
    const [newMessageText,setNewMessageText]=useState("");
    const [files,setFiles]=useState([]);
    const [error,setError]=useState("");
    const handleMessage=async()=>{
        try {
            setError("Blabla");
            //const attachments=files.map(f=>{});
            //await API.sendMessage(props.ticketId,user.role==='CUSTOMER',newMessageText,attachments);
            props.setRefresh(!props.refresh);
        } catch (error) {
            setError(error);
        }
    }
    return(
        <div className="newmessage-container">
            <Row>
                <Col xs={10}>
                    <div className="newmessage-box">
                        <textarea disabled={props.ticket.status!=="IN PROGRESS"} value={newMessageText} style={{border:"none", background: "transparent", "outline": 0,width:"100%",height:"100%"}}
                        placeholder="Type here...!" onChange={e=>{e.preventDefault();e.stopPropagation();setNewMessageText(e.target.value);}}/>
                    </div>
                </Col>
                <Col xs={2}>
                    <div className="newmessage-send" onClick={e=>{e.preventDefault();e.stopPropagation();handleMessage();}}>
                        <span disabled={props.ticket.status!=="IN PROGRESS"} className="material-icons-round md-36 sendmessagebutton">
                            send
                        </span>
                    </div>
                </Col>
            </Row>
            <Row>
                <GallerySlider disabled={props.ticket.status!=="IN PROGRESS"} add={true} files={files} setFiles={setFiles}/>
            </Row>
            {error!=="" &&
                <Modal show={true} onHide={()=>setError("")} size="md" aria-labelledby="contained-modal-title-vcenter" centered>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">Couldn't send the message!</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>{error}</Modal.Body>
                </Modal>
            }
        </div>
    )
}

function StatusBasedOptions(props){
    const {user,setUser}=useContext(UserContext);
    // All the buttons written once so they just have to be put together later
    function StartProgress(props){
        return(
            <Button size="sm" disabled={user.role==='CUSTOMER'} variant="success">
                Start progress!
            </Button>
        );
    }
    function StopProgress(props){
        return(
            <Button size="sm" variant="danger">
                Stop progress!
            </Button>
        );
    }
    function ResolveIssue(props){
        return(
            <Button size="sm" variant="info">
                Resolve issue!
            </Button>
        );
    }
    function ReopenIssue(props){
        return(
            <Button size="sm" variant="dark">
                Reopen issue!
            </Button>
        );
    }
    function CloseIssue(props){
        return(
            <Button size="sm" variant="secondary">
                Close issue!
            </Button>
        );
    }
    //specific status set of options
    function OpenTicketOptions(props){
        return(
            <>
                <StartProgress/>
                <ResolveIssue/>
                <CloseIssue/>
            </>
        );
    }
    function InProgressTicketOptions(props){
        return(
            <>
                <StopProgress/>
                <CloseIssue/>
                <ResolveIssue/>
            </>
        );
    }
    function ClosedTicketOptions(props){
        return(
            <>
                <ReopenIssue/>
            </>
        );
    }
    function ReopenedTicketOptions(props){
        return(
            <>
                <StartProgress/>
                <ResolveIssue/>
                <CloseIssue/>
            </>
        );
    }
    function ResolvedTicketOptions(props){
        return(
            <>
                <ReopenIssue/>
                <CloseIssue/>
            </>
        );
    }
    switch (props.ticket.status) {
        case 'OPEN':
            return (<OpenTicketOptions/>);
        case 'IN PROGRESS':
            return (<InProgressTicketOptions/>);
        case 'CLOSED':
            return (<ClosedTicketOptions/>);
        case 'REOPENED':
            return (<ReopenedTicketOptions/>);
        case 'RESOLVED':
            return (<ResolvedTicketOptions/>);
        default:
            break;
    }
    return(<></>);
}

function ChatHeader(props){
    const {user,setUser}=useContext(UserContext);
    return(
        <div className="chat-header">
            <Row>
                <Col xs={3}>
                    <div className="chat-header-receiver">
                        {user.role==="CUSTOMER"?(props.ticket.status==="OPEN"?"Ticket needs to be assigned!":props.ticket.expert.username):props.ticket.profile.username}
                    </div>
                </Col>
                <Col xs={9}>
                    <div className="chat-header-status-management">
                        <StatusBasedOptions ticket={props.ticket}/>
                    </div>
                </Col>
            </Row>
        </div>
    )
}

function Chat(props){
    const {user,setUser}=useContext(UserContext);
    //some messages just for test frontend, actual ones will be blobs so they will be treated differently as well
    const [messages,setMessages]=useState([{
        datetime: "22/02/1999 14:22",
        fromUser:false,
        text:"Hey customer how may I assist u?",
        files:[/*{
            type:"photo",
            name:"Stark",
            url:'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/7/73/SMH_Mentor_6.png'
        },{
            type:"photo",
            name:"Banner",
            url:'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/4/4f/BruceHulk-Endgame-TravelingCapInPast.jpg'
        },{
            type:"pdf",
            name:"Myfile",
            url:"file.pdf"
        }{
            type:"photo",
            name:"Thor",
            url:'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/9/98/ThorFliesThroughTheAnus.jpg'
        },{
            type:"photo",
            name:"Rogers",
            url:'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/7/7c/Cap.America_%28We_Don%27t_Trade_Lives_Vision%29.png'
        }*/],
    },{
        datetime: "22/02/1999 14:25",
        fromUser:true,
        text:"Hey expert this is my issue"
    },{
        datetime: "22/02/1999 14:25",
        fromUser:true,
        text:"Hey expert this is my issue"
    },{
        datetime: "22/02/1999 14:25",
        fromUser:true,
        text:"Hey expert this is my issue"
    },{
        datetime: "22/02/1999 14:25",
        fromUser:true,
        text:"Hey expert this is my issue"
    }]);
    return(
        <Row>
            <ChatHeader ticket={props.ticket}/>
            <MessageList className='message-list'
                lockable={true}
                toBottomHeight={'100%'}
                messages={messages.map(m=>{
                    const pos=(m.fromUser && user.role==="customer") || (!m.fromUser && user.role!=="customer") ? "right" : "left";
                    return {
                    position: pos,
                    type: "text",
                    title: m.fromUser? "customer" : "expert",
                    text: m.text,
                    data: m.files? m.files: [],
                    datetime: m.datetime,
                }})}/>
            <NewMessage ticket={props.ticket} refresh={props.refresh} setRefresh={props.setRefresh}/>
        </Row>
    );
}

export default Chat;