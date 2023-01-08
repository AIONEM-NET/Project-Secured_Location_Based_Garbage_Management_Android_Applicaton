
if(!userID) {
    window.location.replace("../login/");
}

document.querySelector(".facc-dashboard-title").innerHTML = (userAccount != "Admin" ? "System" : userDistrict) +" Analytics";

let completedData = 0;

document.querySelector(".dashboard-loader").classList.add("loader");



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
        
        if(userDistrict && !(""+data.district).includes(userDistrict)) {
    
            if(counts == i) {
                completedData++;

                document.querySelectorAll(".count-districts").forEach(function(e, key) {

                    let district = e.getAttribute("data-district");

                    e.querySelector(".count-1").innerHTML = arrayUsersDistrict[district] ?? 0;

                });

            }

            if(completedData >= 5) {
                document.querySelector(".dashboard-loader").classList.remove("loader");
            }
            
            return;
        }

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

        if(completedData >= 5) {
            document.querySelector(".dashboard-loader").classList.remove("loader");
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
        
        if(userDistrict && !(""+data.district).includes(userDistrict)) {
            
            if(counts == i) {
                completedData++;

                document.querySelectorAll(".count-districts").forEach(function(e, key) {

                    let district = e.getAttribute("data-district");

                    e.querySelector(".count-2").innerHTML = arrayDriversDistrict[district] ?? 0;

                });

            }

            if(completedData >= 5) {
                document.querySelector(".dashboard-loader").classList.remove("loader");
            }
            
            return;
        }

        noDrivers++;

        for(let district of data.district.split(",")) {
            if(!arrayDriversDistrict[district]) {
                arrayDriversDistrict[district] = 0;
            }
            arrayDriversDistrict[district]++;
        }

        document.querySelector(".count-drivers").innerHTML = noDrivers;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                e.querySelector(".count-2").innerHTML = arrayDriversDistrict[district] ?? 0;

            });

        }

        if(completedData >= 5) {
            document.querySelector(".dashboard-loader").classList.remove("loader");
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
        
        if(userDistrict && !(""+data.district).includes(userDistrict)) {

            if(counts == i) {
                completedData++;
    
                document.querySelectorAll(".count-districts").forEach(function(e, key) {
    
                    let district = e.getAttribute("data-district");
    
                    // e.querySelector(".count-3").innerHTML = arrayTrashesDistrict[district] ?? 0;
    
                });
    
            }

            if(completedData >= 5) {
                document.querySelector(".dashboard-loader").classList.remove("loader");
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

        document.querySelector(".count-trashes").innerHTML = noTrashes;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                // e.querySelector(".count-3").innerHTML = arrayTrashesDistrict[district] ?? 0;

            });

        }

        if(completedData >= 5) {
            document.querySelector(".dashboard-loader").classList.remove("loader");
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
    
                document.querySelectorAll(".count-districts").forEach(function(e, key) {
    
                    let district = e.getAttribute("data-district");
    
                    e.querySelector(".count-3").innerHTML = arrayPackagesCountDistrict[district] ?? 0;
    
                });
    
            }

            if(completedData >= 5) {
                document.querySelector(".dashboard-loader").classList.remove("loader");
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

        document.querySelector(".count-packages").innerHTML = countPackages;

        if(counts == i) {
            completedData++;

            document.querySelectorAll(".count-districts").forEach(function(e, key) {

                let district = e.getAttribute("data-district");

                e.querySelector(".count-3").innerHTML = arrayPackagesCountDistrict[district] ?? 0;

            });

        }

        if(completedData >= 5) {
            document.querySelector(".dashboard-loader").classList.remove("loader");
        }

    });

});




let noPayments = 0;
let countPayments = 0;
let arrayPaymentsDistrict = [];

fDatabase.ref('Payments').on('value', (list) => {

    noPayments = 0;
    countPayments = 0;
    arrayPaymentsDistrict = [];

    let i = 0;
    let counts = list.numChildren();
    list.forEach((item) => {

        i++;

        const id = item.key;
        const data = item.val();
        
        if(userDistrict && !(""+data.district).includes(userDistrict)) {

            if(counts == i) {
                completedData++;
    
                document.querySelectorAll(".count-districts").forEach(function(e, key) {
    
                    let district = e.getAttribute("data-district");
    
                    
    
                });
    
            }

            if(completedData >= 5) {
                document.querySelector(".dashboard-loader").classList.remove("loader");
            }
            
            return;
        }

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

        if(completedData >= 5) {
            document.querySelector(".dashboard-loader").classList.remove("loader");
        }

    });

});

document.querySelectorAll(".count-districts").forEach(function(e, key) {
    
    let district = e.getAttribute("data-district");

    if(userDistrict && userDistrict != district) {
        e.style.display = "none";
    }

});

window.addEventListener('popstate', (event) => {
    history.go(1);
});
history.pushState({ state: 1 }, '');
