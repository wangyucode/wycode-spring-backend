package cn.wycode.web.service

import cn.wycode.web.entity.admin.*
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
        val query = StringBuilder("remote_addr | SELECT count(1) as use ,")
        query.append("substr(split_part(request_uri,'?',1), 17, 4) as path ")
        query.append("WHERE substr(split_part(request_uri,'?',1), 1, 9) = '/web/api/' ")
        query.append("group by path ")
        query.append("order by use desc ")
        query.append("limit 10")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val uses = ArrayList<AppUse>(response.GetCount())
            val other = AppUse("其它")
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
                if (appUse.app == "其它") {
                    other.use += appUse.use
                } else {
                    uses.add(appUse)
                }
            }
            uses.add(other)
            return uses
        }
        return emptyList()
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

    fun getGeo(day: Int = 7): List<Geo> {
        val now = Date().time / 1000L
        val from = (now - day * 24 * 3600)
        val query = StringBuilder("remote_addr | SELECT count(1) as count, ")
        query.append("ip_to_geo(remote_addr) as geo ")
        query.append("group by geo ")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = logClient.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val geos = ArrayList<Geo>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val geo = Geo()
                for (content in item.GetLogContents()) {
                    when (content.GetKey()) {
                        "count" -> geo.count = content.GetValue().toInt()
                        "geo" -> {
                            val latlong = content.GetValue().split(',')
                            if (latlong.size == 2) {
                                geo.lat = latlong[0].toFloat()
                                geo.lng = latlong[1].toFloat()
                            }
                        }
                    }
                }
                geos.add(geo)
            }
            return geos
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