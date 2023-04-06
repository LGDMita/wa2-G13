import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';


import {ProductTable} from "./components/ProductTable";
import {SingleProduct} from "./components/SingleProduct";
import {SingleProfile} from "./components/SingleProfile";
import {AddProfile} from "./components/AddProfile";
import {EditProfile} from "./components/EditProfile";

function App() {
    return (
        <div className="main-div">
            <ProductTable></ProductTable>
            <SingleProduct></SingleProduct>
            <SingleProfile></SingleProfile>
            <AddProfile></AddProfile>
            <EditProfile></EditProfile>
        </div>
    );
}

export default App;
