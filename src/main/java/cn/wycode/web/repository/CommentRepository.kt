package cn.wycode.web.repository

import cn.wycode.web.entity.Comment
import cn.wycode.web.entity.CommentApp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentAppRepository : CrudRepository<CommentApp, String> {
    fun findByNameAndAccessKey(name: String, accessKey: String): CommentApp?
}

@Repository
interface CommentRepository : PagingAndSortingRepository<Comment, Long> {
    fun findAllByApp_NameAndTopicId(appName: String, topicId: String): List<Comment>
}