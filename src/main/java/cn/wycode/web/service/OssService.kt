package cn.wycode.web.service

import com.aliyun.oss.OSSClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class OssService(
        @Value("\${wycode.ali.access-key-id}")
        val aliAccessKeyId: String,
        @Value("\${wycode.ali.access-key-secret}")
        val aliAccessKeySecret: String
) {

    private val endpoint = "http://oss-cn-zhangjiakou-internal.aliyuncs.com"
    //    private val endpoint = "http://oss-cn-zhangjiakou.aliyuncs.com"
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun putFile(bucketName: String, path: String, file: File): String? {
        var objectName: String? = null
        try {
            val ossClient = OSSClient(endpoint, aliAccessKeyId, aliAccessKeySecret)
            objectName = "$path/" + file.name
            ossClient.putObject(bucketName, objectName, file)
            ossClient.shutdown()
        } catch (e: Exception) {
            logger.error("oss put file error", e)
        }
        return objectName
    }

    fun deleteFile(bucketName: String, path: String): Long {
        var length = 0L
        try {
            val ossClient = OSSClient(endpoint, aliAccessKeyId, aliAccessKeySecret)
            length = ossClient.getObjectMetadata(bucketName, path).contentLength
            ossClient.deleteObject(bucketName, path)
        } catch (e: Exception) {
            logger.error("oss delete file error", e)
        }
        return length
    }


}