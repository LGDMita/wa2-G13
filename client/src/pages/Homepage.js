import { useContext } from "react";
import { ProductTable,SingleProduct,SingleProfile,AddProfile,EditProfile } from "../components";
import UserContext from "../context/UserContext";

function HomepagePage(props){
    const {user,setUser}=useContext(UserContext);
    return CustomerHomepagePage();
    //if(user.role==='customer') return CustomerHomepagePage();
    //else return(<></>);
}

function CustomerHomepagePage(props){
    return(
        <>
            <SingleProduct/>
            <ProductTable/>
        </>
    );
}

export default HomepagePage;