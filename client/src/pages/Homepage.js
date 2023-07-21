import { useContext } from "react";
import { ProductTable } from "../components";
import UserContext from "../context/UserContext";

function HomepagePage() {
    const { user } = useContext(UserContext);
    if (user.role === 'customer') return CustomerHomepagePage();
    else return (<ProductTable />);
}

function CustomerHomepagePage(props) {
    return (
        <ProductTable />
    );
}

export default HomepagePage;