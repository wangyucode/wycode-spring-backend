package cn.wycode.web.service

import cn.wycode.web.ALI_ACCESS_KEY_ID
import cn.wycode.web.ALI_ACCESS_KEY_SECRET
import cn.wycode.web.entity.admin.Visitor
import com.aliyun.openservices.log.Client
import com.aliyun.openservices.log.request.GetLogsRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList


@Service
class LogService {

    //    private val endpoint = "http://cn-zhangjiakou-intranet.log.aliyuncs.com"
    private val endpoint = "http://cn-zhangjiakou.log.aliyuncs.com"
    private val project = "wycode-nginx"
    private val logstore = "nignx"

    fun getVisitors(day: Int = 30):List<Visitor> {
        val client = Client(endpoint, ALI_ACCESS_KEY_ID, ALI_ACCESS_KEY_SECRET)
        val now = Date().time / 1000L
        val from = (now - 30 * 24 * 3600)
        val query = StringBuilder("remote_addr | select approx_distinct(remote_addr) as uv ,")
        query.append("count(1) as pv ,")
        if (day > 7) {
            query.append("date_format(date_trunc('day', date_parse(time_local, '%d/%b/%Y:%T')), '%c/%e/%k')  as time\n")
        } else {
            query.append("date_format(date_trunc('hour', date_parse(time_local, '%d/%b/%Y:%T')), '%c/%e/%k')  as time\n")
        }
        query.append("group by time\n")
        query.append("order by time\n")
        query.append("limit 1000")
        val request = GetLogsRequest(project, logstore, from.toInt(), now.toInt(), "", query.toString())
        val response = client.GetLogs(request)
        if (response != null && response.IsCompleted()) {
            val visitors = ArrayList<Visitor>(response.GetCount())
            for (log in response.GetLogs()) {
                val item = log.GetLogItem()
                val visitor = Visitor()
                for (content in item.GetLogContents()) {
                    when(content.GetKey()){
                        "pv"-> visitor.pv = content.GetValue().toInt()
                        "uv"-> visitor.uv = content.GetValue().toInt()
                        "time"-> visitor.time = content.GetValue()
                    }
                }
                visitors.add(visitor)
            }
            return visitors
        }
        return emptyList()
    }
}