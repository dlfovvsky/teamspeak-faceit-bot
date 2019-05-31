package com.deelef.teamspeak.visitor

import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import com.deelef.teamspeak.tsclient.TeamspeakBot

import javax.transaction.Transactional

/**
 * // TODO add_java_doc_here
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 2018/10/18
 */
@Service
@Slf4j
class VisitorService {

    private final VisitorRepository visitorRepository
    private final TeamspeakBot teamspeakBot

    VisitorService(VisitorRepository visitorRepository, TeamspeakBot teamspeakBot) {
        this.visitorRepository = visitorRepository
        this.teamspeakBot = teamspeakBot
    }

    @Async
    @Transactional
    public void newVisit(String ip) {
        def client = null
        try {
            client = teamspeakBot.getClientByIp(ip)
            def visitor = new Visitor()
            visitor.setNickname(client.getNickname())
            visitor.setIp(ip)
            visitorRepository.save(visitor)
        } catch(Exception e) {
            log.error("No teamspeak client found for visitor ip " + ip)
        }

    }
}
