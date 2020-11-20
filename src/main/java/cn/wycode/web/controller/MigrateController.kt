package cn.wycode.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/migrate")
class MigrateController {

    @GetMapping("/run")
    fun run(@RequestParam script: String){

    }
}