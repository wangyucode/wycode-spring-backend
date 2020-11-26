package cn.wycode.web.service

import cn.wycode.web.entity.admin.*
import com.aliyun.openservices.log.Client
import com.aliyun.openservices.log.request.GetLogsRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


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
            query.append("date_trunc('day', date_parse(time_local, '%d/%b/%Y:%T')) as time ")
        } else {
            query.append("date_trunc('hour', date_parse(time_local, '%d/%b/%Y:%T')) as time ")
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
                        "time" -> visitor.time = content.GetValue()
                    }
                }
                visitors.add(visitor)
            }
            return visitors
        }
        return emptyList()
    }

    fun getAppUse(day: Int = 30): Set<AppUse> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = StringBuilder("remote_addr | SELECT count(1) as use ,")
        query.append("regexp_extract(http_referer, 'http[s]?://[a-zA-Z0-9._-]+/') as referer, ")
        query.append("request_method as method, ")
        query.append("substr(split_part(request_uri,'?',1), 17, 4) as path ")
        query.append("WHERE substr(split_part(request_uri,'?',1), 1, 9) = '/web/api/' ")
        query.append("group by path,referer,method ")
        //query.append("order by use desc ")
        //query.append("limit 10")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val uses = HashSet<AppUse>(response.GetCount())
            val other = AppUse("其它")
            logs@ for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val appUse = AppUse()
                val contents = item.GetLogContents()
                var path: String? = null
                var referer: String? = null
                var method: String? = null
                var use = 0
                for (content in contents) {
                    when (content.GetKey()) {
                        "path" -> path = content.GetValue()
                        "referer" -> referer = content.GetValue()
                        "method" -> method = content.GetValue()
                        "use" -> use = content.GetValue().toInt()
                    }
                }

                when (path) {
                    "dota" -> {
                        if (referer != "https://servicewechat.com/") {
                            continue@logs
                        }
                        appUse.app = "Dota小助手"
                    }
                    "fish" -> {
                        if (referer != "https://servicewechat.com/") {
                            continue@logs
                        }
                        appUse.app = "养鱼小助手"
                    }
                    "comm" -> {
                        if (referer == "https://servicewechat.com/" || method == "POST") {
                            appUse.app = "评论SDK"
                        } else {
                            continue@logs
                        }
                    }
                    "clip" -> {
                        if (referer != "https://servicewechat.com/") {
                            continue@logs
                        }
                        appUse.app = "跨平台剪切板"
                    }
                    "albu" -> {
                        if (referer != "https://servicewechat.com/") {
                            continue@logs
                        }
                        appUse.app = "宝宝圈"
                    }
                    "deal" -> {
                        appUse.app = "发牌员"
                    }
                    else -> {
                        appUse.app = "其它"
                    }
                }
                appUse.use = use
                if (appUse.app == "其它") {
                    other.use += use
                } else {
                    if (uses.contains(appUse)) {
                        uses.elementAt(uses.indexOf(appUse)).use += use
                        continue@logs
                    }
                    uses.add(appUse)
                }
            }
            uses.add(other)
            return uses
        }
        return emptySet()
    }

    fun getErrorPath(day: Int = 30, code: Int = 500): List<ErrorPath> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = StringBuilder("status=$code | SELECT count(1) as count, ")
        query.append("split_part(request_uri,'?',1) as path, ")
        query.append("request_method as method ")
        query.append("GROUP BY method, path ")
        query.append("ORDER BY count DESC")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val errors = ArrayList<ErrorPath>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val error = ErrorPath()
                for (content in item.GetLogContents()) {
                    when (content.GetKey()) {
                        "count" -> error.count = content.GetValue().toInt()
                        "path" -> error.path = content.GetValue()
                        "method" -> error.method = content.GetValue()
                    }
                }
                errors.add(error)
            }
            return errors
        }
        return emptyList()
    }

    fun getBlogAccess(day: Int = 30): List<BlogAccess> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = "* | select request_uri as path ,COUNT(1) as count WHERE regexp_like(request_uri, '^/20.*\\.html\$') GROUP BY path"
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query)
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val blogAccesses = ArrayList<BlogAccess>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val blogAccess = BlogAccess()
                for (content in item.GetLogContents()) {
                    when (content.GetKey()) {
                        "count" -> blogAccess.count = content.GetValue().toInt()
                        "path" -> blogAccess.path = content.GetValue()
                    }
                }
                blogAccesses.add(blogAccess)
            }
            return blogAccesses
        }
        return emptyList()
    }

}