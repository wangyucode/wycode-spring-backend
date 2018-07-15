package cn.wycode.web.repository

import cn.wycode.web.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Deprecated("已废弃，9月30日删除，用WXClipboard替代")
@Repository
interface ClipboardRepository : CrudRepository<Clipboard, Long> {
    fun findTopByOrderByIdDesc(): Clipboard?
}

@Repository
interface ClipboardSuggestRepository : CrudRepository<ClipboardSuggest, Long>

@Repository
interface FishAnswerRepository : PagingAndSortingRepository<FishQuestionAnswer, Long> {
    fun findAllByQuestion_Id(questionId: Long, orders: Sort): List<FishQuestionAnswer>
    fun findByUser_Key(accessKey: String,orders: Sort):List<FishQuestionAnswer>
}

@Repository
interface FishBaikeRepository : CrudRepository<FishBaike, Long> {
    fun findByTypeOrderByReadCountDesc(type: String): List<FishBaike>
}

@Repository
interface  FishHandBookRepository: CrudRepository<FishHandBook, Long> {
    fun findByTypeOrderByCollectCountDesc(type: String): List<FishHandBook>
}

@Repository
interface FishQuestionRepository : PagingAndSortingRepository<FishQuestion, Long> {
    fun findByOrderByUpdateTimeDesc(page: Pageable): Page<FishQuestion>
    fun findByUser_Key(accessKey: String,page: Pageable):Page<FishQuestion>
}

@Repository
interface FishSuggestRepository : CrudRepository<FishSuggest, Long>

@Repository
interface FishUserRepository : CrudRepository<FishUser, Long> {
    fun findByOpenId(openId: String): FishUser?
    fun findByKey(accessKey: String): FishUser?
}

@Repository
interface WXClipboardRepository : CrudRepository<WXClipboard, String> {
    fun findByKey(key: String): WXClipboard?
    fun findByOpenid(openid: String): WXClipboard?
}