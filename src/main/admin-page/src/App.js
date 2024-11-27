import './App.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
    const [hello, setHello] = useState('')

    let ary = ['남자 코트 추천', '강남 우동 맛집', '파이썬독학']
    let [titles, setTitles] = useState(ary)

    useEffect(() => {
        axios.get('/api/admin/hello')
            .then(response => setHello(response.data))
            .catch(error => console.log(error))
    }, []);

    const sortTitles = () => {
        const sortedTitles = [...titles].sort();
        setTitles(sortedTitles);
    };

    return (
        <div className="App">
            <div className="black-nav">
                <h4>관리자 페이지 입니다.</h4>
            </div>

            <button onClick={sortTitles}>글 제목 정렬</button>

            {titles.map((title, index) => (
                <ProductList key={title} name={title} date="11월 26일" />
            ))}

            <Modal margin="20px"></Modal>

            <h4>백엔드에서 가져온 데이터입니다~~ : {hello}</h4>
        </div>
    );
}

function ProductList({ name }, { date }) {
    const [like, setLike] = useState(0);

    return (
        <div className="list">
            <h4>
                {name}
                <span onClick={() => {
                    setLike(like + 1)
                }}> 👍 </span>
                {like}
            </h4>
            <p>
                {date} 발행
            </p>
        </div>
    );
}

function Modal({ margin }) {
    const modalStyle = {
        margin: margin
    };

    return (
        <div className='modal' style={modalStyle}>
            <h4>제목</h4>
            <p>날짜</p>
            <p>상세내용</p>
        </div>
    );
}


export default App;