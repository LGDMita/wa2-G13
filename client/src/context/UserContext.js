import React from "react";
const UserContext= React.createContext(
    {id:'', logged:false, role:undefined, username:'', pwd:''}
);
export default UserContext;