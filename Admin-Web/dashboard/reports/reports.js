
if(!userID) {
    window.location.replace("../../login/");
}

document.querySelector("tbody").innerHTML = "";
document.querySelector(".card-list").classList.add("loader");

let isDataTable = true;
fDatabase.ref('Users').once('value', (list) => {

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
                <td>
                    ${data.name ?? '-'}
                </td>
                <td class="text-center">
                    ${data.phone ?? '-'}
                </td>
                <td>
                    ${data.message ?? '-'}
                </td>
                <td class="text-center">
                    ${new Date().toString().replace("GMT+0200 (Eastern European Standard Time)", "")}
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