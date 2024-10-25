
function validate1(event){
    event.preventDefault(); //blocca il comportamento di default
    casellaTesto=document.getElementById("user");
    content=casellaTesto.value;
    if (content.includes("@")){
        let form=document.getElementById("formSubmit");
        form.submit();

    alert("ok");}
    else {alert("NO");}
    alert(" il tuo nome utente è: "+content);

}

window.addEventListener("load", function(){
    //nell'html non devo richiamare la funzione
    form=document.getElementsByName("formSubmit");
    form.addEventListener("submit", validate1);});