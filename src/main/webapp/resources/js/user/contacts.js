document.addEventListener("DOMContentLoaded", function (event) {
    var tab = document.getElementById("tab").value;
    if(tab === 'search'){
        document.querySelector("input[name='tabs']:checked").checked = false;
        document.querySelector(".tab3").checked = true;
    }else if(tab === 'requests'){
        document.querySelector("input[name='tabs']:checked").checked = false;
        document.querySelector(".tab2").checked = true;
    }else {
        document.querySelector("input[name='tabs']:checked").checked = false;
        document.querySelector(".tab1").checked = true;
    }
});