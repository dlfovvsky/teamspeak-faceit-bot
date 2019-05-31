package com.deelef.teamspeak.tsclient

import groovy.util.logging.Slf4j
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * // TODO add_java_doc_here
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 2018/10/18
 */
@RestController
@RequestMapping("/teamspeak")
@Slf4j
class TeamspeakBotController {
    private final TeamspeakBot teamspeakBot;

    TeamspeakBotController(TeamspeakBot teamspeakBot) {
        this.teamspeakBot = teamspeakBot
    }

    @GetMapping
    public def list() {
        return teamspeakBot.getApi().getChannels()
    }

}
