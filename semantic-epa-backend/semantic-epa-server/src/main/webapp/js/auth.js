var authUrl = "http://localhost:8080/semantic-epa-backend/api/";

function attemptLogin() {
	$.ajax({
		url: authUrl + "user/login",
		data: "username="+$('#inputEmail').val()+"&password="+$('#inputPassword').val(),
		type: 'POST',
	}).done(function(data) {
        console.log(data); 
		if (data.success) 
			window.location = "index.html";
		else 
			$('#messages').replaceWith('<div class="alert alert-danger"><b>' + data.notifications[0].title +'</b><br/>' +data.notifications[0].description + '</div>');
    });
}

function registerUser() {
	$.ajax({
		url: authUrl + "user/register",
		data: "username="+$('#inputEmail').val()+"&password="+$('#inputPassword').val(),
		type: 'POST',
	}).done(function(data) {
        console.log(data); 
		if (data.success) 
			window.location = "login.html";
		else
			$('#messages').replaceWith('<div class="alert alert-danger"><b>' + data.notifications[0].title +'</b><br/>' +data.notifications[0].description + '</div>');
    });
}

//See: https://stackoverflow.com/questions/14220321/how-to-return-the-response-from-an-asynchronous-call
function isUserAuthenticated() {
    $.ajax({
        url: authUrl + "user/authc",
        type: 'GET',
    }).done(function(ret) {
        console.log(ret);
            if (ret.success) {
            	window.location="index.html";
            } else {
                return false;
            }
    });
}

function getPrincipalName() {
    $.ajax({
        url: authUrl + "user/authc",
        type: 'GET',
    }).done(function(ret) {
        console.log(ret);
            if (ret.success) {
				$("#login-label").replaceWith(ret.notifications[0].title);
            } else {
				window.location="login.html";
            }
    });
}

function attemptLogout() {
	$.ajax({
		url: authUrl + "user/logout",
		type: 'GET',
	}).done(function(data) {
        console.log(data); 
		if (data.success) 
			window.location = "login.html";
    });
}
