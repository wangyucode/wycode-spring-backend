package cn.wycode.web.repository

import cn.wycode.web.entity.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface HeroRepository : CrudRepository<Dota2Hero, String> {
    @Deprecated("小程序更新删除A杖后去除")
    @Query("select new Dota2Hero(h.name,h.icon,h.imageUrl) from Dota2Hero h inner join DotaSpecialHero h1 on h.name = h1.name")
    fun findNoAzhangHeros(): List<Dota2Hero>
}


@Repository
interface VersionRepository : CrudRepository<DotaVersion, Int>

@Repository
interface HeroDetailRepository : CrudRepository<HeroDetail, String>

@Repository
interface AbilityRepository : CrudRepository<HeroAbility, String> {
    fun deleteAllByHeroName(name: String)
}


@Repository
interface DotaItemRepository : CrudRepository<DotaItem, String> {
    @Query("select new DotaItem(i.key,i.name,i.img,i.cname,i.type,i.cost) from DotaItem i")
    fun findItemList(): List<DotaItem>

    @Query("DELETE FROM DOTA_ITEM_COMPONENTS WHERE components = ? ", nativeQuery = true)
    fun deleteComponents(key: String)
}

@Deprecated("小程序更新删除A杖后去除")
@Repository
interface DotaAzhangEffectRepository : CrudRepository<DotaAzhangEffect, Long>