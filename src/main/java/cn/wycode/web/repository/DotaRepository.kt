package cn.wycode.web.repository

import cn.wycode.web.entity.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("delete after migrate")
@Repository
interface HeroRepository : CrudRepository<Dota2Hero, String>

@Deprecated("delete after migrate")
@Repository
interface VersionRepository : CrudRepository<DotaVersion, Int>

@Deprecated("delete after migrate")
@Repository
interface HeroDetailRepository : CrudRepository<HeroDetail, String>

@Deprecated("delete after migrate")
@Repository
interface AbilityRepository : CrudRepository<HeroAbility, String> {
    fun deleteAllByHeroName(name: String)
}

@Deprecated("delete after migrate")
@Repository
interface DotaItemRepository : CrudRepository<DotaItem, String> {
    @Query("select new DotaItem(i.key,i.name,i.img,i.cname,i.type,i.cost) from DotaItem i")
    fun findItemList(): List<DotaItem>

    @Query("DELETE FROM DOTA_ITEM_COMPONENTS WHERE components = ? ", nativeQuery = true)
    fun deleteComponents(key: String)
}

@Repository
interface MongoHeroRepository : CrudRepository<MongoDota2Hero, String>

@Repository
interface MongoVersionRepository : CrudRepository<MongoDotaVersion, Int>

@Repository
interface MongoHeroDetailRepository : CrudRepository<MongoHeroDetail, String>

@Repository
interface MongoDotaItemRepository : CrudRepository<MongoDotaItem, String>