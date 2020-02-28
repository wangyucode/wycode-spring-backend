package cn.wycode.web.controller.chat

import cn.wycode.web.entity.ChatMessage
import cn.wycode.web.entity.CommonMessage
import cn.wycode.web.entity.InitData
import cn.wycode.web.service.ChatService
import cn.wycode.web.service.GEN_CODE_TIME_IN_MINUTES
import cn.wycode.web.service.REMOVE_MESSAGE_TIME_IN_MINUTES
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.*


@Controller
class ChatController(val chatService: ChatService) {

    @MessageMapping("/status")
    @SendToUser("/queue/status")
    fun status(accessor: SimpMessageHeaderAccessor): CommonMessage<*> {
        return if (accessor.user != null) {
            chatService.sendSystemMessage(200, accessor.user!!.name)
            CommonMessage.success(InitData(accessor.user!!.name.toInt(), chatService.users, chatService.messages, chatService.code, GEN_CODE_TIME_IN_MINUTES, REMOVE_MESSAGE_TIME_IN_MINUTES))
        } else {
            CommonMessage.fail(accessor.sessionAttributes!!["error"] as String)
        }
    }

    @MessageMapping("/all")
    fun all(message: String, user: Principal): CommonMessage<ChatMessage> {
        val msg = ChatMessage(user.name.toInt(), Date(), message, 0)
        chatService.messages.add(msg)
        return CommonMessage.success(msg)
    }

}