package cn.wycode.web.controller;

import cn.wycode.web.entity.Clipboard;
import cn.wycode.web.entity.JsonResult;
import cn.wycode.web.entity.WXSession;
import cn.wycode.web.repository.ClipboardRepository;
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

    private final ClipboardRepository clipboardRepository;
    private final WXSessionService sessionService;
    private final Random random = new Random();

    @Autowired
    public ClipboardController(ClipboardRepository clipboardRepository, WXSessionService sessionService) {
        this.clipboardRepository = clipboardRepository;
        this.sessionService = sessionService;
    }

    @ApiOperation(value = "通过WXKEY查询剪切板")
    @RequestMapping(method = RequestMethod.GET, path = "/queryByKey")
    public JsonResult<Clipboard> queryByKey(@RequestParam String key) {
        Clipboard p = clipboardRepository.findByKey(key);
        if (p == null) {
            return JsonResult.Companion.error("invalid key");
        }
        return JsonResult.Companion.data(p);
    }

    @ApiOperation(value = "通过id查询剪切板")
    @RequestMapping(method = RequestMethod.GET, path = "/queryById")
    public JsonResult<Clipboard> queryById(@RequestParam String id) {
        Clipboard p = clipboardRepository.findById(id).orElse(null);
        return JsonResult.Companion.data(p);
    }

    @ApiOperation(value = "通过ID保存剪切板")
    @RequestMapping(method = RequestMethod.POST, path = "/saveById")
    public JsonResult<Clipboard> saveById(@RequestParam String id, @RequestParam String content, @RequestParam String tips) {
        Clipboard p = clipboardRepository.findById(id).orElse(null);
        if (p != null) {
            p.setContent(content);
            p.setTips(tips);
            p.setLastUpdate(new Date());
            p = clipboardRepository.save(p);
        }
        return JsonResult.Companion.data(p);
    }

    @ApiOperation(value = "意见反馈")
    @RequestMapping(method = RequestMethod.POST, path = "/suggest")
    @Deprecated // TODO use comment instead, can be remove after mini-app updated
    public JsonResult<?> suggest(@RequestParam String content, @RequestParam String contact) {
        return JsonResult.Companion.error("此接口已移除，请联系管理员");
    }

    @ApiOperation(value = "获取微信Session")
    @RequestMapping(path = "/getSession", method = RequestMethod.GET)
    public JsonResult<Clipboard> getSession(@RequestParam String jsCode) {
        WXSession session = sessionService.getWXSessionForClipboard(jsCode);
        if (session != null) {
            String accessKey = EncryptionUtil.getHash(session.getSession_key(), EncryptionUtil.MD5);
            Clipboard clipboard = clipboardRepository.findByOpenid(session.getOpenid());
            if (clipboard == null) {
                String id = generateId();
                clipboard = new Clipboard(id);
                clipboard.setContent("请输入或粘贴你想保存的内容，内容可在网页端：https://wycode.cn/clipboard.html 使用查询码查询。");
                clipboard.setOpenid(session.getOpenid());
                logger.info(clipboard.toString());
            }
            clipboard.setKey(accessKey); //一旦登录就刷新key

            return JsonResult.Companion.data(clipboardRepository.save(clipboard));
        } else {
            return JsonResult.Companion.error("未获取到session");
        }
    }

    private String generateId() {
        String id = generateShortUuid();
        while (clipboardRepository.existsById(id)) {
            id += Integer.toString(random.nextInt(36), 36);
        }
        return id;
    }


    public static String generateShortUuid() {
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 4; i++) { //分成4组，每组8位
            String str = uuid.substring(i * 8, i * 8 + 8);
            long x = Long.parseLong(str, 16);
            sb.append(Integer.toString((int) (x % 36), 36)); //对10个数字和26个字母取模
        }
        return sb.toString();

    }

}
