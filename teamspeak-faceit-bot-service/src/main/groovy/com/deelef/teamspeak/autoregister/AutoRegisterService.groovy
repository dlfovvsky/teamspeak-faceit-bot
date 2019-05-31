package com.deelef.teamspeak.autoregister


import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

/**
 * TODO add comment here!!!
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 19/07/2017
 */

@Service
@Slf4j
class AutoRegisterService implements InitializingBean {

    @Autowired
    com.deelef.teamspeak.domain.repository.UserRepository userRepository

    @Autowired
    com.deelef.teamspeak.tsclient.TeamspeakBot teamspeakBot

    @Autowired
    com.deelef.teamspeak.faceit.FaceitService faceitService

    @Value("\${faceit.api-key}")
    String apiKey

    private final com.deelef.teamspeak.register.RegisterService registerService;

    AutoRegisterService(com.deelef.teamspeak.register.RegisterService registerService) {
        this.registerService = registerService
    }

    @Scheduled(initialDelay = 40000l, fixedDelay = 1200000l)
    @Transactional
    public void autoRegister() {
        try {
            def players = userRepository.findPlayingFaceitClients()
            players.forEach({ player ->
                log.info("Trying for.." + player.faceitClient.nickname)
                def online = teamspeakBot.isOnline(player.getTeamspeakClient().getUid())
                if (online) {
                    log.info("Ts online: " + player.faceitClient.nickname)
                    List<Client> channelClients = teamspeakBot.getClientChannelClients(player.getTeamspeakClient().getUid())
                    Optional<Client> client = channelClients.stream().filter({ c -> (!teamspeakBot.isClientInServerGroupByClient(c, com.deelef.teamspeak.tsclient.ServerGroupType.FACEIT.toString())) }).findAny()
                    if (client.isPresent()) {
                        Map faceitTeamPlayers = faceitService.getTeamPlayers(player.faceitClient.guid)
                        channelClients.forEach({ c ->
                            log.info("Trying for client in channel: " + c.getNickname())
                            faceitTeamPlayers.forEach({ nickname, p ->
                                log.info("Trying to comparae with: " + p)
                                if (similar(c.getNickname(), nickname.toString()) || similar(c.getNickname(), p.csgo_name as String)) {
                                    def playerDetails = com.deelef.teamspeak.faceit.FaceitApi.getPlayerDetails(new RestTemplate(), apiKey, p.id as String)
                                    log.info("Checking if that player is friend.." + nickname)
                                    List<String> friends = playerDetails.friends_ids as List<String>
                                    if (friends.contains(player.faceitClient.guid)) {
                                        log.info("Adding automatically client.." + nickname)
                                        registerService.register(playerDetails.steam_id_64 as String, c, true)
                                    }
                                } else {
                                    log.info("Skipping adding automatically client..")
                                }
                            })
                        })
                    }
                }
            })
        } catch (Exception e) {
            log.error("Exception occurred while autoregistering players.", e)
        }
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public boolean similar(String s1, String s2) {
        if(s1 == null || s2 == null) {
            return false;
        }
        def similiarity = StringSimilarity.similarity(s1, s2)
        println similiarity
        if(similiarity > 0.8D) {
            return true
        } else {
            return false;
        }
    }


    void afterPropertiesSet() {
    }
}
