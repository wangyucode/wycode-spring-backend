package cn.wycode.web.controller;

import cn.wycode.web.entity.Hello;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第一个Controller
 * Created by wayne on 2017/10/13.
 */
@Api(value = "Hello", description = "第一个api", tags = "Hello")
@RestController
@RequestMapping("/api/public")
public class HelloController {

    @ApiOperation(value = "Say Hello", produces = "application/json ")
    @RequestMapping("/hello")
    public Hello hello(@ApiParam(value = "消息", defaultValue = "Hello World!") @RequestParam(name = "message", defaultValue = "Hello World!") String message) {
        return new Hello("message is : " + message);
    }
}
