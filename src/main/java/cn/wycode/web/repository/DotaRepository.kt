package cn.wycode.web.repository

import cn.wycode.web.entity.Dota2Hero
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HeroRepository : CrudRepository<Dota2Hero, Long>