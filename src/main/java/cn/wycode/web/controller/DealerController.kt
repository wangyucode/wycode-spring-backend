package cn.wycode.web.controller

import cn.wycode.web.entity.CommonMessage
import cn.wycode.web.entity.Room
import cn.wycode.web.entity.UserWord
import cn.wycode.web.service.DealerService
import cn.wycode.web.service.MAX_ROOM_COUNT
import cn.wycode.web.service.MailService
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/public/dealer")
@Api(value = "Dealer", description = "卧底发牌员", tags = ["Dealer"])
class DealerController(private val dealerService: DealerService, private val mailService: MailService) {

    @GetMapping("/createRoom")
    fun createRoom(): CommonMessage<*> {
        return if (dealerService.rooms.size < MAX_ROOM_COUNT) {
            val room = dealerService.createRoom()
            val user = dealerService.joinRoom(room)
            CommonMessage.success(user)
        } else {
            mailService.sendSimpleMail("wangyu@wycode.cn",
                    "Dealer服务通知",
                    "房间数到达上限！-${MAX_ROOM_COUNT}"
            )
            CommonMessage.fail("服务器爆满！")
        }
    }

    @GetMapping("/joinRoom")
    fun joinRoom(@RequestParam id: String): CommonMessage<*> {
        val room = dealerService.rooms[id]
        return when {
            room == null -> {
                CommonMessage.fail("房间不存在！")
            }
            room.lastRoleTime > 0 -> {
                CommonMessage.fail("游戏已经开始！")
            }
            else -> {
                val user = dealerService.joinRoom(room)
                CommonMessage.success(user)
            }
        }
    }

    @GetMapping("/heartbeat")
    fun heartbeat(@RequestParam(name = "u") userUpdateTime: Long,
                  @RequestParam(name = "r") roleUpdateTime: Long,
                  @RequestParam(name = "i") roomId: String): Int {
        val room = dealerService.rooms[roomId]
        return when {
            room == null -> 0
            room.lastUserTime > userUpdateTime -> 1
            room.lastRoleTime > roleUpdateTime -> 2
            else -> 0
        }
    }

    @GetMapping("/users")
    fun users(@RequestParam id: String): Room? {
        return dealerService.rooms[id]
    }

    /**
     * roles: role-count,role-count
     * example: 1-1,2-1,3-5
     */
    @GetMapping("/start")
    fun start(@RequestParam id: String, @RequestParam roles: String) {
        dealerService.assignRoles(id, roles)
    }

    @GetMapping("/word")
    fun role(@RequestParam roomId: String, @RequestParam userId: Int): UserWord {
        return dealerService.getUserWord(roomId, userId)
    }

    @GetMapping("/out")
    fun out(@RequestParam roomId: String, @RequestParam userId: Int) {
        dealerService.outUser(roomId, userId)
    }

    @GetMapping("/close")
    fun close(@RequestParam roomId: String) {
        dealerService.closeRoom(roomId)
    }
}