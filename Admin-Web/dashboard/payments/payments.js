
if(!userID) {
    window.location.replace("../../login/");
}

document.querySelector("tbody").innerHTML = "";
document.querySelector(".card-list").classList.add("loader");

let isDataTable = true;
fDatabase.ref('Payments').on('value', (list) => {

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
                    ${data.name ?? '-'}
                </td>
                <td class="text-center">
                    ${data.garbage ?? '-'}
                </td>
                <td class="text-center">
                    ${data.price ?? '-'} Rwf
                </td>
                <td class="text-center">
                    <span class="label gradient-4 btn-rounded">
                        ${data.packages ?? '1'}
                    </span>
                </td>
                <td class="text-center">
                    ${data.amount ?? '-'} Rwf
                </td>
                <td class="text-center">
                    ${data.method ?? '-'}
                </td>
                <td class="text-center">
                    <i class="fa fa-circle-o mr-2 ${data.isPaid ? 'text-success' : 'text-danger'}"></i>
                    ${data.isPaid ? 'Paid' : 'Not Paid'}
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