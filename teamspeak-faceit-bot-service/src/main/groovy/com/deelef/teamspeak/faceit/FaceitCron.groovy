package com.deelef.teamspeak.faceit


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.deelef.teamspeak.domain.repository.UserRepository

/**
 * Created by adam on 25.04.2017.
 */
@Service
class FaceitCron {

    @Autowired
    FaceitService faceitService

    @Autowired
    UserRepository userRepository

    @Scheduled(fixedDelay = 60000l)
    @Transactional
    public void refreshFaceitClients() {
        List<com.deelef.teamspeak.domain.model.User> users = userRepository.findFaceitClients()
        users.stream().forEach({ u->
            faceitService.refreshFaceitClient(u)
        })
    }
}
