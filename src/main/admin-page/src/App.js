import './App.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import LineChart from './LineChart'; // 새 컴포넌트 임포트

import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';

// Chart.js를 사용하기 위해 기본 설정 등록
ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

function App() {
    const [statisticsData, setStatisticsData] = useState(null);

    useEffect(() => {
        axios.get('/api/admin/statistics')
            .then(response => setStatisticsData(response.data))
            .catch(error => console.log(error))
    }, [])

    // 차트 데이터 준비
    const chartData = {
        IMAGE: statisticsData?.IMAGE || {},
        EMOTION: statisticsData?.EMOTION || {},
        CAMPFIRE: statisticsData?.CAMPFIRE || {},
        ACTIVE_CAMPFIRE: statisticsData?.ACTIVE_CAMPFIRE || {},
        MEMBER: statisticsData?.MEMBER || {},
        LOG: statisticsData?.LOG || {}
    };

    return (
        <div className="App">
            <div className="black-nav">
                <h4>관리자 페이지 입니다.</h4>
            </div>

            <div className="charts-container">
                {/* 첫 번째 줄에 3개의 차트 */}
                <div className="chart-row">
                    {chartData.IMAGE && (
                        <div className="chart">
                            <LineChart
                                data={chartData.IMAGE}
                                chartTitle="이미지 총 개수"
                                borderColor="rgba(255, 99, 132, 1)"
                                backgroundColor="rgba(255, 99, 132, 0.2)"
                            />
                        </div>
                    )}

                    {chartData.EMOTION && (
                        <div className="chart">
                            <LineChart
                                data={chartData.EMOTION}
                                chartTitle="감정 표현 총 개수"
                                borderColor="rgba(54, 162, 235, 1)"
                                backgroundColor="rgba(54, 162, 235, 0.2)"
                            />
                        </div>
                    )}

                    {chartData.CAMPFIRE && (
                        <div className="chart">
                            <LineChart
                                data={chartData.CAMPFIRE}
                                chartTitle="캠프파이어 총 개수"
                                borderColor="rgba(75, 192, 192, 1)"
                                backgroundColor="rgba(75, 192, 192, 0.2)"
                            />
                        </div>
                    )}
                </div>

                {/* 두 번째 줄에 3개의 차트 */}
                <div className="chart-row">
                    {chartData.ACTIVE_CAMPFIRE && (
                        <div className="chart">
                            <LineChart
                                data={chartData.ACTIVE_CAMPFIRE}
                                chartTitle="활성 캠프파이어 개수"
                                borderColor="rgba(153, 102, 255, 1)"
                                backgroundColor="rgba(153, 102, 255, 0.2)"
                            />
                        </div>
                    )}

                    {chartData.MEMBER && (
                        <div className="chart">
                            <LineChart
                                data={chartData.MEMBER}
                                chartTitle="회원 수"
                                borderColor="rgba(255, 159, 64, 1)"
                                backgroundColor="rgba(255, 159, 64, 0.2)"
                            />
                        </div>
                    )}

                    {chartData.LOG && (
                        <div className="chart">
                            <LineChart
                                data={chartData.LOG}
                                chartTitle="로그 총 개수"
                                borderColor="rgba(75, 192, 192, 1)"
                                backgroundColor="rgba(75, 192, 192, 0.2)"
                            />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default App;