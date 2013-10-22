package org.jasig.cas.web;

import java.io.PrintWriter;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.util.CasPcOrMobileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.baixc.ucenter.model.user.User;
import com.baixc.ucenter.service.user.UserService;
import com.google.code.kaptcha.Constants;

public class RegisterController extends AbstractController {

    @NotNull
    private String registerView;
    
    @Resource
	private UserService userService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String action = request.getParameter("action");
		if(action!=null){
			if(action.equals("register")){
				this.register(request, response);
				return null;
			}
			if(action.equals("check")){
				this.check(request, response);
				return null;
			}
		}
			
		return new ModelAndView(this.registerView);
	}
	
	public void register(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	    PrintWriter out = response.getWriter();

        try {
			// 判断验证码
        	if(!CasPcOrMobileUtils.isMobile(request)){
				String captcha = request.getParameter("captcha")==null?"":request.getParameter("captcha");
				String kaptcha = (String)request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
				if(!captcha.equals(kaptcha)) {
					out.print("{success:false, msg:'验证码不正确!'}");
					return;
				}
        	}
			
			//获取参数
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String repassword = request.getParameter("repassword");
			String email = request.getParameter("email");
			
			//验证数据
			if(!username.matches("^\\w{5,20}$")){
	            out.println("{success: false, msg: '会员名应为5-20个字符（只能为字母、数字、下划线）！'}");
	            return;
			}
			if(!password.matches("^.{6,20}$")){
	            out.println("{success: false, msg: '密码应为6-20个字符！'}");
	            return;
			}
			if(!password.equals(repassword)){
	            out.println("{success: false, msg: '密码不一致，请重输！'}");
	            return;
			}
			if(!email.matches("^(\\w)+(\\.\\w+)*@(\\w||\\-)+((\\.\\w+)+)$")){
	            out.println("{success: false, msg: '邮箱格式有误！'}");
	            return;
			}
			
			//判断用户是否存在
			User userByName = userService.getUser(username);
			if(userByName!=null){
	            out.println("{success: false, msg: '已存在相同的用户名！'}");
	            return;
			}
			
			//判断邮箱是否重复
			User userByEmail = userService.getUser(email);
			if(userByEmail!=null){
	            out.println("{success: false, msg: '已存在相同的邮箱！'}");
	            return;
			}
			
			//保存数据库
			long id = System.currentTimeMillis();
			User user = new User();
			
			//对user属性赋值
			initializeProperty(username, password, email, id, user);
			userService.addUser(user);
			
            // 输出数据
            out.println("{success: true}");
            
        } catch (Exception e) {
            out.println("{success: false, msg: '" + e.getMessage() + "'}");
            e.printStackTrace();
        }finally{
        	out.close();
        }
	}
	
	public void check(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

	    PrintWriter out = response.getWriter();

        try {
        	//判断用户是否存在
			String username = request.getParameter("username");
			if(StringUtils.isNotBlank(username)){
				User userByName = userService.getUser(username);
				if(userByName!=null){
		            out.println("{success: false, msg: '已存在相同的用户名！'}");
		            return;
				}
			}

			//判断邮箱是否重复
			String email = request.getParameter("email");
			if(StringUtils.isNotBlank(email)){
				User userByEmail = userService.getUser(email);
				if(userByEmail!=null){
		            out.println("{success: false, msg: '已存在相同的邮箱！'}");
		            return;
				}
			}

			// 判断验证码
			String captcha = request.getParameter("captcha");
			if(StringUtils.isNotBlank(captcha)){
	        	if(!CasPcOrMobileUtils.isMobile(request)){
					String kaptcha = (String)request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
					if(!captcha.equals(kaptcha)) {
						out.print("{success:false, msg:'验证码不正确!'}");
						return;
					}
	        	}
			}
			
            // 输出数据
            out.println("{success: true}");
            
        } catch (Exception e) {
            out.println("{success: false, msg: '" + e.getMessage() + "'}");
            e.printStackTrace();
        }finally{
        	out.close();
        }
	}
	
	/**
	 * 对user和userInfo对象属性赋值
	 */
	private void initializeProperty(String username, String password,
			String email, long userId, User user) {
		Date date = new Date();

		user.setUserId(userId);
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setRegTime(date);
		user.setLastUpdateTime(date);
		user.setEnabled((byte)1);
	}
	
    public void setRegisterView(final String registerView) {
        this.registerView = registerView;
    }

}
