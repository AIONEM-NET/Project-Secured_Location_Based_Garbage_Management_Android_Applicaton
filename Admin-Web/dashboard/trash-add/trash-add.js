
const form = document.querySelector(".form-add");
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


    const id = fDatabase.ref('Trashes').push().key;

    await fDatabase.ref('Trashes/' + id).set({
        id: id,
        type: type,
        name : name,
        price: price,
        district : districts.toString(),
        districts : districts,
    })
    .then(() => {

        alert("Trash added successfully");

        window.location.replace("../trashes/");

    });


    form.classList.remove("loader");

});
