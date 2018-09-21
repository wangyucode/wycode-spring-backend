package cn.wycode.web.service.impl;

import cn.wycode.web.entity.WXSession;
import cn.wycode.web.service.WXSessionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WXSessionServiceImpl implements WXSessionService {

    private Log log = LogFactory.getLog(WXSessionServiceImpl.class);

    private static final String sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String appid = "wx1d777be6c442da17";
    private static final String secret = "06eae637f43d35aef392b0942277522b";
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
                "?appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + jsCode +
                "&grant_type=" + grantType;
        return restTemplate.getForObject(url, WXSession.class);
    }

    @Override
    public WXSession getWXSessionForClipboard(String jsCode) {
        String url = sessionUrl +
                "?appid=wx1977172112eb7b61" +
                "&secret=58c20f669effe862b700dc6510bf092c"+
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
