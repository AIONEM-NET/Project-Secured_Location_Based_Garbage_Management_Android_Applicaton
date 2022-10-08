
window.localStorage.clear();

const form = document.querySelector("form");
form.addEventListener("submit", async function(e) {
    e.preventDefault();
    
    let email = document.querySelector("input[name='email']").value;
    let password = document.querySelector("input[name='password']").value;

    form.classList.add("loader");

    await fAuth.signInWithEmailAndPassword(email, password)
    .then((userCredential) => {

        const user = userCredential.user;
        
        window.localStorage.setItem("userID", user.uid);
        window.localStorage.setItem("userEmail", user.email);
        window.localStorage.setItem("userName", user.displayName);
        window.localStorage.setItem("userAccount", user.displayName);

        if(false) {
            user.updateProfile({
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
    
            alert("Login Successfully");

            window.location.replace("../dashboard/");
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
