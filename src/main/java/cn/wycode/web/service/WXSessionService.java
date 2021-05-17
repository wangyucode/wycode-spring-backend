package cn.wycode.web.service;


import cn.wycode.web.entity.WXSession;

public interface WXSessionService {

    WXSession getWXSessionForClipboard(String jsCode);
    WXSession getWXSessionForAlbum(String jsCode);

}
