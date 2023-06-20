import { ProductTable,SingleProduct,SingleProfile,AddProfile,EditProfile } from "../components";

function HomepagePage(props){
    return(
        <>
            <ProductTable></ProductTable>
            <SingleProduct></SingleProduct>
            <SingleProfile></SingleProfile>
            <AddProfile></AddProfile>
            <EditProfile></EditProfile>
        </>
    );
}

export default HomepagePage;