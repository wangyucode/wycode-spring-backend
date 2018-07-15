package cn.wycode.web;

import cn.wycode.web.controller.FishController;
import cn.wycode.web.entity.FishQuestion;
import cn.wycode.web.entity.FishQuestionAnswer;
import cn.wycode.web.entity.JsonResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class WebApplicationTests {

//    @Autowired
//    FishController fishController;

    @Test
    public void contextLoads() {
//        JsonResult<Page<FishQuestion>> result = fishController.getMyQuestions(0, 10, "7D9AD54C644F9E93F1E3C6CE27F5C1D7");
//        assert result.isSuccess();
//        assert result.getData().getTotalElements()>0;
//        System.out.println(result.getData().getContent().get(0).getTitle());
//        JsonResult<List<FishQuestionAnswer>>  answer = fishController.getMyAnswers("7D9AD54C644F9E93F1E3C6CE27F5C1D7");
//        assert answer.getData().size()>0;
//        System.out.println(answer.getData().get(0).getContent());
    }

}
