package com.deelef.teamspeak.tsclient


import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by gontareka on 2017-03-12.
 */
@Slf4j
@Service
class TeamspeakService {

    @Autowired
    TeamspeakBot teamspeakBot

    public void faceitLevelChanged(com.deelef.teamspeak.domain.model.User user, String oldLvl, String newLvl){
        int databaseId = user.teamspeakClient.databaseId
        if(!teamspeakBot.isClientInServerGroup(databaseId, ServerGroupType.FACEIT.toString())){
            teamspeakBot.addClientToServerGroup(databaseId, ServerGroupType.FACEIT)
        }
        if(!teamspeakBot.isClientInServerGroup(databaseId, ServerGroupType.FACEIT_LVL.withLevel(oldLvl))){
            log.error("Client should be in this server group, but wasn't")
        }else{
            teamspeakBot.removeClientFromServerGroup(databaseId, ServerGroupType.FACEIT_LVL.withLevel(oldLvl))
        }

        teamspeakBot.addClientToServerGroup(databaseId, ServerGroupType.FACEIT_LVL.withLevel(newLvl))
    }
}
