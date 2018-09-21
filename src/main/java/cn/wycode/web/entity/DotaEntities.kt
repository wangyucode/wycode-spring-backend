package cn.wycode.web.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Dota2Hero(
        @Id
        val name: String = "",
        @Column(length = 1023)
        val imageUrl: String = "",
        val type: String = ""
)