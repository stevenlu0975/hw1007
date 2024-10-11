/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
	const registerForm = document.getElementById("loginForm");
	const usernameInput = document.querySelector("#username");
	const passwordInput = document.querySelector("#password");
	const errors = document.getElementById("errors");
	registerForm.addEventListener("submit", function(event) {
	    event.preventDefault(); // 防止表单提交后刷新页面

	    const username = document.querySelector("#username").value;
	    const password = document.querySelector("#password").value;
	
		// 发送 POST 请求到 /login
	    fetch("/lottery/login", {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/json",
	        },
	        body: JSON.stringify({
	            username: username,
	            password: password,
	        }),
	    })
		.then((response)=>{
			if(!response.ok){
				throw new Error('http error ${response.status}');
			}
			return  response.json();
		})
	    .then()
	    .then(json => {
			console.log(json);
	        if (json.code === 1) { 
	            const token = json.data;
				errors.textContent = "";
				console.log("token: "+token);
	            localStorage.setItem("jwtToken", token);
	            window.location.href = "index.html"; 
	        } else {
				errors.textContent = json.message; 
				localStorage.removeItem("token");
	        }
	    })
	    .catch(error => {
	        console.error("Error:", error);
	    });	
	    
	});
});
