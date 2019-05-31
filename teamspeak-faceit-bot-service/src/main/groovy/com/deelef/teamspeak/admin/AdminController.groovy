package com.deelef.teamspeak.admin

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.deelef.teamspeak.steam.SteamService

/**
 * TODO add comment here!!!
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 19/06/2017
 */
@RestController
@RequestMapping("admin")
@Slf4j
class AdminController {

    @Autowired
    SteamService steamService

    @GetMapping("/register")
    public void register(@RequestParam String steam, @RequestParam String ip){
        log.info("Register: " + steam + " ip:"+ ip)
        steamService.onAuth(ip, steam)
    }
}
