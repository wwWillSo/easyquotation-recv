;
$('#login_confirm').click(function (e) {
	
	//阻止原来的事件执行结果
	e.preventDefault() ;
	
	var username = $('#username').val()
	var password = $('#password').val()
	
	$.ajax({
		url : common.http_passwordTransformer + password ,
		async : false ,
		success : function (result) {
			password = result.password
		}
	})
	
	var data = {
		"username" : username,
		"password" : password
	}
	
	$.ajax({
        url: common.http_userLogin,
        type: "POST",
        data: data,
        dataType: "JSON",
		contentType:"application/x-www-form-urlencoded;charset=UTF-8",
        success: function (data, status, xhr) {

        	if(data["statusCode"] == "ACCESS_GRANTED"){
				var seconds=1*60*60;
				locache.set("sessionId",data["sessionId"],seconds);
				sessionStorage.setItem("session",data["sessionId"]);
//				locache.set("JSESSIONID",data["sessionId"],seconds);
//				sessionStorage.setItem("JSESSIONID",data["sessionId"]);
				
				//添加cookie
//				document.cookie="JSESSIONID="+data["sessionId"];
				
				layer.confirm('登录成功！')
			   	window.location.href="pc/stockList.html";
				
			} else if(data["errorCode"] == "用户名或密码错误") {
				layer.alert("用户名或密码错误");
			}
        },
        error: function (xhr, errorType, error) {
        	if (xhr.status=="401") {
				layer.alert("用户名或密码错误");
			}
        }
    });
})