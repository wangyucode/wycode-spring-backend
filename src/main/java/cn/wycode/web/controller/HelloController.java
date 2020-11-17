package cn.wycode.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 第一个Controller
 * Created by wayne on 2017/10/13.
 */
@Api(value = "Test", description = "for test",tags = "Test")
@RestController
@RequestMapping("/api/public")
public class HelloController {

    @ApiOperation(value = "generate uuid", produces = "application/json")
    @RequestMapping("/uuid")
    public String uuid(@ApiParam(value = "has hyphen(-)", defaultValue = "false")
                       @RequestParam(name = "has hyphen(-)", defaultValue = "false")
                               boolean hasHyphen,
                       @ApiParam(value = "upperCase", defaultValue = "false")
                       @RequestParam(name = "upperCase", defaultValue = "false")
                               boolean upperCase) {

        String uuid = UUID.randomUUID().toString();
        if (!hasHyphen) uuid = uuid.replaceAll("-", "");
        if (upperCase) uuid = uuid.toUpperCase();
        return uuid;
    }
}
