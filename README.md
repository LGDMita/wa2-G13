# Web Applications II - Group 13 - Lab02

Group members:
 - 301247, MOHAMED AMINE HAMDI
 - 303474, LUIGI GIUSEPPE DIEGO MOLINENGO
 - 304684, GIOVANNI GENNA
 - 306151, MASSIMILIANO PELLEGRINO

GitHub Repository: https://github.com/MolinengoLuigi/wa2-G13


### TICKETING APIs LIST
- GET `/API/tickets/`
  - Request parameters: none
  - Request body: none
  - Result:
    - status: `200`
      - body: `List<TicketDTO>`
- GET `/API/tickets/{ticketId}`
  - Request parameters: ID of the desired ticket
  - Request body: none
  - Result:
    - status: `200`
      - body: `TicketDTO`
    - status: `404` if ticket not found
- GET `/API/tickets/API/tickets/?ean={ean}&profileId={profileId}&priorityLevel={priorityLevel}&expertId={expertId}&status={status}&creationDateStart={creationDateStart}&creationDateStop={creationDateStop}`
  - Request parameters: ean, profileId, priorityLevel, expertId, status, creationDateStart & creationDateStop to use for searching the tickets
  - Request body: none
  - Result:
    - status: `200`
        - body: `List<TicketDTO>`
    - status: `422` if priorityLevel outside `[0-4]` or if status not in `[open-closed-resolved-in_progress-reopened]` or if profileId not in email format
- PUT `/API/ticket/`
  - Request parameters: none
  - Request body: `TicketDTO` ticket to modify
  - Result:
    - status: `200`
        - body: `true` if ticket exist and modify succeeds
    - status: `422` if ticket validation fails
    - status: `404` if no ticket are found with the passed ticketId

## MESSAGE APIS

- POST `/API/tickets/{ticketId}/messages`
  - Request parameters: none
  - Request body: 'multipart/form-data' request like {attachments: [list of files], fromUser : ['False' or 'True'], text: 'string...'}
  - Result:
    - status: `201`
      - body: `Long`
    - status: `404`
      - body: `Ticket non existant`
    - status: `405`
      - body: `Media entity not processable`

- GET `/API/tickets/{ticketId}/messages`
  - Request parameters: none
  - Request body: none
  - Result:
    - status: `200`
      - body: `List<MessageDTO>`
    - status: `404`
      - body: `Ticket non existant`
    
## TicketHistory APIS

- GET `/API/tickets/{ticketId}/history`
  - Request parameters: none
  - Request body: none
  - Result:
    - status: `200`
      - body: `List<TicketHistoryDTO>`
    - status: `404`
      - body: `Ticket non existant`
    
## Expert apis

- GET `/API/experts`
  - Request parameters: none
  - Request body: none
  - Result:
    - status: `200`
      - body: `List<ExpertDTO>`