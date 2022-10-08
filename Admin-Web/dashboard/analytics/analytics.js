

if(!userID) {
    window.location.replace("../login/");
}

let completedData = 0;

document.querySelector(".analytics").classList.add("loader");



let noUsers = 0;
let arrayUsersDistrict = [];
let arrayUsersDays = [];

fDatabase.ref('Users').on('value', (list) => {

    noUsers = 0;
    arrayUsersDistrict = [];
    arrayUsersDays = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();

        noUsers++;

        if(!arrayUsersDistrict[data.district]) {
            arrayUsersDistrict[data.district] = 0;
        }
        arrayUsersDistrict[data.district]++;


        let day = new Date().getDay();
        if(!arrayUsersDays[day]) {
            arrayUsersDays[day] = 0;
        }
        arrayUsersDays[day]++;


        if(counts == i) {
            completedData++;


        }

        if(completedData >= 4) {

            onDataReady();

        }

    });

});




let noDrivers = 0;
let arrayDriversDistrict = [];
let arrayDriversDays = [];

fDatabase.ref('Drivers').on('value', (list) => {

    noDrivers = 0;
    arrayDriversDistrict = [];
    arrayDriversDays = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();

        noDrivers++;

        if(!arrayDriversDistrict[data.district]) {
            arrayDriversDistrict[data.district] = 0;
        }
        arrayDriversDistrict[data.district]++;


        let day = new Date().getDay();
        if(!arrayDriversDays[day]) {
            arrayDriversDays[day] = 0;
        }
        arrayDriversDays[day]++;


        if(counts == i) {
            completedData++;


        }

        if(completedData >= 4) {

            onDataReady();

        }

    });

});




let noTrashes = 0;
let arrayTrashesDistrict = [];
let arrayTrashesDays = [];

fDatabase.ref('Trashes').on('value', (list) => {

    noTrashes = 0;
    arrayTrashesDistrict = [];
    arrayTrashesDays = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();

        noTrashes++;


        if(!arrayTrashesDistrict[data.district]) {
            arrayTrashesDistrict[data.district] = 0;
        }
        arrayTrashesDistrict[data.district]++;

        
        let day = new Date().getDay();
        if(!arrayTrashesDays[day]) {
            arrayTrashesDays[day] = 0;
        }
        arrayTrashesDays[day]++;


        if(counts == i) {
            completedData++;

            chartBarTrashes();

            chartPieTrashes();
            
        }

        if(completedData >= 4) {

            onDataReady();

        }

    });

});





let noPayments = 0;
let countPayments = 0;
let arrayPaymentsDistrict = [];
let arrayPaymentsDays = [];

fDatabase.ref('Payments').on('value', (list) => {

    noTrashes = 0;
    countPayments = 0;
    arrayPaymentsDistrict = [];
    arrayPaymentsDays = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();

        noPayments++;

        countPayments += !isNaN(data.amount) ? parseInt(data.amount) : 0;

        
        if(!arrayPaymentsDistrict[data.district]) {
            arrayPaymentsDistrict[data.district] = 0;
        }
        arrayPaymentsDistrict[data.district]++;


        let day = new Date().getDay();
        if(!arrayPaymentsDays[day]) {
            arrayPaymentsDays[day] = 0;
        }
        arrayPaymentsDays[day]++;

        
        if(counts == i) {
            completedData++;


        }

        if(completedData >= 4) {

            onDataReady();

        }

    });

});


function onDataReady() {

    chartLinePayments();

    document.querySelector(".analytics").classList.remove("loader");

}


function chartBarTrashes() {

    let labels = [];
    let values = [];

    let i = 0;
    for(let key in arrayTrashesDistrict){
        labels[i] = key;
        values[i] = arrayTrashesDistrict[key];
        i++;
    }

    var ctx = document.getElementById("singelBarChart");
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Trashes",
                    data: values,
                    borderColor: "rgba(117, 113, 249, 0.9)",
                    borderWidth: "0",
                    backgroundColor: "rgba(117, 113, 249, 0.5)"
                }
            ]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });

}

function chartPieTrashes() {

    let labels = [];
    let values = [];

    let i = 0;
    for(let key in arrayTrashesDistrict){
        labels[i] = key;
        values[i] = arrayTrashesDistrict[key];
        i++;
    }

    var ctx = document.getElementById("pieChart");
    var myChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    "rgba(117, 113, 249,0.9)",
                    "rgba(117, 113, 249,0.7)",
                    "rgba(117, 113, 249,0.5)",
                    "rgba(144, 104,	190,0.7)"
                ],
                hoverBackgroundColor: [
                    "rgba(117, 113, 249,0.9)",
                    "rgba(117, 113, 249,0.7)",
                    "rgba(117, 113, 249,0.5)",
                    "rgba(144, 104,	190,0.7)"
                ],
            }],
        },
        options: {
            responsive: true,
            legend: {
                display: true,
            },
        }
    });

}


function chartLinePayments() {

    let labels1 = [];
    let values1 = [];
    let i = 0;
    for(let key in arrayPaymentsDistrict){
        labels1[i] = key;
        values1[i] = arrayPaymentsDistrict[key];
        i++;
    }

    let labels2 = [];
    let values2 = [];
    i = 0;
    for(let key in arrayTrashesDistrict){
        labels2[i] = key;
        values2[i] = arrayTrashesDistrict[key];
        i++;
    }

    let labels3 = [];
    let values3 = [];
    i = 0;
    for(let key in arrayUsersDistrict){
        labels3[i] = key;
        values3[i] = arrayUsersDistrict[key];
        i++;
    }
    
    var ctx = document.getElementById("sales-chart");
    // ctx.height = 150;
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ["May 2022", "Jun 2022", "Jul 2022", "Sept 2022", "Oct 2022", "Nov 2022", "Dec 2022"],
            type: 'line',
            defaultFontFamily: 'Montserrat',
            datasets: [{
                label: "Payments",
                data: values1,
                backgroundColor: 'transparent',
                borderColor: '#7571F9',
                borderWidth: 3,
                pointStyle: 'circle',
                pointRadius: 5,
                pointBorderColor: 'transparent',
                pointBackgroundColor: '#7571F9',

            }, {
                label: "Trashes",
                data: values2,
                backgroundColor: 'transparent',
                borderColor: '#4d7cff',
                borderWidth: 3,
                pointStyle: 'circle',
                pointRadius: 5,
                pointBorderColor: 'transparent',
                pointBackgroundColor: '#4d7cff',
            }, {
                label: "Users",
                data: values3,
                backgroundColor: 'transparent',
                borderColor: '#173e43',
                borderWidth: 3,
                pointStyle: 'circle',
                pointRadius: 5,
                pointBorderColor: 'transparent',
                pointBackgroundColor: '#173e43',
            }]
        },
        options: {
            responsive: true,

            tooltips: {
                mode: 'index',
                titleFontSize: 12,
                titleFontColor: '#000',
                bodyFontColor: '#000',
                backgroundColor: '#fff',
                titleFontFamily: 'Montserrat',
                bodyFontFamily: 'Montserrat',
                cornerRadius: 3,
                intersect: false,
            },
            legend: {
                labels: {
                    usePointStyle: true,
                    fontFamily: 'Montserrat',
                },
            },
            scales: {
                xAxes: [{
                    display: true,
                    gridLines: {
                        display: false,
                        drawBorder: false
                    },
                    scaleLabel: {
                        display: false,
                        labelString: 'Month'
                    }
                }],
                yAxes: [{
                    display: true,
                    gridLines: {
                        display: false,
                        drawBorder: false
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Value'
                    }
                }]
            },
            title: {
                display: false,
                text: 'Normal Legend'
            }
        }
    });

}