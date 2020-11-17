package cn.wycode.web.config

import cn.wycode.web.DEV
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class WyErrorAttributes : DefaultErrorAttributes(DEV) {
    override fun getErrorAttributes(webRequest: WebRequest?, includeStackTrace: Boolean): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace)
        errorAttributes["success"] = false
        errorAttributes["error"] = errorAttributes["message"]
        errorAttributes["data"] = errorAttributes["exception"]
        return errorAttributes
    }
}