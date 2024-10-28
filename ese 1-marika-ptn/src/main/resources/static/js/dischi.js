function aggiungiDisco() {
    const titolo = document.getElementById('titolo').value;
    const artista = document.getElementById('artista').value;
    const anno = document.getElementById('anno').value;

    if (titolo && artista && anno) {
        const table = document.getElementById('dischiTable').getElementsByTagName('tbody')[0];
        const newRow = table.insertRow();

        newRow.innerHTML = `
            <td>${titolo}</td>
            <td>${artista}</td>
            <td>${anno}</td>
            <td><button onclick="rimuoviDisco(this)">Rimuovi</button></td>
        `;

        // Pulisci i campi di input
        document.getElementById('titolo').value = '';
        document.getElementById('artista').value = '';
        document.getElementById('anno').value = '';
    } else {
        alert('Per favore, compila tutti i campi.');
    }
}

function rimuoviDisco(button) {
    const row = button.parentNode.parentNode;
    row.parentNode.removeChild(row);
}