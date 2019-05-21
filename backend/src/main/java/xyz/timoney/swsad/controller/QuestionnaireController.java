package xyz.timoney.swsad.controller;

import xyz.timoney.swsad.bean.*;
import xyz.timoney.swsad.bean.quesUser.QuesCollectUser;
import xyz.timoney.swsad.bean.quesUser.QuesFillUser;
import xyz.timoney.swsad.bean.questionnaire.*;
import xyz.timoney.swsad.bean.questionnaire.QuesContent;
import xyz.timoney.swsad.bean.user.User;
import xyz.timoney.swsad.bean.user.UserState;
import xyz.timoney.swsad.bean.questionnaire.QuesContent;
import xyz.timoney.swsad.mapper.QuesCollectUserMapper;
import xyz.timoney.swsad.mapper.QuesFillUserMapper;
import xyz.timoney.swsad.mapper.QuestionnaireMapper;
import xyz.timoney.swsad.singleton.SingletonMybatis;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
            //Questionnaire.initCount(count);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*获取问卷详情*/
    @RequestMapping(method = RequestMethod.GET,value = "/getQues/{quesID}")
    @CrossOrigin
    public Message<Questionnaire> getQueseByID(@PathVariable int quesID){
        Message<Questionnaire> message = new Message<>();
        Questionnaire theQues;
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            theQues = quesMapper.getQuesByID(quesID);
            Infos temp=quesMapper.getInfo(theQues.getQuesID());
            theQues.setInfos(temp);

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


    /*获取问卷内容，即题目等*/
    @RequestMapping(method = RequestMethod.GET,value = "/getQuesCont/{quesID}")
    @CrossOrigin
    public Message<QuesContent> getQueseCont(@PathVariable int quesID){
        Message<QuesContent> message = new Message<>();
        QuesContent quesCont= new QuesContent();
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            //获取title
            String title = quesMapper.getTitleByID(quesID);
            //获取选择题，此处有bug
            List<Ques2_temp> ques2s_temp=new ArrayList<>();
            ques2s_temp=quesMapper.getQues2s(quesID);


            List<Ques2> ques2s= new ArrayList<>();
            for(int i=0;i<ques2s_temp.size();i++)
            {
                Ques2 te = new Ques2();
                te.setXuanID(ques2s_temp.get(i).getXuanID());
                te.setQuesID(ques2s_temp.get(i).getQuesID());
                te.setMode(ques2s_temp.get(i).getMode());
                te.setTitle(ques2s_temp.get(i).getTitle());
                te.setChoose(ques2s_temp.get(i).getChoose());
                te.setFill(ques2s_temp.get(i).isFill());

                String cho=new String();
                List<String> choices = new ArrayList<>();
                cho=ques2s_temp.get(i).getChoices();
                //必须要注意转义
                String [] choic=cho.split("\\$");
                choices.add(choic[0]);
                choices.add(choic[1]);
                choices.add(choic[2]);
                choices.add(choic[3]);
                te.setChoices(choices);

                ques2s.add(te);
            }


            //获取填空题
            List<Ques1> ques1s = quesMapper.getQues1s(quesID);
            //计算number
            int num = ques1s.size()+ques2s.size();

            quesCont.setQuesID(num);
            quesCont.setNumber(num);
            quesCont.setTitle(title);
            quesCont.setQues1(ques1s);
            quesCont.setQues2(ques2s);

            message.setData(quesCont);
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

    /*获取所有正在进行的问卷*/
    @RequestMapping(method = RequestMethod.GET,value = "/allques")
    @CrossOrigin
    public Message<List<Questionnaire>> getQueses(){
        Message<List<Questionnaire>> message = new Message<>();
        List<Questionnaire> listQues;
        //获取一个连接
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //得到映射器
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            listQues = quesMapper.getAllQues();
            for(int i=0; i<listQues.size();i++)
            {
                Infos temp=quesMapper.getInfo(listQues.get(i).getQuesID());
                listQues.get(i).setInfos(temp);
            }
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
    /*并且更新缓存中的用户创建表的List*/
    @RequestMapping(method = RequestMethod.POST,value = "/createques")
    @CrossOrigin
    public Message<String> createQues(@RequestBody Questionnaire ques)
    {
        /*
        @TODO  验证用户身份
        */
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
        /*
        @TODO  更新缓存
        */
        return message;
    }


    /**
     *
     *  收藏某个问卷
     * @param userCookieKey 用户cookie
     * @param quesId 问卷id
     * @return*/
    @RequestMapping(method = RequestMethod.PUT,value = "/questionnaires/{id}/collect")
    @CrossOrigin
    public Message<String> questionnaireCollect(@CookieValue("user") String userCookieKey, @PathVariable("id") int quesId)
    {
        /*
        验证用户身份
        */
        System.out.println("\nPUT /questionnaires/" + quesId + "/collect" + "\n");
        Message<String> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        Questionnaire questionnaire;
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //得到映射器
            QuesCollectUserMapper quesCollectUserMapper= sqlSession.getMapper(QuesCollectUserMapper.class);
            List<Integer> collecterList = quesCollectUserMapper.getAllCollected(userId);
            //判断是否重复收藏
            if(collecterList.contains(userId)){
                message.setSuccess(false);
                message.setMsg("收藏问卷失败: 已经收藏了此问卷");
                System.out.println(message);
                return message;
            }
            //添加记录
            quesCollectUserMapper.insert(new QuesCollectUser(quesId, userId));
            //获得问卷对象
            QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            questionnaire = questionnaireMapper.getQuesByID(quesId);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("收藏问卷失败: 出现异常");
            System.out.println(message);
            return message;
        }
        //同步通知发送到缓存中的用户
        for(User u : User.cacheList){
            if(u.getId() == userId){
                u.getCollected().add(questionnaire);
            }
        }
        message.setSuccess(true);
        message.setMsg("收藏问卷成功");
        System.out.println(message);
        return message;
    }


    /**
     *
     *  取消收藏某个问卷
     * @param userCookieKey 用户cookie
     * @param quesId 问卷id
     * @return*/
    @RequestMapping(method = RequestMethod.DELETE,value = "/questionnaires/{id}/collect")
    @CrossOrigin
    public Message<String> questionnaireCollectCancel(@CookieValue("user") String userCookieKey, @PathVariable("id") int quesId)
    {
        /*
        验证用户身份
        */
        System.out.println("\nDELETE /questionnaires/" + quesId + "/collect" + "\n");
        Message<String> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //得到映射器
            QuesCollectUserMapper quesCollectUserMapper= sqlSession.getMapper(QuesCollectUserMapper.class);
            List<Integer> collecterList = quesCollectUserMapper.getAlsCollector(quesId);
            //判断是否没有收藏过
            if(!collecterList.contains(userId)){
                message.setSuccess(false);
                message.setMsg("取消收藏问卷失败: 没有收藏过此问卷");
                System.out.println(message);
                return message;
            }
            //删除记录
            quesCollectUserMapper.delete(new QuesCollectUser(quesId, userId));
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("取消收藏问卷失败: 出现异常");
            System.out.println(message);
            return message;
        }
        //同步缓存中的用户
        for(User u : User.cacheList){
            if(u.getId() == userId){
                u.getCollected().removeIf(ques -> ques.getQuesID() == quesId);
            }
        }
        message.setSuccess(true);
        message.setMsg("取消收藏问卷成功");
        System.out.println(message);
        return message;
    }

    /**
     * @param userCookieKey 用户cookie
     * @return 用户收藏所有问卷的集合
     */
    @RequestMapping(method = RequestMethod.GET,value = "/questionnaires/collect/all")
    @CrossOrigin
    public Message<List<Integer>> questionnaireCollectList(@CookieValue("user") String userCookieKey)
    {
        /*
        验证用户身份
        */
        System.out.println("\nGET /questionnaires/collect/all"  + "\n");
        Message<List<Integer>> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //得到映射器
            QuesCollectUserMapper quesCollectUserMapper= sqlSession.getMapper(QuesCollectUserMapper.class);
            //获取收藏者列表
            List<Integer> list = quesCollectUserMapper.getAllCollected(userId);
            message.setData(list);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("获取收藏问卷id失败: 出现异常");
            System.out.println(message);
            return message;
        }
        /**
         * @TODO 增加缓存
         * */
        message.setSuccess(true);
        message.setMsg("获取收藏问卷id成功");
        System.out.println(message);
        return message;
    }

    /**
     * 填写某个问卷
     * */
    @RequestMapping(method = RequestMethod.PUT,value = "/questionnaires/{id}/fill")
    @CrossOrigin
    public Message<String> questionnaireFill(@CookieValue("user") String userCookieKey, @PathVariable("id") int quesId)
    {
        /*
        验证用户身份
        */
        System.out.println("\nPUT /questionnaires/"+ quesId + "/fill" + "\n");
        Message<String> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        Questionnaire questionnaire;
        int fillerCount;
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //获得问卷填写者列表
            QuesFillUserMapper quesFillUserMapper = sqlSession.getMapper(QuesFillUserMapper.class);
            List<Integer> fillerList = quesFillUserMapper.getAllFiller(quesId);
            //判断是否重复填写
            if(fillerList.contains(userId)){
                message.setSuccess(false);
                message.setMsg("填写问卷失败: 已经填写了此问卷");
                System.out.println(message);
                return message;
            }
            fillerCount = fillerList.size();
            //获取问卷详情
            QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            questionnaire = questionnaireMapper.getQuesByID(quesId);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("填写问卷失败: 获取问卷出现异常");
            System.out.println(message);
            return message;
        }
        /**
         * 人数是否已满
         * */
        if(questionnaire.getInfos().getTotal() > fillerCount ){
            //修改数据库
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                //新加一条参与的记录
                QuesFillUserMapper quesFillUserMapper = sqlSession.getMapper(QuesFillUserMapper.class);
                quesFillUserMapper.insert(new QuesFillUser(quesId, userId));
                //获得问卷本身
                QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
                //attend加一
                int affect = questionnaireMapper.addOneFill();
                System.out.println("Affect : " + affect);
            } catch (Exception e) {
                e.printStackTrace();
                message.setSuccess(false);
                message.setMsg("填写问卷失败: 填写问卷出现异常");
                System.out.println(message);
                return message;
            }

            //同步缓存中的用户
            for(User u : User.cacheList){
                if(u.getId() == userId){
                    u.getFilled().add(questionnaire);
                }
            }
            message.setSuccess(true);
            message.setMsg("开始填写问卷");
            System.out.println(message);
            return message;
        }
        /**
         * 人数已满
         * */
        message.setSuccess(false);
        message.setMsg("填写问卷失败: 人数已满");
        System.out.println(message);
        return message;
    }

    /**
     * 放弃填写某个问卷
     * */
    @RequestMapping(method = RequestMethod.DELETE,value = "/questionnaires/{id}/fill")
    @CrossOrigin
    public Message<String> questionnaireFillCancel(@CookieValue("user") String userCookieKey, @PathVariable("id") int quesId)
    {
        /*
        验证用户身份
        */
        System.out.println("\nDELETE /questionnaires/"+ quesId + "/fill" + "\n");
        Message<String> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //获得问卷填写者列表
            QuesFillUserMapper quesFillUserMapper = sqlSession.getMapper(QuesFillUserMapper.class);
            List<Integer> fillerList = quesFillUserMapper.getAllFiller(quesId);
            //判断是否没有填写过
            if(!fillerList.contains(userId)){
                message.setSuccess(false);
                message.setMsg("取消填写问卷失败: 没有填写过此问卷");
                System.out.println(message);
                return message;
            }
            //删除记录
            quesFillUserMapper.delete(new QuesFillUser(quesId, userId));
            //attend减1
            QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            int affect = questionnaireMapper.cancelOneFill();
            System.out.println("Affect : " + affect);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("取消填写问卷失败: 获取问卷出现异常");
            System.out.println(message);
            return message;
        }

        //同步缓存中的用户
        for(User u : User.cacheList){
            if(u.getId() == userId){
                u.getFilled().removeIf(ques -> ques.getQuesID() == quesId);
            }
        }
        message.setSuccess(true);
        message.setMsg("已取消填写问卷");
        System.out.println(message);
        return message;
    }

    /**
     * 用户填写的所有问卷id
     * @param userCookieKey 用户cookie
     * @return 用户填写所有问卷的集合
     */
    @RequestMapping(method = RequestMethod.GET,value = "/questionnaires/fill/all")
    @CrossOrigin
    public Message<List<Integer>> questionnaireFillList(@CookieValue("user") String userCookieKey)
    {
        /*
        验证用户身份
        */
        System.out.println("\nGET /questionnaires/fill/all"  + "\n");
        Message<List<Integer>> message = new Message<>();
        int userId = UserState.verifyCookie(userCookieKey, message);
        if(!message.isSuccess()){
            return message;
        }
        //修改数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            //得到映射器
            QuesFillUserMapper quesFillUserMapper = sqlSession.getMapper(QuesFillUserMapper.class);
            //调用接口中的方法去执行xml文件中的SQL语句
            List<Integer> list = quesFillUserMapper.getAllFilled(userId);
            message.setData(list);
        } catch (Exception e) {
            e.printStackTrace();
            message.setSuccess(false);
            message.setMsg("获取填写问卷id失败: 出现异常");
            System.out.println(message);
            return message;
        }
        /**
         * @TODO 增加缓存
         * */
        message.setSuccess(true);
        message.setMsg("获取填写问卷id成功");
        System.out.println(message);
        return message;
    }

    /*添加问卷的填空题*/
    @RequestMapping(method = RequestMethod.POST,value = "/addtian")
    @CrossOrigin
    public Message<String> addTian(@RequestBody List<Ques1> tians)
    {
        Message<String> message = new Message<>();
        System.out.println(tians);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            for(int i=0;i<tians.size();i++) {
                quesMapper.insertTian(tians.get(i));
            }
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

    /*添加问卷的选择题*/
    @RequestMapping(method = RequestMethod.POST,value = "/addxuan")
    @CrossOrigin
    public Message<String> addXuan(@RequestBody List<Ques2> xuans)
    {
        Message<String> message = new Message<>();
        System.out.println(xuans);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            QuestionnaireMapper quesMapper = sqlSession.getMapper(QuestionnaireMapper.class);
            for(int i=0;i<xuans.size();i++) {
                Ques2_temp ques2_temp=new Ques2_temp();
                ques2_temp.setXuanID(xuans.get(i).getXuanID());
                ques2_temp.setQuesID(xuans.get(i).getQuesID());
                ques2_temp.setMode(xuans.get(i).getMode());
                ques2_temp.setTitle(xuans.get(i).getTitle());
                ques2_temp.setChoose(xuans.get(i).getChoose());
                ques2_temp.setFill(xuans.get(i).isFill());
                List<String> te = xuans.get(i).getChoices();
                String re = String.join("$",te);
                ques2_temp.setChoices(re);
                quesMapper.insertXuan(ques2_temp);
            }
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
