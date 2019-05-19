package xyz.timoney.swsad.controller;

import xyz.timoney.swsad.bean.Message;
import xyz.timoney.swsad.bean.questionnaire;
import xyz.timoney.swsad.mapper.QuesCollectUserMapper;
import xyz.timoney.swsad.mapper.QuesFillUserMapper;
import xyz.timoney.swsad.mapper.QuestionnaireMapper;
import xyz.timoney.swsad.singleton.SingletonMybatis;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@EnableAutoConfiguration
public class QuestionnaireController {

    private static SqlSessionFactory sqlSessionFactory;
    static {
        sqlSessionFactory =  SingletonMybatis.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            //得到映射器
            QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);

             //初始化用户填写问卷表格
            QuesFillUserMapper quesFillUserMapper = sqlSession.getMapper(QuesFillUserMapper.class);
            quesFillUserMapper.quesFillUserTableInit();

             //初始化用户收藏问卷表格
            QuesCollectUserMapper quesCollectUserMapper= sqlSession.getMapper(QuesCollectUserMapper.class);
            quesCollectUserMapper.quesCollectUserTableInit();
            //调用接口中的方法去执行xml文件中的SQL语句
            //初始化问卷表
            //questionnaireMapper.questionnaireTableInit();
            /*用户数量*/
            /*问卷数量*/
            int count = questionnaireMapper.getCount();
            questionnaire.initCount(count);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*获取问卷详情*/
    @RequestMapping(method = RequestMethod.GET,value = "/getQues/{quesID}")
    @CrossOrigin
    public Message<questionnaire> getQueseByID(@PathVariable int quesID){
        Message<questionnaire> message = new Message<>();
        questionnaire theQues;
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            theQues = quesMapper.getQuesByID(quesID);
            message.setData(theQues);
            message.setSuccess(true);
            message.setMsg("获取成功");
            //要提交后才会生效
            sqlSession.commit();
        } catch (Exception e) {
            message.setData(null);
            message.setSuccess(false);
            message.setMsg("获取失败:" + e.getMessage());
        }
        //最后记得关闭连接
        System.out.println(message);
        return message;
    }


    /*获取问卷详情*/
    @RequestMapping(method = RequestMethod.GET,value = "/getQuesCont/{quesID}")
    @CrossOrigin
    public Message<String> getQueseCont(@PathVariable int quesID){
        Message<String> message = new Message<>();
        String theQuesCont;
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            theQuesCont = quesMapper.getQuesCont(quesID);
            message.setData(theQuesCont);
            message.setSuccess(true);
            message.setMsg("获取成功");
            //要提交后才会生效
            sqlSession.commit();
        } catch (Exception e) {
            message.setData(null);
            message.setSuccess(false);
            message.setMsg("获取失败:" + e.getMessage());
        }
        //最后记得关闭连接

        return message;
    }

    /*获取所有问卷*/
    @RequestMapping(method = RequestMethod.GET,value = "/allques")
    @CrossOrigin
    public Message<List<questionnaire>> getQueses(){
        Message<List<questionnaire>> message = new Message<>();
        List<questionnaire> listQues;
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            listQues = quesMapper.getAllQues();
            message.setData(listQues);
            message.setSuccess(true);
            message.setMsg("获取成功");
            //要提交后才会生效
            sqlSession.commit();
        } catch (Exception e) {
            message.setData(null);
            message.setSuccess(false);
            message.setMsg("获取失败:" + e.getMessage());
        }
        //最后记得关闭连接

        return message;
    }

    /*插入一个问卷*/
    @RequestMapping(method = RequestMethod.POST,value = "/createques")
    @CrossOrigin
    public Message<String> createQues(@RequestBody questionnaire ques)
    {
        Message<String> message = new Message<>();
        System.out.println(ques);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            quesMapper.insert(ques);
            message.setSuccess(true);
            message.setMsg("创建成功");
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("创建失败:" + e.getMessage());
            return message;
        }
        return message;
    }

}
