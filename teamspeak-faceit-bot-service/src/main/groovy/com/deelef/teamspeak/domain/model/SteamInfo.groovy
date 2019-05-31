package com.deelef.teamspeak.domain.model
/**
 * Created by gontareka on 2017-04-01.
 */
class SteamInfo {
    public static enum PersonaState{
        OFFLINE(0), ONLINE(1), BUSY(2), AWAY(3), SNOOZE(4), LOOKING_TO_TRADE(5), LOOKING_TO_PLAY(6)
        private final int steamNr;

        PersonaState(int steamNr) {
            this.steamNr = steamNr
        }

        static PersonaState from(int steamNr){
            return Arrays.stream(PersonaState.values()).filter({p -> p.steamNr == steamNr}).findFirst().get();
        }

        int getSteamNr() {
            return steamNr
        }
    }

    PersonaState personaState

    Boolean privateProfile;

    Date lastRefresh

    Boolean ingame;

    String gameextrainfo

    SteamInfo() {
    }

    SteamInfo(com.deelef.teamspeak.steam.integration.SteamPlayerProfile steamPlayerProfile) {
        this.personaState = PersonaState.from(steamPlayerProfile.personaState)
        this.privateProfile = steamPlayerProfile.communityVisibilityState==1
        this.lastRefresh = new Date()
        if(steamPlayerProfile.gameextrainfo!=null){
            this.ingame = true;
            this.gameextrainfo = steamPlayerProfile.gameextrainfo
        }else{
            ingame = false
            this.gameextrainfo = null
        }
    }
}
