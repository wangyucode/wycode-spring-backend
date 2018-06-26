package cn.wycode.web.service;


import cn.wycode.web.entity.WXSession;

public interface WXSessionService {

    WXSession getWXSession(String jsCode);
    WXSession getWXSessionForClipboard(String jsCode);

}
