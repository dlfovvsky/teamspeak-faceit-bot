package com.deelef.teamspeak.steam


import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import com.ibasco.agql.protocols.valve.steam.webapi.SteamWebApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.deelef.teamspeak.domain.model.SteamInfo
import com.deelef.teamspeak.domain.model.TeamspeakClient
import com.deelef.teamspeak.domain.model.User
import com.deelef.teamspeak.domain.repository.UserRepository
import com.deelef.teamspeak.faceit.FaceitService
import com.deelef.teamspeak.register.RegisterService
import com.deelef.teamspeak.tsclient.ServerGroupType
import com.deelef.teamspeak.tsclient.TeamspeakBot

import java.util.concurrent.TimeUnit

/**
 * Created by adam on 16.03.2017.
 */
@Service
class SteamService {
    @Autowired
    UserRepository clientRepository

    @Autowired
    TeamspeakBot teamspeakBot

    @Autowired
    FaceitService faceitService

    @Autowired
    UserRepository userRepository

    @Autowired
    RegisterService registerService

    @Value("\${steam.token}")
    String token;

    @Transactional
    String onAuth(String ip, String steam64Id){
//        ip = "62.21.2.26"
        Optional<User> user = clientRepository.findOneBySteam64Id(steam64Id)
        Client client = teamspeakBot.getClientByIp(ip)
        if(user.isPresent()){
            if(client.getDatabaseId() != user.get().getTeamspeakClient().getDatabaseId()){
                User now = user.get();
                now.teamspeakClient = new TeamspeakClient()
                now.teamspeakClient.databaseId = client.getDatabaseId()
                now.teamspeakClient.uid = client.getUniqueIdentifier()
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.STEAM)
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.FACEIT)
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.FACEIT_LVL.withLevel(now.faceitClient.lvl))
                return "U are restored"
            }
            return "U are already registered"
        }

        if(!client){
            return "U need to be connected to teamspeak server to register"
        }

        registerService.register(steam64Id, client)
        return "U've been registered"
    }



//    @Scheduled(fixedDelay = 180000l)
    @Transactional
    public void refreshUsers() {
        List<User> users = userRepository.findAll()
        SteamWebApiClient steamWebApiClient = new SteamWebApiClient(token)
        com.deelef.teamspeak.steam.integration.SteamUser steamUser = new com.deelef.teamspeak.steam.integration.SteamUser(steamWebApiClient)

        users.stream().forEach({User user ->
            def future =  steamUser.getPlayerProfile(user.steam64Id as Long)
            try{
                com.deelef.teamspeak.steam.integration.SteamPlayerProfile steamPlayerProfile = future.get(2000, TimeUnit.MILLISECONDS)
                user.steamInfo = new SteamInfo(steamPlayerProfile)
            }catch(Exception e){
                if(future){
                    e.printStackTrace()
                    future.cancel(true)
                }
            }

        })
    }
}
