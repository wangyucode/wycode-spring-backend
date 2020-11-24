package cn.wycode.web.repository

import cn.wycode.web.entity.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Deprecated("delete after migrate to mongodb")
@Repository
interface CommentAppRepository : CrudRepository<CommentApp, String> {
    fun findByNameAndAccessKey(name: String, accessKey: String): CommentApp?
}

@Repository
interface MongoCommentAppRepository : MongoRepository<MongoCommentApp, String> {
    fun findByNameAndAccessKey(name: String, accessKey: String): MongoCommentApp?
}

@Deprecated("delete after migrate to mongodb")
@Repository
interface CommentRepository : PagingAndSortingRepository<Comment, Long> {
    fun findAllByApp_NameAndTopicIdAndDeleted(appName: String, topicId: String, deleted: Boolean = false): List<Comment>
}

@Repository
interface MongoCommentRepository : MongoRepository<MongoComment, Long> {
    fun findAllByAppAndTopicIdAndDeleted(app: String, topicId: String, deleted: Boolean = false): List<MongoComment>
}

@Deprecated("delete after migrate to mongodb")
@Repository
interface ThirdUserRepository : CrudRepository<ThirdUser, String>

@Repository
interface MongoThirdUserRepository : CrudRepository<MongoThirdUser, String>