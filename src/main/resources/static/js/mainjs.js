$(document).ready(function () {

  // ========== PROFILE PAGE: load /users/me ==========
  if (document.getElementById("profile")) {
    $.ajax({
      type: "GET",
      url: "/users/me",
      dataType: "json",
      contentType: "application/json; charset=utf-8",
      beforeSend: function (xhr) {
        if (localStorage.token) {
          xhr.setRequestHeader("Authorization", "Bearer " + localStorage.token);
        }
      },
      success: function (data) {
        $("#profile").text(data.fullName);
        $("#images").attr("src", data.images || "/images/u1.jpg");
      },
      error: function (e) {
        $("#feedback").text(e.responseText || "You are not logged in.");
        window.location.href = "/login";
      }
    });
  }

  // ========== LOGOUT ==========
  $("#logout").click(function () {
    localStorage.clear();
    window.location.href = "/login";
  });

  // ========== LOGIN ==========
  $("#Login").click(function () {
    var email = $("#email").val();
    var password = $("#password").val();

    var basicInfo = JSON.stringify({
      email: email,
      password: password
    });

    $.ajax({
      type: "POST",
      url: "/auth/login",
      dataType: "json",
      contentType: "application/json; charset=utf-8",
      data: basicInfo,
      success: function (data) {
        localStorage.token = data.token;
        window.location.href = "/user/profile";
      },
      error: function (e) {
        $("#feedback").text("Login failed: " + (e.responseText || ""));
      }
    });
  });

});