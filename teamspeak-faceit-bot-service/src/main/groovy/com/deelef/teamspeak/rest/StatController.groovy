package com.deelef.teamspeak.rest

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.deelef.teamspeak.domain.model.Stat
import com.deelef.teamspeak.domain.repository.StatRepository

/**
 * TODO add comment here!!!
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 17/06/2017
 */
@RestController
@RequestMapping("/stats")
@Slf4j
class StatController {

    @Autowired
    StatRepository statRepository

    @GetMapping
    Stat get() {
        def stat = statRepository.findOne(1L)
        log.info(stat.toString())
        return stat
    }
}
