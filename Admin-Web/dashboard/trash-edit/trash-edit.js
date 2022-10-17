

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const id = urlParams.get('id');

if(!id) {
    window.location.replace("../trashes-add/");
}

const form = document.querySelector(".form-add");

form.classList.add("loader");

let data = [];

fDatabase.ref('Trashes/' + id).once('value', (item) => {

    const id = item.key;
    data = item.val();

    document.querySelector("input[name='name']").value = data.name ?? "";
    document.querySelector("select[name='type']").value = data.type ?? "";
    document.querySelector("input[name='price']").value = data.price ?? "";
    document.querySelector("select[name='district']").value = data.districts ?? "";
    
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
    let type = document.querySelector("select[name='type']").value;
    let price = document.querySelector("input[name='price']").value;
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

    data.type = type;
    data.name = name;
    data.price = price;
    data.district = districts.toString();
    data.phone = districts;

    await fDatabase.ref('Trashes/' + id).set(data)
    .then(() => {

        alert("Trash updated successfully");

        window.location.replace("../trashes/");

    });


    form.classList.remove("loader");

});
