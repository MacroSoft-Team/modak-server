import React from 'react';
import { Line } from 'react-chartjs-2';
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

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const LineChart = ({
    data,
    chartTitle = 'Chart Title',
    borderColor = 'rgba(75, 192, 192, 1)',
    backgroundColor = 'rgba(75, 192, 192, 0.2)'
}) => {
    const sortedDates = Object.keys(data).sort((a, b) => new Date(a) - new Date(b));
    const sortedValues = sortedDates.map(date => data[date]);
    const chartData = {
        labels: sortedDates,
        datasets: [
            {
                label: chartTitle,
                data: sortedValues,
                borderColor,
                backgroundColor,
                tension: 0.3,
            },
        ],
    };

    const options = {
        responsive: true,
        plugins: {
            legend: { position: 'top' },
            title: { display: true, text: '일일 ' + chartTitle },
        },
        scales: {
            x: { title: { display: true, text: 'Date' } },
            y: { title: { display: true, text: 'Counts' } },
        },
    };

    return <Line data={chartData} options={options} />;
};

export default LineChart;