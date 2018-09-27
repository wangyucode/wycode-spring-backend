package cn.wycode.web.repository

import cn.wycode.web.entity.Dota2Hero
import cn.wycode.web.entity.DotaNews
import cn.wycode.web.entity.HeroDetail
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface HeroRepository : CrudRepository<Dota2Hero, String>

@Repository
interface NewsRepository : PagingAndSortingRepository<DotaNews, Long> {
    fun findByTitle(title: String): DotaNews?
}

@Repository
interface HeroDetailRepository : CrudRepository<HeroDetail, String>