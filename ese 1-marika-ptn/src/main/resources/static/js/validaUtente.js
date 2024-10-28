function validateLogin(event) {
    const username = document.getElementById('user').value;
    const password = document.querySelector('input[name="password"]').value;

    // Controlla se i campi sono vuoti
    if (username.trim() === "" || password.trim() === "") {
        alert("Username e password sono obbligatori.");
        event.preventDefault(); // Impedisce l'invio del form
        return false;
    }
    console.log(username, password);
    return true; // Permette l'invio del form
}

function validateSignup(event) {
    const nome = document.getElementById('nome').value;
    const cognome = document.getElementById('cognome').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const comple = document.getElementById('comple').value;
    const radioOptions = document.getElementsByName('radio_option');
    let radioChecked = false;

    // Controlla se i campi sono vuoti
    if (nome.trim() === "" || cognome.trim() === "" || email.trim() === "" || password.trim() === "" || comple.trim() === "") {
        alert("Tutti i campi sono obbligatori.");
        event.preventDefault(); // Impedisce l'invio del form
        return false;
    }

    // Controlla se almeno un'opzione radio è selezionata
    for (let i = 0; i < radioOptions.length; i++) {
        if (radioOptions[i].checked) {
            radioChecked = true;
            break;
        }
    }
    if (!radioChecked) {
        alert("Devi selezionare un'opzione.");
        event.preventDefault(); // Impedisce l'invio del form
        return false;
    }

    return true; // Permette l'invio del form
}