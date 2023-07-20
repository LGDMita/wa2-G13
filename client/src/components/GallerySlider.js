import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { Col, Row } from "react-bootstrap";
import { useEffect } from "react";
import React from 'react';
import "../styles/Gallery.css";

function openFileUpload() {
    document.getElementById('addfileupload').click();
}

function GallerySlider(props) {
    useEffect(() => {
        if (props.add) return () => {
            props.files.forEach(f => URL.revokeObjectURL(f.url));
        }
    });
    const addFile = file => {
        console.log("Adding file", file.target.files[0]);
        props.setFiles([...props.files, {
            file: file.target.files[0],
            type: file.target.files[0].type,
            name: file.target.files[0].name,
            url: URL.createObjectURL(file.target.files[0])
        }]);
    };
    const removeFile = url => {
        props.setFiles([...props.files.filter(f => f.url !== url)]);
        URL.revokeObjectURL(url);
    }
    const setFullScreenUrl = id => {
        const element = document.getElementById(id);
        if (element.requestFullscreen)
            element.requestFullscreen();
        else if (element.mozRequestFullScreen)   /* Firefox */
            element.mozRequestFullScreen();
        else if (element.webkitRequestFullscreen)   /* Chrome, Safari & Opera */
            element.webkitRequestFullscreen();
        else if (element.msRequestFullscreen)   /* IE/Edge */
            element.msRequestFullscreen();
    }
    const sliderSettings = {
        arrows: props.files.length > 1,
        infinite: true,
        dots: props.dots === undefined ? true : props.dots,
        lazyLoad: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        speed: 500,
        autoplay: false,
        cssEase: "linear",
        adaptiveHeight: true
    };

    return (
        <Row>
            {props.add &&
                <Col xs={props.files.length > 0 ? 6 : 12} sm={props.files.length > 0 ? 4 : 12}
                    className='justify-content-center mx-auto my-2'>
                    <div className="addfilecontainer" disabled={props.disabled}>
                        <span className="material-icons-round md-36 addfilelogo" disabled={props.disabled}
                            onClick={e => {
                                e.preventDefault();
                                e.stopPropagation();
                                openFileUpload();
                            }}>
                            upload_file
                        </span>
                        <input disabled={props.disabled} id="addfileupload" type="file" hidden
                            onChange={f => addFile(f)} />
                    </div>
                </Col>
            }
            {props.files.length > 0 &&
                <Col xs={12} sm={props.add ? 8 : 12} className='justify-content-center mx-auto my-2'>
                    <div className="mx-auto slider-container">
                        <Slider className='mx-auto' {...sliderSettings} style={{ width: "75%", height: "25%" }}>
                            {
                                props.files.map(f =>
                                    <div key={f.url} className="slidecont">
                                        {f.type.split("/")[0] === "image" ?
                                            <img alt="alt" src={f.url} id={f.url} className='img-fluid slideimg' />
                                            :
                                            <div className="slidefile justify-content-center mx-auto">
                                                <span className="material-icons-round md-3" style={{ color: "white" }}>
                                                    file_copy
                                                </span>
                                                {f.name}
                                            </div>}
                                        <div className="slideoverlay">
                                            {f.type.split("/")[0] === "image" &&
                                                <span className="material-icons-round md-36 slidefullscreen"
                                                    onClick={e => {
                                                        e.preventDefault();
                                                        e.stopPropagation();
                                                        setFullScreenUrl(f.url)
                                                    }}>
                                                    open_in_full
                                                </span>}
                                            {props.add &&
                                                <span className="material-icons-round md-36 slidedelete" onClick={e => {
                                                    e.preventDefault();
                                                    e.stopPropagation();
                                                    removeFile(f.url)
                                                }}>
                                                    delete_forever
                                                </span>
                                            }
                                            {!props.add &&
                                                <span className="material-icons-round md-36 slidedown">
                                                    file_download
                                                    <a href={f.url} download />
                                                </span>
                                            }
                                        </div>
                                    </div>)
                            }
                        </Slider>
                    </div>
                </Col>
            }
        </Row>
    )
}

export default GallerySlider;