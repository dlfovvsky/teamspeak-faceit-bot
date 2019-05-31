package com.deelef.teamspeak.faceit


import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import com.deelef.teamspeak.tsclient.TeamspeakService

import java.text.SimpleDateFormat

/**
 * Created by gontareka on 2017-03-11.
 */
@Service
@Slf4j
class FaceitService {

    final SimpleDateFormat parser=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
    final SimpleDateFormat pattern =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    com.deelef.teamspeak.domain.repository.UserRepository userRepository

    @Autowired
    TeamspeakService teamspeakService

    @Autowired
    RestTemplate restTemplate

    def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    String getFaceitLevel(String nickname) {
        def obj = jsonSlurper.parseText(FaceitApi.getPlayerInfo(restTemplate, nickname))
        return obj.payload.games.csgo.skill_level_label

    }


    FaceitInfo getFaceitInfo(String nickname) {
        def obj = FaceitApi.getPlayerInfo(restTemplate, nickname)
        FaceitInfo info = new FaceitInfo()
        info.guid = obj.payload.guid
        info.lvl = obj.payload.games.csgo.skill_level_label
        info.elo = obj.payload.games.csgo.faceit_elo as Integer
        info.online = obj.payload.last_quick_matches.csgo.online

        return info

    }


    def getMatchByPlayer(String guid) {
        def obj = jsonSlurper.parseText(FaceitApi.getMatchByPlayer(restTemplate, guid))
        return obj.payload

    }

    FaceitStats getStats(String guid) {
        def obj = jsonSlurper.parseText(FaceitApi.getStats(restTemplate, guid))
        def faceitStats = new FaceitStats()
        faceitStats.lastGames = (obj.lifetime.s0 as List).toList()
        return faceitStats
    }

    String getNickname(String steam64Id) {
        def obj = jsonSlurper.parseText(FaceitApi.search(restTemplate, steam64Id))
        List list = obj.payload.players.results as List
        if (list.size() != 1) throw new RuntimeException("Cannot find player" + list.toString())
        return list[0].nickname
    }


    public def getOngoingMatch(String faceitNickname) {
        FaceitInfo faceitInfo = getFaceitInfo(faceitNickname)
        if (faceitInfo.match_id) {
            def obj = jsonSlurper.parseText(FaceitApi.getMatchInfo(restTemplate, faceitInfo.match_id))
            return obj.payload
        } else {
            return null
        }
    }

    def getMatchWhenExist(currentMatch) {
        def match = null;
        if(currentMatch) {
            if (currentMatch.READY != null) {
                match = currentMatch.READY
            }
            if(currentMatch.ONGOING != null) {
                match = currentMatch.ONGOING
            } else if (currentMatch.VOTING != null) {
                match = currentMatch.VOTING
            }
        }


        return match
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refreshFaceitClient(com.deelef.teamspeak.domain.model.User u) {
        FaceitInfo faceitInfo = null
        try {
            faceitInfo = this.getFaceitInfo(u.faceitClient.nickname)
            u.faceitClient.guid = faceitInfo.guid
            u.faceitClient.elo = faceitInfo.elo
            String oldLevel = u.faceitClient.lvl
            u.faceitClient.lvl = faceitInfo.lvl
//            if (faceitInfo.playing != null) {
//                u.faceitClient.playing = faceitInfo.playing
//                u.faceitClient.match_id = faceitInfo.match_id
//                u.faceitClient.lastGame = faceitInfo.lastGame
//            } else {
//                u.faceitClient.playing = false;
//                u.faceitClient.match_id = null;
//            }
            def currentMatch = getMatchByPlayer(faceitInfo.guid)
            def match = getMatchWhenExist(currentMatch)
            if (match) {
                u.faceitClient.playing = true
                u.faceitClient.match_id = match.id[0]
                try {
                    u.faceitClient.lastGame = match.createdAt[0]
                } catch(Exception e) {
                    log.error("Error occured while setting last game date for player:" + u.getId() + " faceit:" + u.getFaceitClient().getNickname() + "date: " + currentMatch.ONGOING.createdAt, e)
                }
            } else {
                u.faceitClient.playing = false;
                u.faceitClient.match_id = null;
            }
            u.faceitClient.online = faceitInfo.online

            def stats = getStats(u.getFaceitClient().getGuid())
            u.faceitClient.lastGames = stats.lastGames
            if (!oldLevel.equals(faceitInfo.lvl)) {
                teamspeakService.faceitLevelChanged(u, oldLevel, u.faceitClient.lvl)
            }
            u.faceitClient.lastRefreshDate = new Date();
        } catch (RuntimeException re) {
            log.error("Error occurred while performing faceit scan for user, id:" + u.getId() + " faceit:" + u.getFaceitClient().getNickname(), re)
            log.error("Trying refetch nickname, maybe player changed nick");
            if (faceitInfo == null) {
                String nickname = getNickname(u.getSteam64Id())
                if (nickname != null) {
                    u.faceitClient.nickname = nickname
                }
            }
        }
    }

    Map getTeamPlayers(String faceitGUID) {
        def currentMatch = getMatchByPlayer(faceitGUID)
        def match = getMatchWhenExist(currentMatch)
        if (match) {
            def faction1 = match.teams.faction1.roster[0] as List
            def faction2 = match.teams.faction2.roster[0] as List
            List faction

            Optional<Object> present = faction1.stream().filter({ player -> player.id == faceitGUID }).findAny()
            if (present.isPresent()) {
                faction = faction1
            } else {
                faction = faction2
            }

            def result = [:]
            faction
                    .stream()
                    .filter({ player -> player.id != faceitGUID })
                    .forEach({ player ->
                result.put(player.nickname, player)
            })

            return result
        }
    }
}
