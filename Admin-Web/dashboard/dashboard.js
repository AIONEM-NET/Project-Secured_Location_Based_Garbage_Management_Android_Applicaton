
if(!userID) {
    window.location.replace("../login/");
}

let completedData = 0;

document.querySelector(".dashboard").classList.add("loader");



let noUsers = 0;
let arrayUsersDistrict = [];

fDatabase.ref('Users').on('value', (list) => {

    noUsers = 0;
    arrayUsersDistrict = []

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

        document.querySelector(".count-users").innerHTML = noUsers;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                e.querySelector(".count-1").innerHTML = arrayUsersDistrict[district] ?? 0;

            });

        }

        if(completedData >= 3) {
            document.querySelector(".dashboard").classList.remove("loader");
        }

    });

});



let noDrivers = 0;
let arrayDriversDistrict = [];

fDatabase.ref('Drivers').on('value', (list) => {

    noDrivers = 0;
    arrayDriversDistrict = [];

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

        document.querySelector(".count-drivers").innerHTML = noDrivers;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                e.querySelector(".count-2").innerHTML = arrayDriversDistrict[district] ?? 0;

            });

        }

        if(completedData >= 3) {
            document.querySelector(".dashboard").classList.remove("loader");
        }

    });

});





let noTrashes = 0;
let arrayTrashesDistrict = [];

fDatabase.ref('Trashes').on('value', (list) => {

    noTrashes = 0;
    arrayTrashesDistrict = [];

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

        document.querySelector(".count-trashes").innerHTML = noTrashes;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                e.querySelector(".count-3").innerHTML = arrayUsersDistrict[district] ?? 0;

            });

        }

        if(completedData >= 3) {
            document.querySelector(".dashboard").classList.remove("loader");
        }

    });

});



let noPayments = 0;
let countPayments = 0;
let arrayPaymentsDistrict = [];

fDatabase.ref('Payments').on('value', (list) => {

    noTrashes = 0;
    countPayments = 0;
    arrayPaymentsDistrict = [];

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
        
        document.querySelector(".count-payments").innerHTML = countPayments.toLocaleString();

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                

            });

        }

        if(completedData >= 3) {
            document.querySelector(".dashboard").classList.remove("loader");
        }

    });

});


window.addEventListener('popstate', (event) => {
    history.go(1);
});
history.pushState({ state: 1 }, '');
