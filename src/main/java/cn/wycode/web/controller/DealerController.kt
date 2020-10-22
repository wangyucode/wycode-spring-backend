package cn.wycode.web.controller

import cn.wycode.web.entity.CommonMessage
import cn.wycode.web.entity.UserWord
import cn.wycode.web.entity.Users
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
            room.status != 0 -> room.status
            room.lastUserTime > userUpdateTime -> 1
            room.lastRoleTime > roleUpdateTime -> 2
            else -> 0
        }
    }

    @GetMapping("/users")
    fun users(@RequestParam id: String): Users? {
        val room = dealerService.rooms[id]
        return if (room != null) {
            Users(room.users, room.lastUserTime)
        } else {
            null
        }
    }

    // TODO 通过type来做狼人杀，谁是卧底需要分配词语，狼人杀不需要。
    @GetMapping("/start")
    fun start(@RequestParam id: String, @RequestParam type: Int, @RequestParam setting: String) {
        dealerService.assignRoles(id, setting)
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