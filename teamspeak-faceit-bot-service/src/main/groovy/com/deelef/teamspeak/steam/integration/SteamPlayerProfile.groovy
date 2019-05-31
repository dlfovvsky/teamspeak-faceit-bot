package com.deelef.teamspeak.steam.integration

import com.google.gson.annotations.SerializedName

/**
 * Created by gontareka on 2017-04-01.
 */
class SteamPlayerProfile extends com.ibasco.agql.protocols.valve.steam.webapi.pojos.SteamPlayerProfile{
    @SerializedName("gameid")
    private String gameid;

    @SerializedName("gameextrainfo")

    private String gameextrainfo;

    String getGameid() {
        return gameid
    }

    void setGameid(String gameid) {
        this.gameid = gameid
    }

    String getGameextrainfo() {
        return gameextrainfo
    }

    void setGameextrainfo(String gameextrainfo) {
        this.gameextrainfo = gameextrainfo
    }
}
