class Ticket {
    constructor(ticketId, profile, product, priorityLevel, expert, status, creationDate, messages) {
        this.ticketId = ticketId;
        this.profile = profile;
        this.product = product;
        this.priorityLevel = priorityLevel;
        this.expert = expert;
        this.status = status;
        this.creationDate = creationDate;
        this.messages = messages;
    }
}

export default Ticket;