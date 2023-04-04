var xValues = ["2019", "2020", "2021", "2022", "2023"];
var yValues = [55, 49, 44, 34, 28];
var barColors = ["#b91d47", "#00aba9","#2b5797","#e8c3b9","#1e7145"];

new Chart("myChart", {
  type: "bar",
  data: {
    labels: xValues,
    datasets: [{
      backgroundColor: barColors,
      data: yValues
    }]
  },
  options: {
    legend: {display: false},
    title: {
      display: true,
    }
  }
});




var x1Values = ["2019", "2020", "2021", "2022", "2023"];
var y1Values = [55, 49, 44, 34, 28];
var bar1Colors = [
  "#b91d47",
  "#00aba9",
  "#2b5797",
  "#e8c3b9",
  "#1e7145"
];

new Chart("myChart2", {
  type: "pie",
  data: {
    labels: x1Values,
    datasets: [{
      backgroundColor: bar1Colors,
      data: y1Values
    }]
  },
  options: {
    title: {
      display: true,
    }
  }
});