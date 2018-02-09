;
$('#register_confirm').click(function (e) {
	
	//阻止原来的事件执行结果
	e.preventDefault() ;
	
	var username = $('#username').val()
	var customerName = $('#customerName').val()
	var password = $('#password').val()
	var confirmPass = $('#confirmPass').val()
	
	if ('' == username || '' == customerName || '' == password || '' == confirmPass) {
		layer.alert('信息输入有误！');
		return ;
	}
	if (password != confirmPass) {
		layer.alert('两次密码输入不同！');
		return ;
	}
	
	$.ajax({
		url : common.http_passwordTransformer + password ,
		async : false ,
		success : function (result) {
			password = result.password
			confirmPass = password
		}
	})
	
	var req = {
		"username" : username,
		"customerName" : customerName,
		"password" : password,
		"confirmPass" : confirmPass
	}
	
	console.log(req)
	
	$.ajax({
        url: common.http_userRegister,
        type: "POST",
        data: JSON.stringify(req),
        dataType: "JSON",
		contentType:"application/json;charset=UTF-8",
        success: function (data, status, xhr) {

        	if(data["_ReturnCode"] == "0000"){
        		layer.msg('注册成功！', {
					time: 0
				   ,btn: ['前往登录']
				   ,btn1: function(index, layero){
					   window.location.href = 'index.html'
				   }
				});
			} else {
				layer.alert(data["_ReturnMsg"]);
			}
        },
        error: function (xhr, errorType, error) {
        	if (xhr.status=="401") {
				layer.alert("用户名或密码错误");
			}
        }
    });
})