package cn.wycode.web.service

import cn.wycode.web.entity.admin.AppUse
import cn.wycode.web.entity.admin.Visitor
import com.aliyun.openservices.log.Client
import com.aliyun.openservices.log.request.GetLogsRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList


@Service
class LogService(val logClient: Client) {

    private val project = "wycode-nginx"
    private val logstore = "nignx"

    fun getVisitors(day: Int = 30): List<Visitor> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = StringBuilder("remote_addr | select approx_distinct(remote_addr) as uv ,")
        query.append("count(1) as pv ,")
        if (day > 7) {
            query.append("date_format(date_trunc('day', date_parse(time_local, '%d/%b/%Y:%T')), '%c/%e/%k')  as time ")
        } else {
            query.append("date_format(date_trunc('hour', date_parse(time_local, '%d/%b/%Y:%T')), '%c/%e/%k')  as time ")
        }
        query.append("group by time ")
        query.append("order by time ")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val visitors = ArrayList<Visitor>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val visitor = Visitor()
                for (content in item.GetLogContents()) {
                    when (content.GetKey()) {
                        "pv" -> visitor.pv = content.GetValue().toInt()
                        "uv" -> visitor.uv = content.GetValue().toInt()
                        "time" -> {
                            val times = content.GetValue().split("/")
                            if (day > 7) {
                                visitor.time = "${times[0]}月${times[1]}日"
                            } else {
                                visitor.time = "${times[1]}日${times[2]}时"
                            }

                        }
                    }
                }
                visitors.add(visitor)
            }
            return visitors
        }
        return emptyList()
    }

    fun getAppUse(day: Int = 30): List<AppUse> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = StringBuilder("remote_addr | count(1) as use ,")
        query.append("substr(split_part(request_uri,'?',1), 17, 4) as path ")
        query.append("WHERE substr(split_part(request_uri,'?',1), 1, 9) = '/web/api/' ")
        query.append("group by path ")
        query.append("order by use desc ")
        query.append("limit 10")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val uses = ArrayList<AppUse>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val appUse = AppUse()
                for (content in item.GetLogContents()) {
                    when (content.GetKey()) {
                        "use" -> appUse.use = content.GetValue().toInt()
                        "path" -> {
                            when (content.GetValue()) {
                                "dota" -> appUse.app = "Dota小助手"
                                "fish" -> appUse.app = "养鱼小助手"
                                "comm" -> appUse.app = "评论SDK"
                                "clip" -> appUse.app = "跨平台剪切板"
                                "albu" -> appUse.app = "宝宝圈"
                                else -> {
                                    appUse.app = "其它"
                                }
                            }

                        }
                    }
                }
                uses.add(appUse)
            }
            return uses
        }
        return emptyList()
    }
}