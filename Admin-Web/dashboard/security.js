
if(!userID) {
    window.location.replace("../../login/");
}


document.querySelector(".fuser-name").innerHTML = userName;

if(document.querySelector(".facc-table-district")) {
    document.querySelector(".facc-table-district").innerHTML = (userAccount != "Admin" ? "All" : userDistrict) +" ";
}

if(userAccount == "SuperAdmin") {

    document.querySelectorAll(".facc-admin").forEach(function(e) {

        e.style.display = "none";

    });

    document.querySelectorAll(".facc-superadmin").forEach(function(e) {

        e.style.display = "block";

    });

}else if(userAccount == "Admin") {

    document.querySelectorAll(".facc-admin").forEach(function(e) {

        e.style.display = "block";

    });

    document.querySelectorAll(".facc-superadmin").forEach(function(e) {

        e.style.display = "none";

    });

}else {

    document.querySelectorAll(".facc-admin").forEach(function(e) {

        e.style.display = "none";

    });

    document.querySelectorAll(".facc-superadmin").forEach(function(e) {

        e.style.display = "none";

    });

}
