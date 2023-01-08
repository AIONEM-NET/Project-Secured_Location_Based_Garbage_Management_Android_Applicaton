
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const id = urlParams.get('id');

if(!id) {
    window.location.replace("../admins/");
}

const form = document.querySelector(".form-register");

form.classList.add("loader");

let data = [];

fDatabase.ref('Admins/' + id).once('value', (item) => {

    const id = item.key;
    data = item.val();

    document.querySelector("input[name='name']").value = data.name ?? "";
    document.querySelector("input[name='email']").value = data.email ?? "";
    document.querySelector("input[name='phone']").value = data.phone ?? "";
    document.querySelector("select[name='district']").value = data.district ?? "";
    
    let i = 0;
    for(let option of document.querySelector("select[name='district']").options){
        if(option.value && (data.district).includes(option.value)) {
            option.selected = true;
        }
        i++;
    }

    form.classList.remove("loader");

});


form.addEventListener("submit", async function(e) {
    e.preventDefault();
    
    let name = document.querySelector("input[name='name']").value;
    let email = document.querySelector("input[name='email']").value;
    let phone = document.querySelector("input[name='phone']").value;
    let district = document.querySelector("select[name='district']").value;

    form.classList.add("loader");

    var districts = [];
    let i = 0;
    for(let option of document.querySelector("select[name='district']").options){
        if(option.selected) {
            let value = option.value;
            if(value) {
                districts.push(value);
            }
        }
        i++;
    }

    data.phone = phone;
    data.name = name;
    data.district = districts.toString();
    data.districts = districts;

    await fDatabase.ref('Admins/' + id).set(data)
    .then(() => {

        alert("Admin updated successfully");

        window.location.replace("../admins/");

    });

    form.classList.remove("loader");

});
