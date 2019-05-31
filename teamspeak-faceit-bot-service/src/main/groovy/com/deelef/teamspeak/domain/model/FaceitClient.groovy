package com.deelef.teamspeak.domain.model


import javax.persistence.Convert
import javax.persistence.Embeddable

/**
 * Created by adam on 16.03.2017.
 */
@Embeddable
class FaceitClient {
    String nickname
    String lvl
    Integer elo
    Date lastRefreshDate
    String match_id
    Boolean playing
    Boolean online

    @Convert(converter = com.deelef.teamspeak.config.StringListConverter.class)
    List<String> lastGames
    Date lastGame
    String guid

}
