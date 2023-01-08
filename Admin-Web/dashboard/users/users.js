
if(!userID) {
    window.location.replace("../../login/");
}

document.querySelector("tbody").innerHTML = "";
document.querySelector(".card-list").classList.add("loader");

let isDataTable = true;
fDatabase.ref('Users').on('value', (list) => {

    let html = "";
    let i = 0;
    list.forEach((item) => {

        const id = item.key;
        const data = item.val();

        if(userDistrict && !(""+data.district).includes(userDistrict)) {
            return;
        }

        i++;
        let html1 = `
            <tr>
                <td>
                    ${i}
                </td>
                <td>
                    <img src="${data.photo ?? '../assets/images/user.png'}" alt=""/>
                </td>
                <td>
                    ${data.name ?? '-'}
                </td>
                <td class="text-center">
                    ${data.email ?? '-'}
                </td>
                <td class="text-center">
                    ${data.phone ?? '-'}
                </td>
                <td class="text-center">
                    ${data.district ?? '-'}
                </td>
                <td class="text-center">
                    ${data.houseNo ?? '-'}
                </td>
                <td class="text-center">
                    <a class="btn btn-sm font-weight-medium text-white ${data.isApproved == true ? 'btn-success' : 'btn-danger' }" onclick="onUserApproved('${id}', ${data.isApproved == true}, '${data.name}');" style="pointer: cursor;">
                        ${data.isApproved == true ? 'Approved' : 'Disabled'}
                    </a>
                </td>
            </tr>
        `;

        html = html1 + html;
        
    });

    document.querySelector("tbody").innerHTML = html;

    if(isDataTable) {
        $('.table').DataTable({
        dom: 'Bfrtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                exportOptions: {
                    columns: ':not(.no-export)'
                }
            },
            {
                extend: 'pdfHtml5',
                text: '<i class="fa fa-file-pdf-o"></i> PDF',
                exportOptions: {
                    columns: ':not(.no-export)'
                }
            },
            {
                extend: 'print',
                text: '<i class="fa fa-print"></i> PRINT',
                exportOptions: {
                    columns: ':not(.no-export)'
                }
            },
        ]
        });
        isDataTable = false;
    }

    document.querySelector(".card-list").classList.remove("loader");

});


function onUserApproved(id, isApproved, name) {

    const isYes = confirm(`Do you want to ${isApproved == true ? 'DISABLE' : 'APPROVE'} "${name}" ?`);

    if(isYes) {
    
        fDatabase.ref('Users/'+ id +'/isApproved').set(!(isApproved == true));

    }

}
