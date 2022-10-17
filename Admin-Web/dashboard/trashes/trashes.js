
if(!userID) {
    window.location.replace("../../login/");
}

document.querySelector("tbody").innerHTML = "";
document.querySelector(".card-list").classList.add("loader");

let isDataTable = true;
fDatabase.ref('Trashes').on('value', (list) => {

    let html = "";
    let i = 0;
    list.forEach((item) => {

        const id = item.key;
        const data = item.val();

        i++;
        let html1 = `
            <tr>
                <td>
                    ${i}
                </td>
                <td class="text-center">
                    ${data.name ?? '-'}
                </td>
                <td class="text-center">
                    ${data.type ?? '-'}
                </td>
                <td class="text-center">
                    ${data.district.split(",").length >= 30 ? "All" : (data.district ?? '-').replaceAll(",", ", ")}
                </td>
                <td class="text-center">
                    ${data.price ?? '-'}
                </td>
                <td class="text-center" style="display: inline-flex; gap: 4px;">
                    <a class="btn btn-sm font-weight-medium text-white btn-secondary" href="../trash-edit/?id=${id}" style="pointer: cursor;">
                        <i class="fa fa-edit"></i> <span>Edit</span>
                    </a>
                    <a class="btn btn-danger btn-sm font-weight-medium text-white"
                       onclick="onTrashDelete('${id}', '${data.name}');">
                        <i class="fa fa-trash"></i>
                        Delete
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



function onTrashDelete(id, name) {

    const isYes = confirm(`Do you want to DELETE "${name}" ?`);

    if(isYes) {
    
        fDatabase.ref('Trashes/'+ id).remove();

    }

}
