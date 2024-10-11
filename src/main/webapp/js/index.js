/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
	const jwtToken = localStorage.getItem('jwtToken'); // 假設 token 存在 localStorage 中
	const loginBtn = document.querySelector('#loginBtn');
	const registerBtn = document.querySelector('#registerBtn');
	const logoutBtn = document.querySelector('#logoutBtn');
	const lotteryButton = document.querySelector('#lotteryButton');
	const lotteryDiv = document.querySelector('#lotteryDiv');
	console.log(jwtToken);
	// 禁用或啟用按鈕
	if(jwtToken===null || jwtToken.trim()==""){
		setNavgationBarButton(false);
	}else{
	    // 已登入狀態，隱藏登入和註冊按鈕
		setNavgationBarButton(true);
	}

	logoutBtn.addEventListener('click',(e)=> {
		localStorage.removeItem("jwtToken");
		jwtToken=null;
		console.log('remove');
		
	});
	/**
	 * @param bool true 設定目前為登入狀態, false 設定目前為登出狀態
	 * 
	 **/
	function setNavgationBarButton(bool){
		if(bool){
	        loginBtn.classList.add('hidden');
	        registerBtn.classList.add('hidden');
		}else{
	    	// 未登入狀態，隱藏登出按鈕
	    	logoutBtn.classList.add('hidden');
		}
	};
	lotteryButton.addEventListener('click', (e)=>{
		e.preventDefault();
		if(jwtToken!=null ){
			lotteryDiv.style.display='block';
			lotteryButton.style.display="none"
		}else{
			window.location.href = "login.html"; 
		}

	});
});