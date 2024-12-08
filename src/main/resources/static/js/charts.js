const yearSelector = document.getElementById('yearSelector');
const ctx = document.getElementById('revenueChart').getContext('2d');

const currentYears = new Date().getFullYear();
yearSelector.value = currentYears; 

const chart = new Chart(ctx, {
	type: 'bar',
	data: {
		labels: Array.from({ length: 12 }, (_, i) => `Tháng ${i + 1}`),
		datasets: [{
			label: 'Doanh thu theo tháng ',
			data: Array(12).fill(0),
			backgroundColor: 'rgba(75, 192, 192, 0.2)',
			borderColor: 'rgba(75, 192, 192, 1)',
			borderWidth: 1
		}]
	},
	options: {
		responsive: true,
		plugins: {
			tooltip: {
				callbacks: {
					label: function(context) {
						const value = context.raw;
						return `${value.toLocaleString()} VNĐ`; // Thêm "VNĐ" vào tooltip
					}
				}
			},
			title: {
				display: true,
				text: 'Biểu đồ doanh thu theo từng tháng trong năm', // Tiêu đề biểu đồ
				font: {
					size: 18,
					weight: 'bold'
				},
				color: '#333' // Màu tiêu đề
			}
		},
		scales: {
			y: {
				title: {
					display: true,
					text: 'Doanh thu (VNĐ)'
				},
				beginAtZero: true
			},
			x: {
				title: {
					display: true,
					text: 'Tháng'
				}
			}
		}
	}
});

function updateChart(year) {
	fetch(`/admin/monthly?year=${year}`)
		.then(response => response.json())
		.then(data => {
			const revenue = Array.from({ length: 12 }, (_, i) => data[i + 1] || 0);
			chart.data.datasets[0].data = revenue;
			chart.update();
		})
		.catch(error => console.error('Error fetching revenue data:', error));
}



yearSelector.addEventListener('input', () => {
    const selectedYear = yearSelector.value;
    updateChart(selectedYear);
});

updateChart(yearSelector.value);





$(document).ready(function() {
	// Lấy tháng và năm hiện tại
	const currentDate = new Date();
	const currentMonth = currentDate.getMonth() + 1; // Tháng hiện tại (0-11, phải +1)
	const currentYear = currentDate.getFullYear(); // Năm hiện tại

	$('#monthSelector').datepicker({
		format: 'mm/yyyy',
		startView: 'months',
		minViewMode: 'months',
		autoclose: true
	}).datepicker('setDate', new Date(currentYear, currentMonth - 1, 1)); // Mặc định là tháng hiện tại

	$('#monthSelector').on('change', function() {
		const selectedDate = $(this).val();
		const [month, year] = selectedDate.split('/'); // Tách tháng và năm

		updateDayChart(year, month); // Cập nhật biểu đồ doanh thu theo ngày
	});

	// Cập nhật dữ liệu cho tháng hiện tại khi trang load
	updateDayChart(currentYear, currentMonth);
});

const ctx2 = document.getElementById('dailyRevenueChart').getContext('2d');
const chart2 = new Chart(ctx2, {
	type: 'line', // Biểu đồ đường cho doanh thu theo ngày
	data: {
		labels: [], // Dữ liệu sẽ được cập nhật khi người dùng chọn tháng
		datasets: [{
			label: 'Doanh thu theo ngày',
			data: [], // Dữ liệu doanh thu theo ngày
			borderColor: 'rgba(75, 192, 192, 1)',
			borderWidth: 1,
			fill: false
		}]
	},
	options: {
		responsive: true,
		plugins: {
			tooltip: {
				callbacks: {
					label: function(context) {
						const value = context.raw;
						return `${value.toLocaleString()} VNĐ`; // Thêm "VNĐ" vào tooltip
					}
				}
			},
			title: {
				display: true,
				text: 'Biểu đồ doah thu theo từng ngày trong tháng', // Tiêu đề biểu đồ
				font: {
					size: 18,
					weight: 'bold'
				},
				color: '#333' // Màu tiêu đề
			}
		},
		scales: {
			x: {
				title: {
					display: true,
					text: 'Ngày'
				}
			},
			y: {
				title: {
					display: true,
					text: 'Doanh thu (VNĐ)'
				}
			}
		}
	}
});


// Hàm cập nhật dữ liệu biểu đồ doanh thu theo ngày
function updateDayChart(year, month) {
	fetch(`/admin/daily-revenue?year=${year}&month=${month}`)
		.then(response => response.json())
		.then(data => {
			const days = Object.keys(data);
			const revenue = days.map(day => data[day] || 0);
			chart2.data.labels = days; // Gán nhãn là các ngày trong tháng
			chart2.data.datasets[0].data = revenue; // Gán dữ liệu doanh thu
			chart2.update();
		})
		.catch(error => console.error('Error fetching daily revenue data:', error));
}

// Xử lý thay đổi loại thống kê
function handleFilterChange() {
	const filterType = document.getElementById('filterType').value;
	const inputLabel = document.getElementById('inputLabel');
	const startDate = document.getElementById('startDate');
	const endDate = document.getElementById('endDate');
	const separator = document.querySelector('.separator');

	const today = new Date();
	const currentDate = today.toISOString().split('T')[0]; // YYYY-MM-DD
	const currentMonth = today.toISOString().slice(0, 7);  // YYYY-MM

	if (filterType === 'day') {
		inputLabel.textContent = 'Chọn ngày';
		startDate.type = 'date';
		endDate.type = 'date';
		startDate.style.display = 'block';
		endDate.style.display = 'block';
		separator.style.display = 'inline';


		startDate.value = currentDate;
		endDate.value = currentDate;
	} else if (filterType === 'month') {
		inputLabel.textContent = 'Chọn tháng';
		startDate.type = 'month';
		startDate.style.display = 'block';
		endDate.style.display = 'none';
		separator.style.display = 'none';

		// Gán giá trị mặc định là tháng hiện tại
		startDate.value = currentMonth;
	} else if (filterType === 'year') {
		inputLabel.textContent = 'Chọn năm';
		startDate.type = 'number';
		startDate.min = 2000;
		startDate.max = 2100;
		startDate.step = 1;
		startDate.value = today.getFullYear();
		startDate.style.display = 'block';
		endDate.style.display = 'none';
		separator.style.display = 'none';
	}

	updateChartst(); // Tự động cập nhật biểu đồ khi thay đổi bộ lọc
}



async function updateChartst() {
	const filterType = document.getElementById('filterType').value;
	const startDateInput = document.getElementById('startDate');
	const endDateInput = document.getElementById('endDate');

	let startDate, endDate;

	if (filterType === 'day') {
		startDate = startDateInput.value + "T00:00:00";
		endDate = endDateInput.value + "T23:59:59";
	} else if (filterType === 'month') {
		startDate = startDateInput.value + "-01T00:00:00";
		endDate = new Date(new Date(startDate).getFullYear(), new Date(startDate).getMonth() + 1, 0).toISOString();
	} else if (filterType === 'year') {
		startDate = startDateInput.value + "-01-01T00:00:00";
		endDate = startDateInput.value + "-12-31T23:59:59";
	}


	const data = await fetchStatistics(startDate, endDate);
	renderChartst(data); // Vẽ lại biểu đồ
}


async function fetchStatistics(startDate, endDate) {
	const apiUrl = "/admin/statistics";
	const response = await fetch(`${apiUrl}?startDate=${startDate}&endDate=${endDate}`);
	return await response.json();
}

// Vẽ biểu đồ
function renderChartst(data) {
	const ctx = document.getElementById('orderStatusChart').getContext('2d');
	const labels = Object.keys(data);
	const values = Object.values(data);


	if (window.myChart) {
		window.myChart.destroy();
	}

	window.myChart = new Chart(ctx, {
		type: 'pie',
		data: {
			labels: labels,
			datasets: [{
				label: 'Số đơn hàng',
				data: values,
				backgroundColor: [
					'#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
				],
				hoverOffset: 4
			}]
		},

		options: {
			responsive: true,
			plugins: {
				title: {
					display: true,  // Hiển thị tiêu đề
					text: 'Thống kê trạng thái đơn hàng',  // Tiêu đề bạn muốn hiển thị
					font: {
						size: 18,  // Kích thước font của tiêu đề
						weight: 'bold'  // Độ đậm của font
					},
					padding: {
						top: 10,  // Khoảng cách từ tiêu đề đến phần biểu đồ
						bottom: 10
					}
				}
			}
		}
	});
}

document.getElementById('filterType').addEventListener('change', handleFilterChange);
document.getElementById('startDate').addEventListener('change', updateChartst);
document.getElementById('endDate').addEventListener('change', updateChartst);

document.addEventListener('DOMContentLoaded', handleFilterChange);
