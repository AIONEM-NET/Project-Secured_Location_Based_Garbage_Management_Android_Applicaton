
window.localStorage.clear();

const form = document.querySelector("form");
form.addEventListener("submit", async function(e) {
    e.preventDefault();
    
    let email = document.querySelector("input[name='email']").value;
    let password = document.querySelector("input[name='password']").value;

    form.classList.add("loader");

    await fAuth.signInWithEmailAndPassword(email, password)
    .then(async (userCredential) => {

        const user = userCredential.user;

        if(true || user.emailVerified) {
        
            window.localStorage.setItem("userID", user.uid);
            window.localStorage.setItem("userEmail", user.email);
            window.localStorage.setItem("userName", user.displayName);
            window.localStorage.setItem("userAccount", user.displayName);

            if(false) {
                await user.updateProfile({
                    displayName: "SuperAdmin"
                }).then(() => {
                    window.localStorage.setItem("userName", "SuperAdmin");
                    window.localStorage.setItem("userAccount", "SuperAdmin");
                    alert("Login Successfully");
                    window.location.replace("../dashboard/");
                }).catch((error) => {
                    const errorCode = error.code;
                    let errorMessage = error.message;
                    if(errorMessage) {
                        alert(errorMessage);
                    }
                }); 
            }else {

                if(user.displayName != "Admin" && user.displayName != "SuperAdmin") {
                    alert("You don't have Administrator Previledges !!!");
                    return;
                }
        
                let isApproved = true;

                if(user.displayName == "Admin") {

                    await fDatabase.ref('Admins/' + user.uid).once('value', (item) => {

                        const id = item.key;
                        const data = item.val();

                        if(!data.isApproved) {
                            isApproved = false;

                            alert("You are not activated");
                        }else {

                            window.localStorage.setItem("userDistrict", data.district);
                            window.localStorage.setItem("userDistricts", data.districts);

                        }

                    });

                }
        
                if(isApproved) {
                    alert("Login Successfully");

                    window.location.replace("../dashboard/");
                }
            }

        }else {

            await user.sendEmailVerification()
            .then(() => {

                alert("Check your email to verifiy your account first");

            })
            .catch((error) => {
                const errorCode = error.code;
                let errorMessage = error.message;
                if(errorMessage) {
                    alert(errorMessage);
                }
            });
            
        }

    })
    .catch((error) => {
        const errorCode = error.code;
        let errorMessage = error.message;
        if(errorMessage) {
            alert(errorMessage);
        }
    });

    form.classList.remove("loader");

});


document.querySelector(".reset-password").addEventListener("click", function() {

    let email = document.querySelector("input[name='email']").value;

    if(!email) {
        alert("Enter your email")
        return;
    }

    form.classList.add("loader");

    fAuth.sendPasswordResetEmail(email)
    .then(() => {

        alert("Check your email to reset your password");

        form.classList.remove("loader");

    })
    .catch((error) => {
        const errorCode = error.code;
        let errorMessage = error.message;
        if(errorMessage) {
            alert(errorMessage);
        }
    });

});


window.addEventListener('popstate', (event) => {
    history.go(1);
});
history.pushState({ state: 1 }, '');
