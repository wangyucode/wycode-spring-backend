package cn.wycode.web.controller;

import cn.wycode.web.entity.*;
import cn.wycode.web.repository.ClipboardRepository;
import cn.wycode.web.repository.ClipboardSuggestRepository;
import cn.wycode.web.repository.WXClipboardRepository;
import cn.wycode.web.service.WXSessionService;
import cn.wycode.web.utils.EncryptionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/clipboard")
@Api(value = "clipboard", description = "剪切板", tags = "Clipboard")
public class ClipboardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9'};

    private ClipboardRepository repository;
    private final WXClipboardRepository wxClipboardRepository;
    private ClipboardSuggestRepository suggestRepository;
    private final WXSessionService sessionService;
    private final Random random = new Random();

    @Autowired
    public ClipboardController(ClipboardRepository repository, WXClipboardRepository wxClipboardRepository, ClipboardSuggestRepository suggestRepository, WXSessionService sessionService) {
        this.repository = repository;
        this.wxClipboardRepository = wxClipboardRepository;
        this.suggestRepository = suggestRepository;
        this.sessionService = sessionService;
    }

    @Deprecated
    @ApiOperation(value = "查询剪切板")
    @RequestMapping(method = RequestMethod.GET, path = "/query")
    public JsonResult<Clipboard> query(@RequestParam long id) {
        Clipboard p = repository.findById(id).orElse(null);
        return JsonResult.builder().data(p).build();
    }

    @ApiOperation(value = "通过WXKEY查询剪切板")
    @RequestMapping(method = RequestMethod.GET, path = "/queryByKey")
    public JsonResult<WXClipboard> queryByKey(@RequestParam String key) {
        WXClipboard p = wxClipboardRepository.findByKey(key);
        if (p == null) {
            return JsonResult.builder().error("invalid key").build();
        }
        return JsonResult.builder().data(p).build();
    }

    @ApiOperation(value = "通过id查询剪切板")
    @RequestMapping(method = RequestMethod.GET, path = "/queryById")
    public JsonResult<WXClipboard> queryById(@RequestParam String id) {
        WXClipboard p = wxClipboardRepository.findById(id).orElse(null);
        return JsonResult.builder().data(p).build();
    }

    @ApiOperation(value = "通过ID保存剪切板")
    @RequestMapping(method = RequestMethod.POST, path = "/saveById")
    public JsonResult<WXClipboard> saveById(@RequestParam String id, @RequestParam String content, @RequestParam String tips) {
        WXClipboard p = wxClipboardRepository.findById(id).orElse(null);
        if (p != null) {
            p.setContent(content);
            p.setTips(tips);
            p.setUpdateTime(new Date());
            p = wxClipboardRepository.save(p);
        }
        return JsonResult.builder().data(p).build();
    }

    @ApiOperation(value = "意见反馈")
    @RequestMapping(method = RequestMethod.POST, path = "/suggest")
    public JsonResult<ClipboardSuggest> suggest(@RequestParam String content, @RequestParam String contact) {
        ClipboardSuggest suggest = new ClipboardSuggest(new Date(), content, contact);
        return JsonResult.builder().data(suggestRepository.save(suggest)).build();
    }

    @ApiOperation(value = "获取微信Session")
    @RequestMapping(path = "/getSession", method = RequestMethod.GET)
    public JsonResult<WXClipboard> getSession(@RequestParam String jsCode) {
        WXSession session = sessionService.getWXSessionForClipboard(jsCode);
        if (session != null) {
            String accessKey = EncryptionUtil.getHash(session.getSession_key(), EncryptionUtil.MD5);
            WXClipboard clipboard = wxClipboardRepository.findByOpenid(session.getOpenid());
            if (clipboard == null) {
                String id = generateId();
                clipboard = new WXClipboard(id, session.getOpenid(), "请输入或粘贴你想保存的内容，内容可在网页端：https://clipboard.wycode.cn 使用查询码查询。");
                logger.info(clipboard.toString());
            }
            clipboard.setKey(accessKey); //一旦登录就刷新key

            return JsonResult.builder().data(wxClipboardRepository.save(clipboard)).build();
        } else {
            return JsonResult.builder().error("未获取到session").build();
        }
    }

    private String generateId() {
        String id = generateShortUuid();
        while (wxClipboardRepository.existsById(id)) {
            id += chars[random.nextInt(36)];
        }
        return id;
    }


    public static String generateShortUuid() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 4; i++) { //分成4组，每组8位
            String str = uuid.substring(i * 8, i * 8 + 8);
            long x = Long.parseLong(str, 16);
            sb.append(chars[(int) (x % 36)]); //对10个数字和26个字母取模
        }
        return sb.toString();

    }

}
