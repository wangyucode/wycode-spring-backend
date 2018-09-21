package cn.wycode.web.controller;

import cn.wycode.web.entity.*;
import cn.wycode.web.repository.*;
import cn.wycode.web.service.StorageService;
import cn.wycode.web.service.WXSessionService;
import cn.wycode.web.utils.EncryptionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/public/fish")
@Api(value = "fish", description = "养鱼小助手", tags = "Fish")
public class FishController {

    private Log log = LogFactory.getLog(FishController.class);

    private final FishBaikeRepository baikeRepository;
    private final FishSuggestRepository suggestRepository;
    private final FishQuestionRepository questionRepository;
    private final WXSessionService sessionService;
    private final StorageService storageService;
    private final FishUserRepository userRepository;
    private final FishAnswerRepository answerRepository;
    private final FishHandBookRepository fishHandBookRepository;
    private final FishCollectionRepository collectionRepository;

    @Autowired
    public FishController(FishHandBookRepository fishHandBookRepository, FishBaikeRepository baikeRepository, FishSuggestRepository suggestRepository, FishQuestionRepository questionRepository, WXSessionService sessionService, StorageService storageService, FishUserRepository userRepository, FishAnswerRepository answerRepository, FishCollectionRepository collectionRepository) {
        this.baikeRepository = baikeRepository;
        this.suggestRepository = suggestRepository;
        this.questionRepository = questionRepository;
        this.sessionService = sessionService;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.fishHandBookRepository = fishHandBookRepository;
        this.collectionRepository = collectionRepository;
    }

    @ApiOperation(value = "根据类型查询百科，按阅读量倒序排序")
    @RequestMapping(method = RequestMethod.GET, path = "/getBaike")
    public JsonResult<List<FishBaike>> getBaike(@RequestParam String type) {
        List<FishBaike> fishBaikes = baikeRepository.findByTypeOrderByReadCountDesc(type);
        return JsonResult.Companion.data(fishBaikes);
    }

    @ApiOperation(value = "根据类型查询图鉴，按收藏量倒序排序")
    @RequestMapping(method = RequestMethod.GET, path = "/getFishHandBook")
    public JsonResult<List<FishHandBook>> getFishHandBook(@RequestParam String type) {
        List<FishHandBook> fishHandBooks = fishHandBookRepository.findByTypeOrderByCollectCountDesc(type);
        return JsonResult.Companion.data(fishHandBooks);
    }

    @ApiOperation(value = "添加图鉴")
    @RequestMapping(method = RequestMethod.POST, path = "/addFishHandBook")
    public JsonResult<FishHandBook> getFishHandBook(@RequestParam String handBookName, @RequestParam String handBookDetail,
                                                    @RequestParam String handBookImageUrl, @RequestParam String type) {
        FishHandBook fishHandBook = new FishHandBook(handBookName, handBookDetail, handBookImageUrl, new Date(), type);
        return JsonResult.Companion.data(fishHandBookRepository.save(fishHandBook));
    }

    @ApiOperation(value = "图鉴收藏")
    @RequestMapping(method = RequestMethod.POST, path = "/addCollection")
    public JsonResult<FishCollection> addCollection(@RequestParam long id, @RequestParam String accessKey) {
        FishHandBook fishHandBook = fishHandBookRepository.findById(id).orElse(null);
        FishUser user = userRepository.findByKey(accessKey);
        if (fishHandBook == null) {
            return JsonResult.Companion.error("图鉴不存在");
        }

        if (user == null) {
            return JsonResult.Companion.error("已在其它地方登录");
        }
        fishHandBook.setCollectCount(fishHandBook.getCollectCount() + 1);
        fishHandBook = fishHandBookRepository.save(fishHandBook);

        FishCollection collection = new FishCollection(fishHandBook, user);
        collectionRepository.save(collection);
        return JsonResult.Companion.data(collection);
    }

    @ApiOperation(value = "图鉴收藏删除")
    @RequestMapping(method = RequestMethod.POST, path = "/deleteCollection")
    public JsonResult<FishCollection> deleteCollection(@RequestParam long id, @RequestParam String accessKey) {
        FishCollection collection = collectionRepository.findById(id).orElse(null);
        FishUser user = userRepository.findByKey(accessKey);
        if (collection == null) {
            return JsonResult.Companion.error("收藏不存在");
        }

        if (user == null) {
            return JsonResult.Companion.error("已在其它地方登录");
        }

        FishHandBook handBook = collection.getHandBook();
        handBook.setCollectCount(handBook.getCollectCount() - 1);
        fishHandBookRepository.save(handBook);

        collectionRepository.deleteById(id);
        return JsonResult.Companion.data(collection);
    }

    @ApiOperation(value = "获取我的收藏列表")
    @RequestMapping(method = RequestMethod.GET, path = "/getMyCollection")
    public JsonResult<List<FishCollection>> getMyQuestions(@RequestParam String accessKey) {
        List<FishCollection> collections = collectionRepository.findAllByUser_KeyOrderByCreateTimeDesc(accessKey);
        return JsonResult.Companion.data(collections);
    }

    @ApiOperation(value = "是否已收藏图鉴")
    @RequestMapping(method = RequestMethod.GET, path = "/isCollected")
    public JsonResult<Long> isCollected(@RequestParam String accessKey, @RequestParam Long id) {
        FishCollection collection = collectionRepository.findByUser_KeyAndHandBook_Id(accessKey, id);
        if (collection == null) {
            return JsonResult.Companion.data(-1L);
        } else {
            return JsonResult.Companion.data(collection.getId());
        }

    }


    @ApiOperation(value = "增加百科阅读量")
    @RequestMapping(method = RequestMethod.GET, path = "/addReadCount")
    public JsonResult<FishBaike> addReadCount(@RequestParam long id) {
        FishBaike baike = baikeRepository.findById(id).orElse(null);
        if (baike != null) {
            baike.setReadCount(baike.getReadCount() + 1);
            baike = baikeRepository.save(baike);
        }
        return JsonResult.Companion.data(baike);
    }

    @ApiOperation(value = "添加百科")
    @RequestMapping(method = RequestMethod.POST, path = "/addBaike")
    public JsonResult<FishBaike> addBaike(@RequestParam String type, @RequestParam String title, @RequestParam String detail, @RequestParam String imageName) {
        FishBaike baike = new FishBaike(type, title, detail, imageName, new Date());
        return JsonResult.Companion.data(baikeRepository.save(baike));
    }

    @ApiOperation(value = "意见反馈")
    @RequestMapping(method = RequestMethod.POST, path = "/suggest")
    public JsonResult<FishSuggest> suggest(@RequestParam String content, @RequestParam String contact) {
        FishSuggest suggest = new FishSuggest(new Date(), content, contact);
        return JsonResult.Companion.data(suggestRepository.save(suggest));
    }

    @ApiOperation(value = "添加问题")
    @RequestMapping(method = RequestMethod.POST, path = "/addQuestion")
    public JsonResult<FishQuestion> addQuestion(@RequestParam String accessKey, @RequestParam String content, @RequestParam String title, @RequestParam(required = false) String images) {
        FishUser user = userRepository.findByKey(accessKey);
        if (user == null) {
            return JsonResult.Companion.error("accessKey错误");
        }
        List<String> imageList = null;
        if (!StringUtils.isEmpty(images)) {
            imageList = Arrays.asList(images.split(","));
        }
        FishQuestion question = new FishQuestion(title, content, user, imageList);
        question = questionRepository.save(question);

        if (imageList != null && imageList.size() > 0) {
            String questionFolder = "question/" + question.getId().toString();
            for (String image : imageList) {
                try {
                    storageService.moveTempFileToFolder(image, questionFolder);
                } catch (IOException e) {
                    log.error("文件挪动失败，文件:" + image + ",文件夹:" + questionFolder, e);
                }
            }
        }

        return JsonResult.Companion.data(question);
    }

    @ApiOperation(value = "分页获取问题列表")
    @RequestMapping(method = RequestMethod.GET, path = "/getQuestionsPage")
    public JsonResult<Page<FishQuestion>> getQuestions(@RequestParam int page, @RequestParam int size) {
        PageRequest request = PageRequest.of(page, size);
        Page<FishQuestion> questions = questionRepository.findByOrderByUpdateTimeDesc(request);
        return JsonResult.Companion.data(questions);
    }

    @ApiOperation(value = "获取我的问题列表")
    @RequestMapping(method = RequestMethod.GET, path = "/getMyQuestionsPage")
    public JsonResult<Page<FishQuestion>> getMyQuestions(@RequestParam int page, @RequestParam int size, @RequestParam String accessKey) {
        PageRequest request = PageRequest.of(page, size);
        Page<FishQuestion> questions = questionRepository.findByUser_Key(accessKey, request);
        return JsonResult.Companion.data(questions);
    }

    @ApiOperation(value = "获取我的回答")
    @RequestMapping(method = RequestMethod.GET, path = "/getMyAnswers")
    public JsonResult<List<FishQuestionAnswer>> getMyAnswers(@RequestParam String accessKey) {
        ArrayList<Sort.Order> orderArrayList = new ArrayList<>(2);
        orderArrayList.add(new Sort.Order(Sort.Direction.DESC, "value"));
        orderArrayList.add(new Sort.Order(Sort.Direction.ASC, "createTime"));
        Sort orders = Sort.by(orderArrayList);
        List<FishQuestionAnswer> answers = answerRepository.findByUser_Key(accessKey, orders);
        return JsonResult.Companion.data(answers);
    }

    @ApiOperation(value = "获取问题")
    @RequestMapping(method = RequestMethod.GET, path = "/getQuestion")
    public JsonResult<FishQuestion> getQuestion(@RequestParam Long id) {
        FishQuestion question = questionRepository.findById(id).orElse(null);
        return JsonResult.Companion.data(question);
    }

    @ApiOperation(value = "获取问题回答")
    @RequestMapping(method = RequestMethod.GET, path = "/getQuestionAnswers")
    public JsonResult<List<FishQuestionAnswer>> getQuestions(@RequestParam Long id) {
        ArrayList<Sort.Order> orderArrayList = new ArrayList<>(2);
        orderArrayList.add(new Sort.Order(Sort.Direction.DESC, "value"));
        orderArrayList.add(new Sort.Order(Sort.Direction.ASC, "createTime"));
        Sort orders = Sort.by(orderArrayList);
        List<FishQuestionAnswer> answers = answerRepository.findAllByQuestion_Id(id, orders);
        return JsonResult.Companion.data(answers);
    }

    @ApiOperation(value = "问题回答")
    @RequestMapping(method = RequestMethod.POST, path = "/postAnswer")
    public JsonResult<FishQuestionAnswer> postAnswer(@RequestParam String accessKey, @RequestParam Long id, @RequestParam String content) {
        FishUser user = userRepository.findByKey(accessKey);
        if (user == null) {
            return JsonResult.Companion.error("用户session异常，请重新登录");
        }
        FishQuestion question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return JsonResult.Companion.error("问题不存在");
        }
        question.setUpdateTime(new Date());
        questionRepository.save(question);
        FishQuestionAnswer answer = new FishQuestionAnswer(content, question, user);
        return JsonResult.Companion.data(answerRepository.save(answer));
    }

    @ApiOperation(value = "回答顶踩")
    @RequestMapping(method = RequestMethod.POST, path = "/answerLikeOrDislike")
    public JsonResult<FishQuestionAnswer> answerLikeOrDislike(@RequestParam Long id, @RequestParam int like) {
        FishQuestionAnswer answer = answerRepository.findById(id).orElse(null);
        if (answer == null) {
            return JsonResult.Companion.error("问题不存在");
        }
        if (like > 0) {
            answer.up();
        } else {
            answer.down();
        }
        return JsonResult.Companion.data(answerRepository.save(answer));
    }

    @ApiOperation(value = "置顶问题")
    @RequestMapping(method = RequestMethod.GET, path = "/questionUp")
    public JsonResult<FishQuestion> questionUp(@RequestParam Long id) {
        FishQuestion question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return JsonResult.Companion.error("问题不存在");
        }
        Date now = new Date();
        if (now.getTime() - question.getUpdateTime().getTime() < 12 * 3600 * 1000) {
            return JsonResult.Companion.error("12小时内只能置顶1次");
        }
        question.setUpdateTime(now);
        return JsonResult.Companion.data(questionRepository.save(question));
    }

    @ApiOperation(value = "获取微信Session")
    @RequestMapping(path = "/wx/getSession", method = RequestMethod.GET)
    public JsonResult<String> getSession(@RequestParam String jsCode) {
        WXSession session = sessionService.getWXSession(jsCode);
        if (session != null &&
                !StringUtils.isEmpty(session.getSession_key()) &&
                !StringUtils.isEmpty(session.getOpenid())
        ) {
            log.info(session.toString());
            String accessKey = EncryptionUtil.getHash(session.getSession_key(), EncryptionUtil.MD5);
            FishUser user = userRepository.findByOpenId(session.getOpenid());
            if (user == null) {
                user = new FishUser(session.getOpenid());
            }
            user.setKey(accessKey); //一旦登录就刷新key
            log.info(user.toString());
            userRepository.save(user);
            return JsonResult.Companion.data(accessKey);
        } else {
            log.error("/wx/getSession-->" + jsCode + "-->" + (session == null ? "null" : session.toString()));
            return JsonResult.Companion.error("未获取到session");
        }
    }

    @ApiOperation(value = "更新用户信息")
    @RequestMapping(path = "/updateUserInfo", method = RequestMethod.POST)
    public JsonResult<FishUser> updateUserInfo(@RequestParam String accessKey,
                                               @RequestParam String avatarUrl,
                                               @RequestParam String city,
                                               @RequestParam String country,
                                               @RequestParam int gender,
                                               @RequestParam String language,
                                               @RequestParam String nickName,
                                               @RequestParam String province) {
        FishUser user = userRepository.findByKey(accessKey);
        if (user != null) {
            user.setAvatarUrl(avatarUrl);
            user.setNickName(nickName);
            user.setLanguage(language);
            user.setCity(city);
            user.setCountry(country);
            user.setGender(gender);
            user.setProvince(province);
            user.setUpdateTime(new Date());
        }
        return JsonResult.Companion.data(userRepository.save(user));
    }

}
