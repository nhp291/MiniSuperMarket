'use strict';

$(document).ready(function() {
    // Hàm tạo dữ liệu ngẫu nhiên cho biểu đồ
    function generateData(baseval, count, yrange) {
        var i = 0;
        var series = [];
        // Tạo dữ liệu ngẫu nhiên cho mỗi điểm dữ liệu
        while (i < count) {
            var x = Math.floor(Math.random() * (750 - 1 + 1)) + 1;
            var y = Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min;
            var z = Math.floor(Math.random() * (75 - 15 + 1)) + 15;
            series.push([x, y, z]);
            baseval += 86400000; // Cập nhật giá trị cơ sở (tính theo ngày)
            i++;
        }
        return series;
    }

    // Biểu đồ cột thể hiện số liệu "Received" và "Pending"
    if ($('#sales_chart').length > 0) {
        var columnCtx = document.getElementById("sales_chart");
        var columnConfig = {
            colors: ['#7638ff', '#fda600'],
            series: [{
                name: "Received", // Dữ liệu cho "Received"
                type: "column",
                data: [70, 150, 80, 180, 150, 175, 201, 60, 200, 120, 190, 160, 50]
            }, {
                name: "Pending", // Dữ liệu cho "Pending"
                type: "column",
                data: [23, 42, 35, 27, 43, 22, 17, 31, 22, 22, 12, 16, 80]
            }],
            chart: {
                type: 'bar',
                fontFamily: 'Poppins, sans-serif',
                height: 350,
                toolbar: { show: false }
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '60%',
                    endingShape: 'rounded'
                },
            },
            dataLabels: { enabled: false },
            stroke: { show: true, width: 2, colors: ['transparent'] },
            xaxis: { categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct'] },
            yaxis: { title: { text: '$ (thousands)' } },
            fill: { opacity: 1 },
            tooltip: { y: { formatter: function(val) { return "$ " + val + " thousands" } } }
        };
        var columnChart = new ApexCharts(columnCtx, columnConfig);
        columnChart.render();
    }

    // Biểu đồ hình tròn (Donut chart) thể hiện các trạng thái hóa đơn
    if ($('#invoice_chart').length > 0) {
        var pieCtx = document.getElementById("invoice_chart");
        var pieConfig = {
            colors: ['#7638ff', '#ff737b', '#fda600', '#1ec1b0'],
            series: [55, 40, 20, 10],
            chart: {
                fontFamily: 'Poppins, sans-serif',
                height: 350,
                type: 'donut',
            },
            labels: ['Paid', 'Unpaid', 'Overdue', 'Draft'],
            legend: { show: false },
            responsive: [{
                breakpoint: 480,
                options: {
                    chart: { width: 200 },
                    legend: { position: 'bottom' }
                }
            }]
        };
        var pieChart = new ApexCharts(pieCtx, pieConfig);
        pieChart.render();
    }

    // Biểu đồ đường thể hiện xu hướng sản phẩm theo tháng
    if ($('#s-line').length > 0) {
        var sline = {
            chart: {
                height: 350,
                type: 'line',
                zoom: { enabled: false },
                toolbar: { show: false },
            },
            dataLabels: { enabled: false },
            stroke: { curve: 'straight' },
            series: [{
                name: "Desktops",
                data: [10, 41, 35, 51, 49, 62, 69, 91, 148]
            }],
            title: { text: 'Product Trends by Month', align: 'left' },
            grid: { row: { colors: ['#f1f2f3', 'transparent'], opacity: 0.5 } },
            xaxis: { categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'] }
        }
        var chart = new ApexCharts(document.querySelector("#s-line"), sline);
        chart.render();
    }

    // Biểu đồ vùng thể hiện sự thay đổi của các chuỗi dữ liệu theo thời gian
    if ($('#s-line-area').length > 0) {
        var sLineArea = {
            chart: {
                height: 350,
                type: 'area',
                toolbar: { show: false },
            },
            dataLabels: { enabled: false },
            stroke: { curve: 'smooth' },
            series: [{
                name: 'series1',
                data: [31, 40, 28, 51, 42, 109, 100]
            }, {
                name: 'series2',
                data: [11, 32, 45, 32, 34, 52, 41]
            }],
            xaxis: {
                type: 'datetime',
                categories: [
                    "2018-09-19T00:00:00", "2018-09-19T01:30:00", "2018-09-19T02:30:00",
                    "2018-09-19T03:30:00", "2018-09-19T04:30:00", "2018-09-19T05:30:00", "2018-09-19T06:30:00"
                ],
            },
            tooltip: { x: { format: 'dd/MM/yy HH:mm' } }
        };
        var chart = new ApexCharts(document.querySelector("#s-line-area"), sLineArea);
        chart.render();
    }

    // Biểu đồ cột thể hiện lợi nhuận và doanh thu theo từng tháng
    if ($('#s-col').length > 0) {
        var sCol = {
            chart: {
                height: 350,
                type: 'bar',
                toolbar: { show: false },
            },
            plotOptions: {
                bar: { horizontal: false, columnWidth: '55%', endingShape: 'rounded' },
            },
            dataLabels: { enabled: false },
            stroke: { show: true, width: 2, colors: ['transparent'] },
            series: [{
                name: 'Net Profit',
                data: [44, 55, 57, 56, 61, 58, 63, 60, 66]
            }, {
                name: 'Revenue',
                data: [76, 85, 101, 98, 87, 105, 91, 114, 94]
            }],
            xaxis: { categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct'] },
            yaxis: { title: { text: '$ (thousands)' } },
            fill: { opacity: 1 },
            tooltip: { y: { formatter: function(val) { return "$ " + val + " thousands" } } }
        };
        var chart = new ApexCharts(document.querySelector("#s-col"), sCol);
        chart.render();
    }

    // Biểu đồ cột chồng thể hiện các sản phẩm trong các tháng
    if ($('#s-col-stacked').length > 0) {
        var sColStacked = {
            chart: {
                height: 350,
                type: 'bar',
                stacked: true,
                toolbar: { show: false },
            },
            responsive: [{
                breakpoint: 480,
                options: { legend: { position: 'bottom', offsetX: -10, offsetY: 0 } }
            }],
            plotOptions: { bar: { horizontal: false } },
            series: [{
                name: 'PRODUCT A', data: [44, 55, 41, 67, 22, 43]
            }, {
                name: 'PRODUCT B', data: [13, 23, 20, 8, 13, 27]
            }, {
                name: 'PRODUCT C', data: [11, 17, 15, 15, 21, 14]
            }, {
                name: 'PRODUCT D', data: [21, 7, 25, 13, 22, 8]
            }],
            xaxis: {
                type: 'datetime',
                categories: ['01/01/2011 GMT', '01/02/2011 GMT', '01/03/2011 GMT', '01/04/2011 GMT', '01/05/2011 GMT', '01/06/2011 GMT'],
            },
            legend: { position: 'right', offsetY: 40 },
            fill: { opacity: 1 },
        };
        var chart = new ApexCharts(document.querySelector("#s-col-stacked"), sColStacked);
        chart.render();
    }

    // Biểu đồ cột ngang thể hiện số liệu các quốc gia
    if ($('#s-bar').length > 0) {
        var sBar = {
            chart: { height: 350, type: 'bar', toolbar: { show: false } },
            plotOptions: { bar: { horizontal: true } },
            dataLabels: { enabled: false },
            series: [{ data: [400, 430, 448, 470, 540, 580, 690, 1100, 1200, 1380] }],
            xaxis: {
                categories: ['South Korea', 'Canada', 'United Kingdom', 'Netherlands', 'Italy', 'France', 'Japan', 'United States', 'China', 'Germany'],
            }
        };
        var chart = new ApexCharts(document.querySelector("#s-bar"), sBar);
        chart.render();
    }

    // Biểu đồ kết hợp thể hiện các nguồn lưu lượng truy cập
    if ($('#mixed-chart').length > 0) {
        var options = {
            chart: { height: 350, type: 'line', toolbar: { show: false } },
            series: [{
                name: 'Website Blog',
                type: 'column',
                data: [440, 505, 414, 671, 227, 413, 201, 352, 752, 320, 257, 160]
            }, {
                name: 'Social Media',
                type: 'line',
                data: [23, 42, 35, 27, 43, 22, 17, 31, 22, 22, 12, 16]
            }],
            stroke: { width: [0, 4] },
            title: { text: 'Traffic Sources' },
            labels: ['01 Jan 2001', '02 Jan 2001', '03 Jan 2001', '04 Jan 2001', '05 Jan 2001', '06 Jan 2001', '07 Jan 2001', '08 Jan 2001', '09 Jan 2001', '10 Jan 2001', '11 Jan 2001', '12 Jan 2001'],
            xaxis: { type: 'datetime' },
            yaxis: [{
                title: { text: 'Website Blog' },
            }, {
                opposite: true,
                title: { text: 'Social Media' }
            }]
        };
        var chart = new ApexCharts(document.querySelector("#mixed-chart"), options);
        chart.render();
    }

    // Biểu đồ donut đơn giản
    if ($('#donut-chart').length > 0) {
        var donutChart = {
            chart: { height: 350, type: 'donut', toolbar: { show: false } },
            series: [44, 55, 41],
            responsive: [{
                breakpoint: 480,
                options: {
                    chart: { width: 200 },
                    legend: { position: 'bottom' }
                }
            }]
        };
        var donut = new ApexCharts(document.querySelector("#donut-chart"), donutChart);
        donut.render();
    }

    // Biểu đồ hình tròn (radialBar) thể hiện tỷ lệ phân chia trái cây
    if ($('#radial-chart').length > 0) {
        var radialChart = {
            chart: { height: 350, type: 'radialBar', toolbar: { show: false } },
            plotOptions: {
                radialBar: {
                    dataLabels: {
                        name: { fontSize: '22px' },
                        value: { fontSize: '16px' },
                        total: {
                            show: true,
                            label: 'Total',
                            formatter: function(w) { return 249; }
                        }
                    }
                }
            },
            series: [44, 55, 67, 83],
            labels: ['Apples', 'Oranges', 'Bananas', 'Berries'],
        };
        var chart = new ApexCharts(document.querySelector("#radial-chart"), radialChart);
        chart.render();
    }

    // Biểu đồ cột chồng thể hiện doanh thu và mua hàng
    // Biểu đồ cột chồng thể hiện doanh thu và mua hàng
    if ($('#order-stats-chart').length > 0) {
        $('#order-stats-chart').html('<p>Đang tải dữ liệu...</p>');
// Định dạng số liệu tiền tệ Việt Nam
        function formatNumberVN(value) {
            return new Intl.NumberFormat('vi-VN').format(value);
        }

// Cập nhật phần xử lý dữ liệu
        $.get('/Orders/order-stats', function (data) {
            if (data && data.daily && data.monthly && data.yearly) {
                var dailyData = data.daily.map(stat => ({
                    x: `Ngày ${new Date(stat[0]).toLocaleDateString('vi-VN')}`,
                    y: stat[2] * 1000 // Chuyển đổi từ nghìn sang triệu
                }));
                var monthlyData = data.monthly.map(stat => ({
                    x: `Tháng ${stat[1]} / ${stat[0]}`,
                    y: stat[3] * 1000 // Chuyển đổi từ nghìn sang triệu
                }));
                var yearlyData = data.yearly.map(stat => ({
                    x: `Năm ${stat[0]}`,
                    y: stat[2] * 1000 // Chuyển đổi từ nghìn sang triệu
                }));

                // Cấu hình biểu đồ
                var options = {
                    chart: {
                        type: 'bar',
                        height: 350,
                        stacked: true,
                        toolbar: { show: false }
                    },
                    series: [
                        { name: 'Hàng ngày', data: dailyData },
                        { name: 'Hàng tháng', data: monthlyData },
                        { name: 'Hàng năm', data: yearlyData }
                    ],
                    xaxis: {
                        categories: dailyData.map(d => d.x), // Tên cột X
                        labels: { style: { fontSize: '12px' } }
                    },
                    yaxis: {
                        title: { text: 'Doanh thu (VNĐ)' },
                        labels: {
                            formatter: function (value) {
                                return formatNumberVN(value); // Hiển thị định dạng VNĐ
                            }
                        }
                    },
                    tooltip: {
                        y: {
                            formatter: function (value) {
                                return formatNumberVN(value) + ' VNĐ'; // Định dạng tiền trong tooltip
                            }
                        }
                    },
                    legend: { position: 'bottom' },
                    colors: ['#FFC107', '#28A745', '#007BFF'],
                    responsive: [{
                        breakpoint: 480,
                        options: {
                            legend: { position: 'bottom' }
                        }
                    }]
                };

                var chart = new ApexCharts(document.querySelector("#order-stats-chart"), options);
                chart.render();
            } else {
                $('#order-stats-chart').html('<p>Không có dữ liệu để hiển thị.</p>');
            }
        }).fail(function () {
            $('#order-stats-chart').html('<p>Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại sau.</p>');
        });

    }



    // Biểu đồ trạng thái thanh toán (payment-status-chart)
    if ($('#payment-status-chart').length > 0) {
        // Thêm thông báo đang tải dữ liệu
        $('#payment-status-chart').html('<p>Đang tải dữ liệu...</p>');

        // Gọi API để lấy thống kê trạng thái thanh toán
        $.get('/Orders/payment-status-stats', function (data) {
            console.log("Dữ liệu nhận được từ API:", data);  // Log dữ liệu nhận được từ API

            // Kiểm tra xem dữ liệu trả về có hợp lệ không
            if (data && data.pending !== undefined && data.completed !== undefined && data.cancelled !== undefined) {
                // Log các giá trị trạng thái thanh toán
                console.log("Chờ xử lý:", data.pending);
                console.log("Hoàn thành:", data.completed);
                console.log("Hủy bỏ:", data.cancelled);

                // Cập nhật thông báo khi dữ liệu đã được lấy xong
                $('#payment-status-chart').html('');  // Xóa thông báo đang tải
                $('#payment-status-chart').append('<p>Thống kê trạng thái thanh toán đã sẵn sàng!</p>');

                var paymentStatusChart = {
                    chart: {
                        height: 350,
                        type: 'donut',
                        toolbar: { show: false }
                    },
                    series: [data.pending, data.completed, data.cancelled],
                    labels: ['Chờ xử lý', 'Hoàn thành', 'Hủy bỏ'],
                    colors: ['#FFC107', '#28A745', '#DC3545'],
                    legend: {
                        position: 'bottom',
                        markers: {
                            width: 10,
                            height: 10,
                            radius: 2,
                        },
                        formatter: function (seriesName, opts) {
                            var value = opts.w.globals.series[opts.seriesIndex];
                            return seriesName + ": " + value + " đơn hàng";
                        }
                    },
                    responsive: [{
                        breakpoint: 480,
                        options: {
                            chart: { width: 200 },
                            legend: { position: 'bottom' }
                        }
                    }]
                };

                var chart = new ApexCharts(document.querySelector("#payment-status-chart"), paymentStatusChart);
                chart.render();
            } else {
                console.error("Dữ liệu không hợp lệ:", data);
                $('#payment-status-chart').html('<p>Không thể lấy dữ liệu thống kê. Vui lòng thử lại sau.</p>');
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
            console.error("Lỗi khi gọi API:", textStatus, errorThrown);
            $('#payment-status-chart').html('<p>Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại sau.</p>');
        });
    }


});
