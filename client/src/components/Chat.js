import { Button, Col, Modal, Row, Container } from "react-bootstrap";
import { useContext, useEffect, useState } from "react";
import UserContext from "../context/UserContext";
import "../styles/Chat.css";
import { GallerySlider } from ".";
import API from "../API";
import time from "../lib/time";
import { Spinner } from "react-bootstrap";

function Message(props) {
    return (
        <div className={"message-box message-" + props.message.position}>
            <div className="message-header">
                {props.message.title}
            </div>
            <div className="message-body">
                {props.message.text}
            </div>
            <div className="message-footer">
                {props.message.data.length > 0 && <div className="message-files">
                    <GallerySlider add={false} files={props.message.data} />
                </div>}
                <div className="message-time">{time.format(props.message.datetime)}</div>
            </div>
        </div>
    )
}

function MessageList(props) {
    return (
        <div id="messages-list" className="messages-container">
            {
                props.messages.map(m => {
                    return (
                        <Row key={m.messageId}>
                            <Message message={m} key={m.messageId} />
                        </Row>
                    )
                })
            }
        </div>
    );
}

function NewMessage(props) {
    const { user } = useContext(UserContext);
    const [newMessageText, setNewMessageText] = useState("");
    const [files, setFiles] = useState([]);
    const [loading, setLoading] = useState(false);
    const handleMessage = async () => {
        try {
            // modify file structure, actually useless now
            //const attachments=files.map(f=>{});
            if (newMessageText.length > 0 || files.length > 0) {
                setLoading(true);
                await API.sendMessage(props.ticket.ticketId, user.role === 'customer', newMessageText, files);
                files.forEach(f => URL.revokeObjectURL(f.url));
                setFiles([]);
                setNewMessageText("");
                props.handleNewMessage();
                setLoading(false);
            }
        } catch (exc) {
            console.log("Error sending message!! ", JSON.stringify(exc));
            props.setError({ title: "Couldn't send the message", detials: exc.detail });
        }
    }
    //just to unmount URLs and fix leak memory
    useEffect(() => {
        return () => { files.forEach(f => URL.revokeObjectURL(f.url)); }
    }, []);
    return (
        <div className="newmessage-container">
            <Row>
                <Col xs={10}>
                    <Row>
                        <Col xs={files.length > 0 ? 8 : 11}>
                            <div className="newmessage-box" disabled={props.disabled}>
                                <textarea disabled={props.disabled} value={newMessageText} style={{
                                    border: "none",
                                    background: "transparent",
                                    "outline": 0,
                                    width: "100%",
                                    height: "100%"
                                }}
                                    placeholder="Type here...!" onChange={e => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        setNewMessageText(e.target.value);
                                    }} />
                            </div>
                        </Col>
                        <Col xs={files.length > 0 ? 4 : 1}>
                            <GallerySlider disabled={props.disabled} add={true} files={files} setFiles={setFiles} />
                        </Col>
                    </Row>
                </Col>
                <Col xs={2}>
                    <div className="newmessage-send" disabled={props.disabled} onClick={e => {
                        e.preventDefault();
                        e.stopPropagation();
                        if (!props.disabled) handleMessage();
                    }}>
                        <span disabled={props.disabled} className="material-icons-round md-24 sendmessagebutton">
                            send
                        </span>
                        {
                            loading ?
                                <Container fluid>
                                    <Row>
                                        <Spinner animation="border" variant="dark" size="lg" />
                                    </Row>
                                </Container>
                                :
                                <></>
                        }
                    </div>
                </Col>
            </Row>
        </div>
    )
}

function StatusBasedOptions(props) {
    const { user } = useContext(UserContext);

    // All the buttons written once so they just have to be put together later
    function StartProgress(props) {
        return (user.role !== "customer" &&
            <Button size="sm" variant="success" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.handleChangeStatus("in_progress");
            }}>
                Start progress!
            </Button>
        );
    }

    function StopProgress(props) {
        return (
            <Button size="sm" variant="danger" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.handleChangeStatus("open");
            }}>
                Stop progress!
            </Button>
        );
    }

    function ResolveIssue(props) {
        return (
            <Button size="sm" variant="info" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.handleChangeStatus("resolved");
            }}>
                Resolve issue!
            </Button>
        );
    }

    function ReopenIssue(props) {
        return (
            <Button size="sm" variant="dark" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.handleChangeStatus("reopened");
            }}>
                Reopen issue!
            </Button>
        );
    }

    function CloseIssue(props) {
        return (
            <Button size="sm" variant="secondary" onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                props.handleChangeStatus("closed");
            }}>
                Close issue!
            </Button>
        );
    }

    //specific status set of options
    function OpenTicketOptions(props) {
        return (
            <>
                <StartProgress handleChangeStatus={props.handleChangeStatus} />
                <ResolveIssue handleChangeStatus={props.handleChangeStatus} />
                <CloseIssue handleChangeStatus={props.handleChangeStatus} />
            </>
        );
    }

    function InProgressTicketOptions(props) {
        return (
            <>
                <StopProgress handleChangeStatus={props.handleChangeStatus} />
                <CloseIssue handleChangeStatus={props.handleChangeStatus} />
                <ResolveIssue handleChangeStatus={props.handleChangeStatus} />
            </>
        );
    }

    function ClosedTicketOptions(props) {
        return (
            <>
                <ReopenIssue handleChangeStatus={props.handleChangeStatus} />
            </>
        );
    }

    function ReopenedTicketOptions(props) {
        return (
            <>
                <StartProgress handleChangeStatus={props.handleChangeStatus} />
                <ResolveIssue handleChangeStatus={props.handleChangeStatus} />
                <CloseIssue handleChangeStatus={props.handleChangeStatus} />
            </>
        );
    }

    function ResolvedTicketOptions(props) {
        return (
            <>
                <ReopenIssue handleChangeStatus={props.handleChangeStatus} />
                <CloseIssue handleChangeStatus={props.handleChangeStatus} />
            </>
        );
    }

    switch (props.ticket.status) {
        case 'open':
            return (<OpenTicketOptions handleChangeStatus={props.handleChangeStatus} />);
        case 'in_progress':
            return (<InProgressTicketOptions handleChangeStatus={props.handleChangeStatus} />);
        case 'closed':
            return (<ClosedTicketOptions handleChangeStatus={props.handleChangeStatus} />);
        case 'reopened':
            return (<ReopenedTicketOptions handleChangeStatus={props.handleChangeStatus} />);
        case 'resolved':
            return (<ResolvedTicketOptions handleChangeStatus={props.handleChangeStatus} />);
        default:
            break;
    }
    return (<></>);
}

function ChatHeader(props) {
    const { user } = useContext(UserContext);
    return (
        <div className="chat-header">
            <Row>
                <Col xs={3}>
                    <div className="chat-header-receiver">
                        {user.role === "customer" ? (props.ticketToAssign ? "Ticket needs to be assigned!" : props.ticket.expert.username) : props.ticket.profile.username}
                    </div>
                </Col>
                <Col xs={9}>
                    <div className="chat-header-status-management">
                        <StatusBasedOptions ticket={props.ticket} handleChangeStatus={props.handleChangeStatus} />
                    </div>
                </Col>
            </Row>
        </div>
    )
}

function Chat(props) {
    const { user } = useContext(UserContext);
    //some messages just for test frontend, actual ones will be blobs so they will be treated differently as well
    /* const frontendTestMessages = [{
        datetime: "22/02/1999 14:22",
        fromUser: false,
        text: "Hey customer how may I assist u?",
        files: [{
            type: "image/png",
            name: "Stark",
            url: 'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/7/73/SMH_Mentor_6.png'
        }, {
            type: "image/jpg",
            name: "Banner",
            url: 'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/4/4f/BruceHulk-Endgame-TravelingCapInPast.jpg'
        }, {
            type: "pdf",
            name: "Myfile",
            url: "file.pdf"
        }, {
            type: "image/jpg",
            name: "Thor",
            url: 'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/9/98/ThorFliesThroughTheAnus.jpg'
        }, {
            type: "image/png",
            name: "Rogers",
            url: 'https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/7/7c/Cap.America_%28We_Don%27t_Trade_Lives_Vision%29.png'
        }],
    }, {
        datetime: "22/02/1999 14:25",
        fromUser: true,
        text: "Hey expert my TV is not working"
    }, {
        datetime: "22/02/1999 14:25",
        fromUser: false,
        text: "Have you tried to plug it and unplug it?"
    }, {
        datetime: "22/02/1999 14:25",
        fromUser: true,
        text: "Oh no actually thanks for the suggestion!"
    }, {
        datetime: "22/02/1999 14:25",
        fromUser: true,
        text: "I tried and it's working currently, thanks!"
    }]; */
    const [messages, setMessages] = useState([]);
    const [error, setError] = useState(undefined);
    const [lastTimeout, setLastTimeout] = useState(-1);
    const cleanupFiles = (calledBy) => {
        messages.forEach(m => m.files.forEach(f => URL.revokeObjectURL(f.url)));
    }
    const handleChangeStatus = async newStatus => {
        try {
            await API.changeTicketStatus(props.ticket.ticketId, newStatus);
            props.setRefreshTickets(!props.setRefreshTickets);
        } catch (exc) {
            setError({ title: "Couldn't update the ticket status", details: exc.detail });
        }
    }
    const handleNewMessage = () => {
        props.setRefresh(!props.refresh);
        setTimeout(() => document.getElementById("messages-list").scrollTop = document.getElementById("messages-list").scrollHeight, 250);
    }
    const getNewMessages = async (scrollDown) => {
        try {
            clearTimeout(lastTimeout);
            if (messages.length > 0 && messages.find(m => m.ticketId !== props.ticket.ticketId)) {
                setMessages([]);
            }
            const mex = await API.getMessages(props.ticket.ticketId);
            const newMexs = mex.filter(m => !messages.find(me => me.messageId === m.messageId) && m.ticketId === props.ticket.ticketId);
            const mexs = [];
            for (const m of newMexs) {
                const mexa = m;
                mexa.files = [];
                for (const a of m.attachments) {
                    const base64Response = await fetch("data:" + a.type + ";base64," + a.dataBin);
                    const byteblob = await base64Response.blob();
                    const genurl = URL.createObjectURL(byteblob);
                    mexa.files.push({ url: genurl, type: a.type, name: "defaultFilename" });
                }
                mexs.push(mexa);
            }
            if (messages.length > 0 && messages.find(m => m.ticketId !== props.ticket.ticketId)) {
                setMessages([...mexs]);
            }
            else {
                setMessages([...messages, ...mexs]);
            }
            if (scrollDown) setTimeout(() => document.getElementById("messages-list").scrollTop = document.getElementById("messages-list").scrollHeight, 100);
            setLastTimeout(setTimeout(() => props.setRefresh(!props.refresh), 1500));
        } catch (error) {
            console.log("Got error while getting new messages " + JSON.stringify(error));
            cleanupFiles("error");
            setMessages([]);
        }
    }
    useEffect(() => {
        cleanupFiles("use effect ticket id with id " + props.ticket.ticketId);
        getNewMessages(true);
        return () => cleanupFiles("use effect clean ticket id");
    }, [props.ticket.ticketId]);
    useEffect(() => {
        getNewMessages(document.getElementById("messages-list").scrollTop === document.getElementById("messages-list").scrollHeight);
    }, [props.refresh]);
    return (
        <Row>
            <ChatHeader ticket={props.ticket}
                ticketToAssign={props.ticket.status === "open" || props.ticket.status === "reopened" || props.ticket.expert === null}
                handleChangeStatus={handleChangeStatus} />
            <MessageList className='message-list'
                lockable={true}
                toBottomHeight={'100%'}
                messages={messages.map(m => {
                    const pos = (m.fromUser && user.role === "customer") || (!m.fromUser && user.role !== "customer") ? "right" : "left";
                    return {
                        position: pos,
                        type: "text",
                        title: m.fromUser ? "customer" : "expert",
                        text: m.text,
                        data: m.files ? m.files : [],
                        datetime: m.datetime,
                        messageId: m.messageId
                    }
                })} />
            <NewMessage setError={setError}
                disabled={false}
                ticket={props.ticket} handleNewMessage={handleNewMessage} />
            {error &&
                <Modal show={true} onHide={() => setError(undefined)} size="md"
                    aria-labelledby="contained-modal-title-vcenter" centered>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">{error.title}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>{error.details}</Modal.Body>
                </Modal>
            }
        </Row>
    );
}

export default Chat;