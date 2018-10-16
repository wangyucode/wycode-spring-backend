package cn.wycode.web.service

import cn.wycode.web.entity.FishUser
import cn.wycode.web.entity.ResultItem
import com.alibaba.fastjson.JSON
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@EnableAsync
@Service
class FishClassifyProcessService {

    private val successPath = Paths.get("/var/fishclassify/success/")
    private val failPath = Paths.get("/var/fishclassify/fail/")

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        try {
            Files.createDirectories(successPath)
            Files.createDirectories(failPath)
        } catch (e: IOException) {
            throw RuntimeException("Could not initialize storage", e)
        }
    }

    @Async
    fun moveAndSaveResult(tempFile: Path, user: FishUser, resultItem: ResultItem?, resultString: String) {
        val jsonObject = JSONObject(resultString)
                .put("user", JSON.toJSONString(user))
        if (resultItem == null) {
            Files.move(tempFile, failPath.resolve(tempFile.fileName), StandardCopyOption.REPLACE_EXISTING)
            Files.copy(jsonObject.toString().byteInputStream(), failPath.resolve("${tempFile.toFile().nameWithoutExtension}.json"), StandardCopyOption.REPLACE_EXISTING)
            logger.info("save fail result ${tempFile.fileName}")
        } else {
            val tf = tempFile.toFile()
            val file = successPath.resolve(tf.nameWithoutExtension + "_" + resultItem.name + "." + tf.extension)
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING)
            Files.copy(jsonObject.toString().byteInputStream(), successPath.resolve("${file.toFile().nameWithoutExtension}.json"), StandardCopyOption.REPLACE_EXISTING)
            logger.info("save fail result ${file.fileName}")
        }
    }
}