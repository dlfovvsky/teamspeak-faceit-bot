package com.deelef.teamspeak.rest

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.deelef.teamspeak.domain.model.Stat
import com.deelef.teamspeak.domain.model.User
import com.deelef.teamspeak.domain.repository.StatRepository
import com.deelef.teamspeak.domain.repository.UserRepository
import com.deelef.teamspeak.faceit.FaceitService
import com.deelef.teamspeak.tsclient.TeamspeakBot
import com.deelef.teamspeak.visitor.VisitorService

import javax.servlet.http.HttpServletRequest

/**
 * Created by adam on 21.03.2017.
 */
@RestController
@RequestMapping("/users")
@Slf4j
class UserController {

    @Autowired
    UserRepository userRepository

    @Autowired
    StatRepository statRepository

    @Autowired
    TeamspeakBot teamspeakBot

    @Autowired
    FaceitService faceitService

    @Autowired
    VisitorService visitorService

    @GetMapping
    public List<User> list(HttpServletRequest httpServletRequest) {

        Stat stat = statRepository.findOne(1L)
        if(!stat) {
            stat = new Stat(id: 1L, hit: 0)
            statRepository.save(stat)
        }
        stat.hit++
        statRepository.save(stat)
        List<User> users = userRepository.findAll(new Sort(Sort.Direction.DESC, "faceitClient.elo"));
        users.parallelStream().forEach({user->
            user.teamspeakClient.online = teamspeakBot.isOnline(user.teamspeakClient.uid)
        })

        try {
            String visitorIp = httpServletRequest.getRemoteAddr();
            visitorService.newVisit(visitorIp)
        } catch (Exception e) {
            log.error("Error while adding visit")
        }
        return users
    }

    @GetMapping("/{faceitNickname}/match")
    public def getPlayerMatch(@PathVariable String faceitNickname) {
        return faceitService.getOngoingMatch(faceitNickname)
    }
}
