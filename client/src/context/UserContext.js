import React from "react";

const UserContext=React.createContext({logged:false,role:undefined,username:'',pwd:''});

export default UserContext;