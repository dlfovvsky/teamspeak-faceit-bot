package com.deelef.teamspeak.steam.integration

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ibasco.agql.protocols.valve.steam.webapi.SteamWebApiClient
import com.ibasco.agql.protocols.valve.steam.webapi.interfaces.user.GetPlayerSummaries

import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture

/**
 * Created by gontareka on 2017-04-01.
 */
class SteamUser extends com.ibasco.agql.protocols.valve.steam.webapi.interfaces.SteamUser {
    SteamUser(SteamWebApiClient client) {
        super(client)
    }

    CompletableFuture<SteamPlayerProfile> getPlayerProfile(Long steamId) {
        return getPlayerProfiles(steamId).thenApply({steamPlayerProfiles ->
            if (steamPlayerProfiles != null && steamPlayerProfiles.size() == 1)
                return steamPlayerProfiles.get(0)
            return null
        })
    }

    CompletableFuture<List<SteamPlayerProfile>> getPlayerProfiles(Long... steamIds) {
        CompletableFuture<JsonObject> json = sendRequest(new GetPlayerSummaries(VERSION_2, steamIds));
        return json.thenApply({root ->
            JsonArray players = root.getAsJsonObject("response").getAsJsonArray("players");
            Type type = new TypeToken<List<SteamPlayerProfile>>() {
            }.getType();
            if (players != null) {
                return builder().fromJson(players, type);
            }
            return null
        });
    }
}
