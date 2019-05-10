package cn.janking.swsad.controller;

import cn.janking.swsad.bean.Message;
import cn.janking.swsad.bean.User;
import cn.janking.swsad.mapper.UserMapper;
import cn.janking.swsad.singleton.SingletonMybatis;
import com.fasterxml.jackson.core.JsonFactory;
import com.sun.xml.internal.ws.api.model.MEP;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletContext;
import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static cn.janking.swsad.bean.Util.export;

@RestController
@EnableAutoConfiguration
public class Controller {

    private static SqlSessionFactory sqlSessionFactory;
    static {
        sqlSessionFactory =  SingletonMybatis.getSqlSessionFactory();
        /*用户数量*/
        User.initCount(sqlSessionFactory.openSession(true).getMapper(UserMapper.class).getCount());
    }

    //如果方法上的RequestMapping没有value，则此方法默认被父路径调用
    //默认回调
    @RequestMapping
    private String index(){
        return "API也会404哦!";
    }
    @RequestMapping("/hello")
    private String hello(){
        return "Hello World!";
    }
    //这里体现了restful风格的请求，按照请求的类型，来进行增删查改。
    //设计restful api（其实也就是URL），不要有冗余，例如不要写成getUsers，URL中
    //最好不要有动词。
    /*初始化表*/
    @RequestMapping(method = RequestMethod.GET,value = "/admin")
    public boolean usersTableInit(){
        //获取一个连接,自动提交
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            //得到映射器
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            userMapper.userTableInit();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            //最后记得关闭连接
            sqlSession.close();
        }
        return true;
    }
    /*获取所有用户*/
    @RequestMapping(method = RequestMethod.GET,value = "/users")
    public Message<List<User>> getUsers(){
        Message<List<User>> message = new Message<>();
        List<User> listUsers;
        //获取一个连接
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            //得到映射器
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            listUsers = userMapper.getUsers();
            message.setData(listUsers);
            message.setSuccess(true);
            message.setMsg("获取成功");
            //要提交后才会生效
            sqlSession.commit();
        }catch (Exception e){
            message.setData(null);
            message.setSuccess(false);
            message.setMsg("获取失败:" + e.getMessage());
        }
        finally {
            //最后记得关闭连接
            sqlSession.close();
        }

        return message;
    }
    /*注册：插入用户数据
     * 成功返回true
     * 用户名存在返回false*/
    @RequestMapping(method = RequestMethod.POST,value = "/register")
    public Message<String> register(@RequestBody User user){
        Message<String> message = new Message<>();
        System.out.println(user);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insert(user);
            message.setSuccess(true);
            message.setMsg("注册成功");
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("注册失败:" + e.getMessage());
            return message;
        }finally {
            sqlSession.close();
        }
        return message;
    }
    /*登录：判断用户名和密码是否一致
     * 一致返回当前用户的信息
     * 不一致返回null*/
    @RequestMapping(method = RequestMethod.POST,value = "/user")
    public Message<User> login(@RequestBody User loginUser){
        Message<User> message = new Message<>();
        User user = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //判断是手机还是邮箱
        System.out.println(loginUser.getPhone() + loginUser.getEmail());
        if(loginUser.getPhone() != null && !loginUser.getPhone().isEmpty() && loginUser.getPhone().charAt(0) != '$'){
            try {
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                user = userMapper.getByPhone(loginUser.getPhone());
                sqlSession.commit();
            }catch (Exception e){
                e.printStackTrace();
                message.setMsg("登录失败:" + e.getMessage());
                return message;
            }finally {
                sqlSession.close();
            }
        }
        else if(loginUser.getEmail() != null && !loginUser.getEmail().isEmpty() && loginUser.getEmail().charAt(0) != '$'){
            try {
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                user = userMapper.getByEmail(loginUser.getEmail());
                sqlSession.commit();
            }catch (Exception e){
                e.printStackTrace();
                message.setMsg("登录失败:" + e.getMessage());
                return message;
            }finally {
                sqlSession.close();
            }
        }
        //判断密码是否正确
        if(user!=null && user.getPassword().equals(loginUser.getPassword())){
            message.setMsg("登录成功");
            message.setSuccess(true);
            message.setData(user);
            return message;
        }
        message.setMsg("登录失败：用户名或密码错误");
        message.setSuccess(false);
        message.setData(null);
        return message;
    }
    /*更新用户数据*/
    @RequestMapping(method = RequestMethod.PUT,value = "/user")
    public Message<String> updateUser(@RequestBody User user){
        Message<String> message = new Message<>();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        boolean result;
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            result = userMapper.updateUser(user);
            message.setSuccess(result);
            message.setMsg("修改成功");
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
            message.setMsg("修改失败:" + e.getMessage());
            message.setSuccess(false);
        }
        finally {
            sqlSession.close();
        }
        return message;
    }

    /*上传用户头像,返回图片URL*/
    @RequestMapping("/upload")
    @ResponseBody
    public Message handleFileUpload(@RequestParam("file") MultipartFile file) {
        Message<String> message = new Message<>();
        if (!file.isEmpty()) {
            try {
                //获取跟目录
                File path = new File(ResourceUtils.getURL("classpath:").getPath());
                String fileName = path.getAbsolutePath() + "/static/faces/" + file.getOriginalFilename();
                if(!path.exists())
                    path = new File("");
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(fileName)));
                System.out.println(file.getName());
                message.setSuccess(true);
                message.setMsg("上传成功");
                message.setData(fileName);
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                message.setSuccess(false);
                message.setMsg("上传失败," + e.getMessage());
                return message;
            } catch (IOException e) {
                e.printStackTrace();
                message.setSuccess(false);
                message.setMsg("上传失败," + e.getMessage());
                return message;
            }

            return message;

        } else {
            message.setSuccess(false);
            message.setMsg("上传失败,文件为空！" );
            return message;
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/report")
    @ResponseBody
    public File testDownload() {

                //获取跟目录
                String fileName = "C:\\Users\\Janking\\Desktop\\嵌入式操作系统\\嵌入式操作系统作业模板_Linux内核.doc";

        return new File(fileName);



    }

    /*删除指定用户
    * 通过id查找
    * 不用验证密码！！*/
    @RequestMapping(method = RequestMethod.DELETE,value = "/user/{userId}")
    public Message<String> deleteUser(@PathVariable int userId){
        Message<String> message = new Message<>();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int result;
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            result = userMapper.deleteUser(userId);
            if(result > 0){
                message.setSuccess(true);
                message.setMsg("删除成功");
            }else{
                message.setSuccess(false);
                message.setMsg("不存在该用户");
            }
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
            message.setMsg("删除失败:"+e.getMessage());
            return message;
        }
        finally {
            sqlSession.close();
        }
        return message;
    }


}
