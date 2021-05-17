package cn.wycode.web.repository

import cn.wycode.web.entity.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoCommentAppRepository : MongoRepository<MongoCommentApp, String> {
    fun findByNameAndAccessKey(name: String, accessKey: String): MongoCommentApp?
}


@Repository
interface MongoCommentRepository : MongoRepository<MongoComment, Long> {
    fun findAllByAppAndTopicIdAndDeleted(app: String, topicId: String, deleted: Boolean = false): List<MongoComment>
}

@Repository
interface MongoThirdUserRepository : CrudRepository<MongoThirdUser, String>