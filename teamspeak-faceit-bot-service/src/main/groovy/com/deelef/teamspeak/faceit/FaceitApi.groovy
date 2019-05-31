package com.deelef.teamspeak.faceit

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate;

/**
 * Created by gontareka on 2017-03-11.
 */
public class FaceitApi {
    static def searchURL = "https://api.faceit.com/search/v1?game=csgo&query="

    static def getPlayerURL = "https://api.faceit.com/core/v1/nicknames/"

    static def getPlayerDetailsURL = "https://open.faceit.com/data/v4/players/"

    static def getMatchURL = "https://api.faceit.com/api/matches/"

    static def getMatchByPlayerURL = "https://api.faceit.com/match/v1/matches/groupByState?userId="

    static def getStatsURL = "https://api.faceit.com/stats/v1/stats/users/%s/games/csgo"
    static def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)

    public static def getPlayerInfo(RestTemplate restTemplate, String nickname){
        def response = restTemplate.getForEntity(getPlayerURL + nickname, String.class).getBody()
        def obj = jsonSlurper.parseText(response)
        return obj
    }

    public static def getPlayerDetails(RestTemplate restTemplate, String apiKey, String guid){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        def response = restTemplate.exchange(getPlayerDetailsURL + guid, HttpMethod.GET, request, String.class).getBody();
        def obj = jsonSlurper.parseText(response as String)
        return obj
    }

    public static String search(RestTemplate restTemplate, String param){
        return restTemplate.getForEntity(searchURL + param, String.class).getBody()
    }

    public static String getMatchInfo(RestTemplate restTemplate, String matchId){
        return restTemplate.getForEntity(getMatchURL + matchId, String.class).getBody()
    }

    public static String getMatchByPlayer(RestTemplate restTemplate, String userGUID){
        return restTemplate.getForEntity(getMatchByPlayerURL + userGUID, String.class).getBody()
    }

    public static String getStats(RestTemplate restTemplate, String guid){
        return restTemplate.getForEntity(String.format(getStatsURL, guid), String.class).getBody()
    }

}
