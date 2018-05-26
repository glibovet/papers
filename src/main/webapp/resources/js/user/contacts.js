document.addEventListener("DOMContentLoaded", function (event) {
    var searchResultsSize = document.getElementById("searchResultsSize").value || null;
    var tabChecked = document.querySelector("input[name='tabs']:checked");
    console.log(searchResultsSize);

    if (searchResultsSize !== "0" && searchResultsSize !== 0 && searchResultsSize) {
        document.querySelector("input[name='tabs']:checked").checked = false;
        document.querySelector(".tab3").checked = true;
        console.log(document.querySelector("input[name='tabs']:checked"));
    }
});