/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
    
	const registerForm = document.getElementById("registerForm");
	const usernameInput = document.getElementById("username");
	const passwordInput = document.getElementById("password");
	const confirmPasswordInput = document.getElementById("confirmPassword");

	const usernameError = document.getElementById("usernameError");
	const passwordError = document.getElementById("passwordError");
	const confirmPasswordError = document.getElementById("confirmPasswordError");
	const errors = document.getElementById("errors");
	//用於追蹤各個驗證項目的狀態。

	let isUsernameValid = false;
	let isConfirmPasswordValid = false;
	
	// 1. 當 username 失去焦點時檢查是否重複
	   usernameInput.addEventListener("blur", async () => {
	       const username = usernameInput.value.trim();
	       if (username === "") {
	           usernameError.textContent = "Username 不能為空";
	           isUsernameValid = false;
	           return;
	       }
	       await checkUsername(username);
	   });

	   // 獨立的函數：檢查用戶名
	   async function checkUsername(username) {

	           const response = await fetch(`/lottery/check_user_name`,{
				method: "post",
				headers:{
					"Content-Type": "application/json"
				},
				body: JSON.stringify({ username: username })
			   })
			   .then((response)=>{
					if(!response.ok){
						throw new Error('http error ${response.status}');
					}
				return response.json();
			   })
			   .then((json)=>{
					console.log(json)
					if(json.code==1){
						console.log(json.code)
						usernameError.textContent = "";
						isUsernameValid = true;
					}else{
						console.log(json.code)
						usernameError.textContent = json.message;
						isUsernameValid = false;
					}
			   })
			   .catch((error) => {
					console.log(error);
			   });
			
	   }
	   confirmPasswordInput.addEventListener('blur', e =>{
		const password = passwordInput.value;
	   	const confirmPassword = confirmPasswordInput.value;
	//	console.log(password);
	//	console.log(confirmPassword);
       	if (confirmPassword === "") {
           	confirmPasswordError.textContent = "確認密碼不能為空";
           	isConfirmPasswordValid = false;
           return;
       	}

       	if (password !== confirmPassword) {
           	confirmPasswordError.textContent = "密碼不匹配";
           	isConfirmPasswordValid = false;
       	}
		else if(password.length<5){
			confirmPasswordError.textContent = "密碼長度必須大於5";
			isConfirmPasswordValid = false;
		} 
		else {
        	confirmPasswordError.textContent = "";
           	isConfirmPasswordValid = true;
       	}
	   	});
		
		registerForm.addEventListener("submit", async (event) => {
		        event.preventDefault(); // 防止默認提交

		        const username = usernameInput.value.trim();
		        const password = passwordInput.value;
		        const confirmPassword = confirmPasswordInput.value;
				
			if(isUsernameValid && isConfirmPasswordValid){
				const response = await fetch("/lottery/register", {
	               method: "POST",
	               headers: {
	                   "Content-Type": "application/json"
	               },
	               body: JSON.stringify({
	                   username: username,
	                   password: password,
					   confirmPassword: confirmPassword,
	               })
	           })
			   .then((resp)=>resp.json())
			   .then((json)=>{
				if(json.code==1){
					console.log(json.code)
					errors.textContent = "";
					window.location.href = '/lottery/login.html';  // 成功後自動跳轉到 login.html
				}else{
					console.log(json.code)
					errors.textContent = json.message;

				}
			   })
			   .catch((error) => {
				errors.textContent= error;
					console.log(error);
				});
			}
		});
		console.log('finished')

});