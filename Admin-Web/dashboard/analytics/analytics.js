

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

        if(completedData >= 5) {

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

        for(let district of data.district.split(",")) {
            if(!arrayDriversDistrict[district]) {
                arrayDriversDistrict[district] = 0;
            }
            arrayDriversDistrict[district]++;
        }

        let day = new Date().getDay();
        if(!arrayDriversDays[day]) {
            arrayDriversDays[day] = 0;
        }
        arrayDriversDays[day]++;


        if(counts == i) {
            completedData++;

        }

        if(completedData >= 5) {

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

        if(userDistrict && !(""+data.district).includes(userDistrict)) {

            if(counts == i) {
                completedData++;
            }
            if(completedData >= 5) {
                onDataReady();
            }

            return;
        }

        noTrashes++;
        

        for(let district of data.district.split(",")) {
            if(!arrayTrashesDistrict[district]) {
                arrayTrashesDistrict[district] = 0;
            }
            arrayTrashesDistrict[district]++;
        }

        
        let day = new Date().getDay();
        if(!arrayTrashesDays[day]) {
            arrayTrashesDays[day] = 0;
        }
        arrayTrashesDays[day]++;


        if(counts == i) {
            completedData++;
        }
        if(completedData >= 5) {
            onDataReady();
        }

    });

});





let noPackages = 0;
let countPackages = 0;
let arrayPackagesDistrict = [];
let arrayPackagesCountDistrict = [];

fDatabase.ref('Garbage').on('value', (list) => {

    noPackages = 0;
    countPackages = 0;
    arrayPackagesDistrict = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();

        if(userDistrict && !(""+data.district).includes(userDistrict)) {

            if(counts == i) {
                completedData++;
            }
            if(completedData >= 5) {
                onDataReady();
            }
            
            return;
        }

        noPackages++;

        let packages = !isNaN(data.packages) ? parseInt(data.packages) : 0;

        countPackages += packages;

        for(let district of data.district.split(",")) {
            if(!arrayPackagesDistrict[district]) {
                arrayPackagesDistrict[district] = 0;
            }
            if(!arrayPackagesCountDistrict[district]) {
                arrayPackagesCountDistrict[district] = 0;
            }
            arrayPackagesDistrict[district]++;
            arrayPackagesCountDistrict[district] += packages;
        }


        if(counts == i) {
            completedData++;

        }

        if(completedData >= 5) {

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

        if(userDistrict && !(""+data.district).includes(userDistrict)) {

            if(counts == i) {
                completedData++;
            }
            if(completedData >= 5) {
                onDataReady();
            }
            
            return;
        }

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
        if(completedData >= 5) {
            onDataReady();
        }

    });

});


function onDataReady() {

    chartBarTrashes();

    chartPieTrashes();

    chartLinePayments();

    document.querySelector(".analytics").classList.remove("loader");

}


function chartBarTrashes() {

    let labels = [];
    let values = [];

    let i = 0;
    for(let key in arrayPackagesCountDistrict){
        labels[i] = key.substring(0, 4) +".";
        values[i] = arrayPackagesCountDistrict[key];
        i++;
    }

    var ctx = document.getElementById("singelBarChart");
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Packages",
                    data: values,
                    borderColor: "#00bfa0",
                    borderWidth: "0",
                    backgroundColor: "#00bfa050"
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
                    "#ea5545", "#f46a9b", "#ef9b20", "#edbf33", "#ede15b", "#bdcf32", "#87bc45", "#27aeef", "#b33dc6",
                    "#e60049", "#0bb4ff", "#50e991", "#e6d800", "#9b19f5", "#ffa300", "#dc0ab4", "#b3d4ff", "#00bfa0",
                    "#b30000", "#7c1158", "#4421af", "#1a53ff", "#0d88e6", "#00b7c7", "#5ad45a", "#8be04e", "#ebdc78",
                    "#fd7f6f", "#7eb0d5", "#b2e061", "#bd7ebe", "#ffb55a", "#ffee65", "#beb9db", "#fdcce5", "#8bd3c7"
                ],
                hoverBackgroundColor: [
                    "#ea5545", "#f46a9b", "#ef9b20", "#edbf33", "#ede15b", "#bdcf32", "#87bc45", "#27aeef", "#b33dc6",
                    "#e60049", "#0bb4ff", "#50e991", "#e6d800", "#9b19f5", "#ffa300", "#dc0ab4", "#b3d4ff", "#00bfa0",
                    "#b30000", "#7c1158", "#4421af", "#1a53ff", "#0d88e6", "#00b7c7", "#5ad45a", "#8be04e", "#ebdc78",
                    "#fd7f6f", "#7eb0d5", "#b2e061", "#bd7ebe", "#ffb55a", "#ffee65", "#beb9db", "#fdcce5", "#8bd3c7"
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

    let labels = [];

    let labels1 = [];
    let values1 = [];
    let i = 0;
    for(let key in arrayPaymentsDistrict){
        labels[i] = key.substring(0, 4) +".";
        labels1[i] = key.substring(0, 4) +".";
        values1[i] = arrayPaymentsDistrict[key];
        i++;
    }

    let labels2 = [];
    let values2 = [];
    i = 0;
    for(let key in arrayTrashesDistrict){
        labels[i] = key.substring(0, 4) +".";
        labels2[i] = key.substring(0, 4) +".";
        values2[i] = arrayTrashesDistrict[key];
        i++;
    }

    let labels3 = [];
    let values3 = [];
    i = 0;
    for(let key in arrayUsersDistrict){
        labels[i] = key.substring(0, 4) +".";
        labels3[i] = key.substring(0, 4) +".";
        values3[i] = arrayUsersDistrict[key];
        i++;
    }

    let labels4 = [];
    let values4 = [];
    i = 0;
    for(let key in arrayDriversDistrict){
        labels[i] = key.substring(0, 4) +".";
        labels4[i] = key.substring(0, 4) +".";
        values4[i] = arrayDriversDistrict[key];
        i++;
    }

    let labels5 = [];
    let values5 = [];
    i = 0;
    for(let key in arrayPackagesDistrict){
        labels[i] = key.substring(0, 4) +".";
        labels5[i] = key.substring(0, 4) +".";
        values5[i] = arrayPackagesDistrict[key];
        i++;
    }
    
    var ctx = document.getElementById("sales-chart");
    // ctx.height = 150;
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            type: 'line',
            defaultFontFamily: 'Montserrat',
            datasets: [
                {
                    label: "Users",
                    data: values3,
                    backgroundColor: 'transparent',
                    borderColor: '#ffa300',
                    borderWidth: 3,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointBorderColor: 'transparent',
                    pointBackgroundColor: '#ffa300',
                }, 
                {
                    label: "Drivers",
                    data: values4,
                    backgroundColor: 'transparent',
                    borderColor: '#00bfa0',
                    borderWidth: 3,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointBorderColor: 'transparent',
                    pointBackgroundColor: '#00bfa0',
                }, 
                {
                    label: "Trashes",
                    data: values2,
                    backgroundColor: 'transparent',
                    borderColor: '#1a53ff',
                    borderWidth: 3,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointBorderColor: 'transparent',
                    pointBackgroundColor: '#1a53ff',
                },
                {
                    label: "Payments",
                    data: values1,
                    backgroundColor: 'transparent',
                    borderColor: '#e60049',
                    borderWidth: 3,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointBorderColor: 'transparent',
                    pointBackgroundColor: '#e60049',
                },
                {
                    label: "Requests",
                    data: values5,
                    backgroundColor: 'transparent',
                    borderColor: '#9b19f5',
                    borderWidth: 3,
                    pointStyle: 'circle',
                    pointRadius: 5,
                    pointBorderColor: 'transparent',
                    pointBackgroundColor: '#9b19f5',
                }
            ]
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
                    },
                    ticks: {
                        beginAtZero: true
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