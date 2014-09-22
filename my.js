function createTable(data) {
    var persons = JSON.parse(data);
    var res = "<table>";
    res += "<tr><th>Id</th><th>Name</th><th>Age</th></tr>";
    for (var i = 0; i < persons.length; i++)
    {
        res += "<tr>";
        res += "<td>" + persons[i].id + "</td>";
        res += "<td>" + persons[i].name + "</td>";
        res += "<td>" + persons[i].age + "</td>";
        res += "</tr>";
    }
    res += "</table>";
    return res;
}

function showAllPersons() {
    $.get("http://localhost:4000/person", function (data) {
        var res = createTable(data);
        $("#result").html(res);
    });
}

function addPerson() {
    var data = "{ id: -1, name: " + $("#personName").val() +
            ", age: " + $("#personAge").val() + "}";
    $.ajax({
        url: "http://localhost:4000/person",
        type: "POST",
        data: data
    }).done(function (data) {
        var res = createTable(data);
        $("#result").html(res);
    }).fail(function (data) {

    });
}

function deletePerson() {
    var id = $("#personId").val();
    $.ajax({
        url: "http://localhost:4000/person/" + id,
        type: "DELETE"
    });
}

