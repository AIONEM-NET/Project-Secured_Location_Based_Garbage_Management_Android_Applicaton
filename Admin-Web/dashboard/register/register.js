
const form = document.querySelector(".form-register");
form.addEventListener("submit", async function(e) {
    e.preventDefault();
    
    let name = document.querySelector("input[name='name']").value;
    let email = document.querySelector("input[name='email']").value;
    let phone = document.querySelector("input[name='phone']").value;
    let district = document.querySelector("select[name='district']").value;
    let password = document.querySelector("input[name='password']").value;
    let rePassword = document.querySelector("input[name='re-password']").value;

    if(password != rePassword) {
        alert("Both password don't match !!");
        return;
    }

    form.classList.add("loader");

    await fAuth.createUserWithEmailAndPassword(email, password)
    .then(async (userCredential) => {

        const user = userCredential.user;

        await fDatabase.ref('Drivers/' + user.uid).set({
            uid: user.uid,
            email: user.email,
            phone: phone,
            name : name,
            district : district,
        })
        .then(() => {

            alert("Registration successfully");

            window.location.replace("../drivers/");

        });

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
