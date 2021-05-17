package cn.wycode.web.repository

import cn.wycode.web.entity.MongoDota2Hero
import cn.wycode.web.entity.MongoDotaItem
import cn.wycode.web.entity.MongoHeroDetail
import cn.wycode.web.entity.WyConfig
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoHeroRepository : CrudRepository<MongoDota2Hero, String>

@Repository
interface WyConfigRepository : CrudRepository<WyConfig, String>

@Repository
interface MongoHeroDetailRepository : CrudRepository<MongoHeroDetail, String>

@Repository
interface MongoDotaItemRepository : CrudRepository<MongoDotaItem, String>