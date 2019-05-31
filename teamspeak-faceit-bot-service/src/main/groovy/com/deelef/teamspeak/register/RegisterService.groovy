package com.deelef.teamspeak.register


import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import com.ibasco.agql.protocols.valve.steam.webapi.SteamWebApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import com.deelef.teamspeak.faceit.FaceitService
import com.deelef.teamspeak.tsclient.ServerGroupType
import com.deelef.teamspeak.tsclient.TeamspeakBot

/**
 * // TODO add_java_doc_here
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 2018/10/18
 */
@Service
class RegisterService {

    @Autowired
    com.deelef.teamspeak.domain.repository.UserRepository clientRepository

    @Autowired
    TeamspeakBot teamspeakBot

    @Autowired
    FaceitService faceitService

    @Value("\${steam.token}")
    String token;

    @Value("\${teamspeak.www}")
    String teamspeakSite;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void register(String steam64Id, Client client, boolean autoRegister = false, boolean forceRegister = false) {

        Optional<com.deelef.teamspeak.domain.model.User> user = clientRepository.findOneBySteam64Id(steam64Id)
        if(user.isPresent()){
            if(client.getDatabaseId() != user.get().getTeamspeakClient().getDatabaseId()){
                com.deelef.teamspeak.domain.model.User now = user.get();
                now.teamspeakClient = new com.deelef.teamspeak.domain.model.TeamspeakClient()
                now.teamspeakClient.databaseId = client.getDatabaseId()
                now.teamspeakClient.uid = client.getUniqueIdentifier()
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.STEAM)
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.FACEIT)
                teamspeakBot.addClientToServerGroup(now.teamspeakClient.databaseId, ServerGroupType.FACEIT_LVL.withLevel(now.faceitClient.lvl))
                return
            }
            return
        }


        com.deelef.teamspeak.domain.model.User createdUser = new com.deelef.teamspeak.domain.model.User()
        createdUser.steam64Id = steam64Id
        createdUser.autoRegister = autoRegister
        createdUser.teamspeakClient = new com.deelef.teamspeak.domain.model.TeamspeakClient()
        createdUser.teamspeakClient.databaseId = client.getDatabaseId()
        createdUser.teamspeakClient.uid = client.getUniqueIdentifier()

        SteamWebApiClient steamWebApiClient = new SteamWebApiClient(token)
        com.deelef.teamspeak.steam.integration.SteamUser steamUser = new com.deelef.teamspeak.steam.integration.SteamUser(steamWebApiClient)
        def future = steamUser.getPlayerProfile(createdUser.steam64Id as Long)
        com.deelef.teamspeak.steam.integration.SteamPlayerProfile steamPlayerProfile = future.get()
        createdUser.steamInfo = new com.deelef.teamspeak.domain.model.SteamInfo(steamPlayerProfile)

        clientRepository.save(createdUser)

        String nickname = faceitService.getNickname(createdUser.steam64Id)
        if (nickname != null) {
            com.deelef.teamspeak.domain.model.FaceitClient fc = new com.deelef.teamspeak.domain.model.FaceitClient()
            createdUser.setFaceitClient(fc)
            fc.nickname = nickname
            com.deelef.teamspeak.faceit.FaceitInfo faceitInfo = faceitService.getFaceitInfo(fc.nickname)
            fc.guid = faceitInfo.guid
            fc.lvl = faceitInfo.lvl
            fc.elo = faceitInfo.elo
            createdUser.faceitClient.lastRefreshDate = new Date();
            teamspeakBot.addClientToServerGroup(createdUser.teamspeakClient.databaseId, ServerGroupType.FACEIT)
            teamspeakBot.addClientToServerGroup(createdUser.teamspeakClient.databaseId, ServerGroupType.FACEIT_LVL.withLevel(faceitInfo.lvl))

            StringBuilder message = new StringBuilder();
            message.append("\n")
            message.append("Zostałeś automatycznie zarejestrowany.")
            message.append("\n")
            message.append("Steam: " + link("http://steamcommunity.com/profiles/"  + steam64Id))
            message.append("\n")
            message.append("Faceit: " + link("https://www.faceit.com/en/players/" + fc.nickname))
            message.append("\n")
            message.append("Faceit lvl:" + faceitInfo.lvl + " elo:" + faceitInfo.elo)
            if(autoRegister) {
                message.append("\n")
                message.append("Jeżeli to nie Twoje konto napisz do admina bądz zarejestruj (zaloguj) się przez steam na: " + link(teamspeakSite) +" .(funkcja nadpisania aktualnie niedostępna)");
            }
            message.append("\n")
            teamspeakBot.sendPrivateMessage(client.getId(), message.toString())

        }
        teamspeakBot.addClientToServerGroup(createdUser.teamspeakClient.databaseId, ServerGroupType.STEAM)
    }

    String link(String link) {
        return String.format("[URL=%s]%s[/URL]", link, link)
    }
}
