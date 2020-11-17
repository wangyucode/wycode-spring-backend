package cn.wycode.web.service.impl;

import cn.wycode.web.entity.WXSession;
import cn.wycode.web.service.WXSessionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WXSessionServiceImpl implements WXSessionService {

    private Log log = LogFactory.getLog(WXSessionServiceImpl.class);

    private static final String sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
    @Value("${wycode.fish.wx-app-id}")
    String fishAppId;
    @Value("${wycode.fish.wx-secret}")
    String fishSecret;
    @Value("${wycode.clipboard.wx-app-id}")
    String clipboardAppId;
    @Value("${wycode.clipboard.wx-secret}")
    String clipboardSecret;
    @Value("${wycode.album.wx-app-id}")
    String albumAppId;
    @Value("${wycode.album.wx-secret}")
    String albumSecret;
    private static final String grantType = "authorization_code";

    private final RestTemplate restTemplate;

    @Autowired
    public WXSessionServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        setResponseType();
    }

    @Override
    public WXSession getWXSession(String jsCode) {
        String url = sessionUrl +
                "?appid=" + fishAppId +
                "&secret=" + fishSecret +
                "&js_code=" + jsCode +
                "&grant_type=" + grantType;
        return restTemplate.getForObject(url, WXSession.class);
    }

    @Override
    public WXSession getWXSessionForClipboard(String jsCode) {
        String url = sessionUrl +
                "?appid=" + clipboardAppId +
                "&secret=" + clipboardSecret +
                "&js_code=" + jsCode +
                "&grant_type=" + grantType;
        return restTemplate.getForObject(url, WXSession.class);
    }

    @Override
    public WXSession getWXSessionForAlbum(String jsCode) {
        String url = sessionUrl +
                "?appid=" + albumAppId +
                "&secret=" + albumSecret +
                "&js_code=" + jsCode +
                "&grant_type=" + grantType;
        return restTemplate.getForObject(url, WXSession.class);
    }

    private void setResponseType() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
    }
}
