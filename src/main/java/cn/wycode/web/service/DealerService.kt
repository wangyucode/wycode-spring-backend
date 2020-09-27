package cn.wycode.web.service

import cn.wycode.web.entity.DealerUser
import cn.wycode.web.entity.Room
import cn.wycode.web.entity.UndercoverCard
import cn.wycode.web.entity.UserWord
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

const val MAX_ROOM_COUNT = 128
const val MAX_ROOM_ID = 9999
const val MAX_ROOM_PLAYER = 32
const val ROOM_EXPIRED_TIME = 30 * 60 * 1000L //30 minutes


@Service
class DealerService(restTemplateBuilder: RestTemplateBuilder) {
    private final val logger: Log = LogFactory.getLog(this.javaClass)
    val random = Random(System.currentTimeMillis())
    private val restTemplate = restTemplateBuilder.build()
    private final val roomIdPool = ArrayList<String>(MAX_ROOM_ID)
    val rooms = ConcurrentHashMap<String, Room>(MAX_ROOM_COUNT)

    init {
        for (i in 0..MAX_ROOM_ID) {
            roomIdPool.add(String.format("%04d", i))
        }
    }

    fun createRoom(): Room {
        checkRoom()
        val index = random.nextInt(roomIdPool.size)
        val roomId = roomIdPool[index]
        roomIdPool.removeAt(index)
        val room = Room(roomId)
        rooms[roomId] = room
        logger.info("room count -->${rooms.size}")
        return room
    }

    fun checkRoom() {
        for (room in rooms.values) {
            if (System.currentTimeMillis() - room.lastUserTime > ROOM_EXPIRED_TIME) {
                closeRoom(room.id)
            }
        }
    }

    fun joinRoom(room: Room): DealerUser {
        val user = DealerUser(room.users.size + 1)
        user.roomId = room.id
        room.users.add(user)
        room.lastUserTime = System.currentTimeMillis()
        return user
    }

    /**
     * roles: type-count,type-count
     * 'U': undercover
     * 'C': citizen
     */
    fun assignRoles(roomId: String, cardsSetting: String) {
        val room = rooms[roomId] ?: return
        val undercoverRoles = restTemplate.getForObject<Array<UndercoverCard>?>("https://wycode.cn/upload/undercover.json")
        if (undercoverRoles == null || undercoverRoles.isEmpty()) return
        val undercoverRole = undercoverRoles[random.nextInt(undercoverRoles.size)]
        logger.info(undercoverRoles.toString())
        val roles = ArrayList<String>()
        val cards = cardsSetting.split(',')
        for (card in cards) {
            val cardNumber = card.split('-')
            repeat(cardNumber[1].toInt()) {
                roles.add(cardNumber[0])
            }
        }

        for (user in room.users) {
            val index = random.nextInt(roles.size)
            user.role = roles[index]
            when (user.role) {
                "C" -> user.word = undercoverRole.C
                "U" -> user.word = undercoverRole.U
                "B" -> user.word = "白板"
            }
            roles.removeAt(index)
        }
        room.lastRoleTime = System.currentTimeMillis()
    }

    fun getUserWord(roomId: String, userId: Int): UserWord {
        val room = rooms[roomId]
        val userWord = UserWord()
        if (room == null) return userWord
        val notBlanks = room.users.filter { it.role != "B" }
        userWord.first = notBlanks[random.nextInt(notBlanks.size)].id
        userWord.lastRoleTime = room.lastRoleTime
        val user = room.users.find { it.id == userId }
        if (user != null) userWord.word = user.word
        return userWord
    }

    fun outUser(roomId: String, userId: Int) {
        val room = rooms[roomId]
        val user = room?.users?.find { it.id == userId }
        if (user != null) {
            user.status = -1
            room.lastUserTime = System.currentTimeMillis()
        }
    }

    fun closeRoom(roomId: String) {
        val room = rooms.remove(roomId)
        if (room != null) {
            room.users.clear()
            roomIdPool.add(roomId)
        }
    }
}