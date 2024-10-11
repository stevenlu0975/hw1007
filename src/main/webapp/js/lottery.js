/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
	const jwtToken = localStorage.getItem('jwtToken');
	const lotteryForm = document.querySelector('#lotteryForm');
	const errors = 	document.querySelector('#errors');
		
	lotteryForm.addEventListener('submit',(e)=>{
		const generateSets = document.querySelector('#generateSets').value;
		const excludeList = document.querySelector('#excludeList').value;
		e.preventDefault();
		console.log("generateSets "+generateSets);
		console.log("excludeList "+excludeList);
		if(jwtToken!=null){
			fetch("/lottery/lotteryController.do", {
			    method: "post",
			    headers: {
			        "Content-Type": "application/json",
					"Authorization": jwtToken
			    },
			    body: JSON.stringify({
			        sets: generateSets,
			        excludeNumbersString: excludeList,
			    }),
			})
			.then((response)=>{
				if(!response.ok){
					throw new Error('http error ${response.status}');
				}
				return  response.json(); 
			})
			.then((json)=>{
				console.log(json);
				// 獲取表格的 tbody 元素
				const tableBody = document.querySelector('#resultTable tbody');
				tableBody.innerHTML="";
				// 判斷後端數據是否成功返回
				if (json.code === 1 && Array.isArray(json.data)) {
					errors.textContent = "";
					// 遍歷每組號碼
				    json.data.forEach((group, index) => {
				        // 創建一個新的行
				        const row = document.createElement('tr');
				        
				        // 創建第一列顯示組數
				        const firstCell = document.createElement('td');
				        firstCell.textContent = `第${index + 1}組`;
				        row.appendChild(firstCell);
				        
				        // 創建後續的列顯示每組的號碼
				        group.forEach(number => {
				            const cell = document.createElement('td');
				            cell.textContent = number;
				            row.appendChild(cell);
				        });
				        
				        // 將新行加入到表格中
				        tableBody.appendChild(row);
				    });
				}
				else if(json.code === 0){
					errors.textContent = json.message;
					console.log(errors);
					if(json.message==="token expired"){
						alert("請重新登入")
						window.location.href = "login.html"; 
					}
					
				}
				
			})			
		}
	});
	

	
});