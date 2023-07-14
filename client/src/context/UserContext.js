import React from "react";
const UserContext= React.createContext(
    {id:'', logged:false, role:undefined, username:'', email: '', name: '', surname:'' }
);
export default UserContext;