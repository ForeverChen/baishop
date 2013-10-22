/**
 * 单点登录javascript操作类
 */
var oauth = {

	/**
	 * OAuth单点登录
	 * @param username 用户名
	 * @param password 密码
	 * @param loginUrl 单点登录地址
	 */
	login : function(username, password, loginUrl){
		var iframe = $('<iframe src="'+ loginUrl +'" style="position:absolute;left:-9999px;"></iframe>');
		$("body").append(iframe);
		iframe.load(function(){
			var dom;
			try{
				dom = $(this).contents();
				dom.find("#frmLogin").attr("target", "_parent");
				dom.find("#username").val(username);
				dom.find("#password").val(password);
				dom.find("#btnSubmit").click();
			}catch(e){
				dom = $(this);
				dom.find("#frmLogin").attr("target", "_parent");
				dom.find("#username").val(username);
				dom.find("#password").val(password);
				dom.find("#btnSubmit").click();
			}
		});
	},

	/**
	 * 注册用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @param loginUrl 单点登录地址，可选
	 * @param callback 登录成功后的回调，可选
	 */
	loginBaixcUser : function(username, password, loginUrl, callback){
		if(!loginUrl)
			loginUrl = "/baixc/ssoproxy/login?ajax=true";
	
		var params = "ssoUser.name=" + username.replace(/\&/g,"%26") +"&ssoUser.password=" + password.replace(/\&/g,"%26");
		
		$.ajax({
		    url: loginUrl,
		    data: params,
			type: "post",
		    dataType: 'jsonp',
		    jsonp: 'callback',
		    success: function(data, textStatus){
		    	if(callback)
					callback(true);
		    },
		    error: function(data, textStatus){
		    	if(callback)
					callback(false);
		    }
		});
	},

	/**
	 * 加载script
	 * @param url
	 * @param callback
	 */
	loadScript : function(url,success){
		var script = document.createElement('script');
		script.type = 'text/javascript';
		if(script.readyState){  
			//兼容IE
			script.onreadystatechange = function(){
				if(script.readyState == 'loaded' || script.readyState == 'complete'){
					script.onreadystatechange = null;
					success();
				}
			};
		}else{  
			//其他现代浏览器
			script.onload = function(){
				success();
			};
		}
		script.src = url;
		document.getElementsByTagName('head')[0].appendChild(script);
	}

};
