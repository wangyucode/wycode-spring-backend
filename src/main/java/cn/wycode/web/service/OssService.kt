package cn.wycode.web.service

import com.aliyun.oss.OSSClient
import org.springframework.stereotype.Service
import java.io.File

@Service
class OssService {

    private val endpoint = "http://oss-cn-zhangjiakou-internal.aliyuncs.com"
    //    private val endpoint = "http://oss-cn-zhangjiakou.aliyuncs.com"
    private val accessKeyId = "lBYHCbr7EKOIAw6d"
    private val accessKeySecret = "OFkmn94oodmdOIGIkBsNrFz2M6TsnR"
    private val bucketName = "wycode-baby-album"


    fun putFile(albumId: Long, file: File): String {
        val ossClient = OSSClient(endpoint, accessKeyId, accessKeySecret)
        val objectName = "$albumId/" + file.name
        ossClient.putObject(bucketName, objectName, file)
        ossClient.shutdown()
        return objectName
    }

    fun deleteFile(path: String): Long {
        val ossClient = OSSClient(endpoint, accessKeyId, accessKeySecret)
        val length = ossClient.getObjectMetadata(bucketName, path).contentLength
        ossClient.deleteObject(bucketName, path)
        return length
    }
}