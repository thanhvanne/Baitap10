function extractErrorMessage(xhr) {
  let data = xhr.responseJSON;

  if (!data && xhr.responseText) {
    try { data = JSON.parse(xhr.responseText); } catch (e) {}
  }

  if (data) return data.description || data.detail || data.title || "Request failed";
  return "Request failed";
}

function showFeedback(msg) {
  const el = $("#feedback");
  if (!el.length) return;
  el.removeClass("d-none").text(msg);
}

function hideFeedback() {
  const el = $("#feedback");
  if (!el.length) return;
  el.addClass("d-none").text("");
}

$(document).ready(function () {

  // ===== PROFILE PAGE =====
  if (document.getElementById("profile")) {
    hideFeedback();

    // show JWT from localStorage (demo)
    if (document.getElementById("jwtText")) {
      $("#jwtText").val(localStorage.token || "");
    }

    $("#copyJwt").click(async function () {
      try {
        await navigator.clipboard.writeText(localStorage.token || "");
      } catch (e) {
        // fallback
        const t = document.getElementById("jwtText");
        t.select();
        document.execCommand("copy");
      }
    });

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
        $("#profile").text(data.fullName || "(no name)");
        $("#emailText").text(data.email || "");

        $("#userIdText").text(data.id != null ? data.id : "");
        $("#imagePathText").text(data.images || "");

        const imgPath = data.images || "/images/u1.jpg";
        $("#images").attr("src", imgPath);

        document.getElementById("images").onerror = function () {
          this.onerror = null;
          this.src = "/images/u1.jpg";
        };
      },
      error: function (xhr) {
        showFeedback(extractErrorMessage(xhr));
        localStorage.clear();
        setTimeout(() => window.location.href = "/login", 600);
      }
    });
  }

  // ===== LOGOUT =====
  $("#logout").click(function () {
    localStorage.clear();
    window.location.href = "/login";
  });

  // ===== LOGIN =====
  $("#Login").click(function () {
    hideFeedback();

    const email = $("#email").val();
    const password = $("#password").val();

    $.ajax({
      type: "POST",
      url: "/auth/login",
      dataType: "json",
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify({ email, password }),
      success: function (data) {
        // Lưu JWT vào localStorage
        localStorage.token = data.token;
        localStorage.expiresIn = data.expiresIn;

        window.location.href = "/user/profile";
      },
      error: function (xhr) {
        showFeedback(extractErrorMessage(xhr));
      }
    });
  });

});