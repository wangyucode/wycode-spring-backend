package cn.wycode.web.service

import com.aliyun.oss.OSSClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class OssService {

    private val endpoint = "http://oss-cn-zhangjiakou-internal.aliyuncs.com"
    //    private val endpoint = "http://oss-cn-zhangjiakou.aliyuncs.com"
    private val accessKeyId = "lBYHCbr7EKOIAw6d"
    private val accessKeySecret = "OFkmn94oodmdOIGIkBsNrFz2M6TsnR"

    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        const val ALBUM_BUCKET_NAME = "wycode-baby-album"
        const val COMMENT_BUCKET_NAME = "wycode-comment"
    }

    fun putFile(bucketName: String, path: String, file: File): String? {
        var objectName: String? = null
        try {
            val ossClient = OSSClient(endpoint, accessKeyId, accessKeySecret)
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
            val ossClient = OSSClient(endpoint, accessKeyId, accessKeySecret)
            length = ossClient.getObjectMetadata(bucketName, path).contentLength
            ossClient.deleteObject(bucketName, path)
        } catch (e: Exception) {
            logger.error("oss delete file error", e)
        }
        return length
    }


}