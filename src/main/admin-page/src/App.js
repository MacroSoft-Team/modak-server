import './App.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import LineChart from './LineChart';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css'

function App() {
    const [statisticsData, setStatisticsData] = useState(null);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    useEffect(() => {
        axios.get('/api/admin/statistics')
            .then((response) => {
                const data = response.data;
                setStatisticsData(data);

                const firstDate = new Date(
                    Math.min(
                        ...Object.values(data).flatMap((dataset) =>
                            Object.keys(dataset).map((date) => new Date(date))
                        )
                    )
                );

                const lastDate = new Date(
                    Math.max(
                        ...Object.values(data).flatMap((dataset) =>
                            Object.keys(dataset).map((date) => new Date(date))
                        )
                    )
                );

                setStartDate(firstDate);
                setEndDate(lastDate);
            })
            .catch(error => console.log(error))
    }, []);

    const filterDataByDateRange = (data) => {
        if (!data || !startDate || !endDate) return data;
        return Object.keys(data)
            .filter((key) => {
                const date = new Date(key);
                return date >= startDate && date <= endDate;
            })
            .reduce((filtered, key) => {
                filtered[key] = data[key];
                return filtered;
            }, {});
    };

    const chartData = {
        IMAGE: filterDataByDateRange(statisticsData?.IMAGE || {}),
        EMOTION: filterDataByDateRange(statisticsData?.EMOTION || {}),
        CAMPFIRE: filterDataByDateRange(statisticsData?.CAMPFIRE || {}),
        ACTIVE_CAMPFIRE: filterDataByDateRange(statisticsData?.ACTIVE_CAMPFIRE || {}),
        MEMBER: filterDataByDateRange(statisticsData?.MEMBER || {}),
        LOG: filterDataByDateRange(statisticsData?.LOG || {}),
    };

    return (
        <div className="App">
            <div className="black-nav">
                <h4>관리자 페이지 입니다.</h4>
            </div>

            <div className="date-picker-container">
                <div>
                    <label>조회 시작 날짜:</label>
                    <DatePicker
                        selected={startDate}
                        onChange={(date) => setStartDate(date)}
                        selectsStart
                        startDate={startDate}
                        endDate={endDate}
                        dateFormat="yyyy-MM-dd"
                    />
                </div>
                <div>
                    <label>조회 종료 날짜:</label>
                    <DatePicker
                        selected={endDate}
                        onChange={(date) => setEndDate(date)}
                        selectsEnd
                        startDate={startDate}
                        endDate={endDate}
                        dateFormat="yyyy-MM-dd"
                    />
                </div>
            </div>

            <div className="charts-container">
                <div className="chart-row">
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
                </div>
                <div className="chart-row">
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
                </div>
            </div>
        </div>
    );
}

export default App;