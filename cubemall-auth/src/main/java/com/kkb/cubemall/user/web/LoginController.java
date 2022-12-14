package com.kkb.cubemall.user.web;

import com.alibaba.fastjson.JSON;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.user.entity.UserEntity;
import com.kkb.cubemall.user.pojo.UserDTO;
import com.kkb.cubemall.user.service.UserService;
import com.kkb.cubemall.user.utils.Constants;
import com.kkb.cubemall.user.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName LoginController
 * @Description
 * @Author hubin
 * @Date 2021/5/18 17:00
 * @Version V1.0
 **/
@Controller
public class LoginController {

    // 注入service服务对象
    @Autowired
    private UserService userService;


    /**
     * @Description: 访问登录页面
     * @Author: hubin
     * @CreateDate: 2021/5/18 17:14
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/18 17:14
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @RequestMapping("/")
    public String showLogin(){
        return "login";
    }

    /**
     * @Description: 登录认证的接口实现
     * @Author: hubin
     * @CreateDate: 2021/5/18 16:44
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/18 16:44
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    //这里传递的是JSON对象格式的参数 而并非JSON字符串 不需要@RequestBody注解
    @RequestMapping("/login")
    public String login(UserDTO userDTO,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response){
        // 调用服务层登录方法
        R r = userService.login(userDTO);
        // 判断是否登录成功
        if(r.getCode()==0){

            // 获取token
            String token = (String) r.get("msg");

            // 登录成功后 将token写入cookie
            CookieUtils.setCookie(request,
                    response,
                    Constants.COOKIE_LOGIN_KEY,
                    token,
                    86400,
                    true);

            // 返回到首页，详情页
            return "redirect:http://localhost:8081/15.html";
        }else{
            //否则登录失败
            //重新登录
            return "redirect:http://localhost:8082";
        }

    }

    /**
     * @Description: 根据token查询用户身份信息
     * @Author: hubin
     * @CreateDate: 2021/5/19 18:27
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/19 18:27
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     * @url : http://localhost:8082/user/info/"+token
     */
    @ResponseBody
    @RequestMapping("/user/info/{token}")
    public String userInfo(@PathVariable String token,String callback){
        // 调用服务方法，查询用户身份信息
        R r  = userService.userInfo(token);

        // 判断是否是跨域 如果不存在跨域以JSON字符串返回
        if(StringUtils.isBlank(callback)){
            return JSON.toJSONString(r);
        }
        // 是跨域 则通过callback封装返回
        return callback + "("+JSON.toJSONString(r)+")";

    }


}

