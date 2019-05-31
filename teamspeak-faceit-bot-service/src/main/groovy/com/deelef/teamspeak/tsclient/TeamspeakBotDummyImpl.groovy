package com.deelef.teamspeak.tsclient

import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.api.ClientProperty
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Created by gontareka on 2017-04-01.
 */
@Profile("run-dummy-teamspeak-bot")
@Service
class TeamspeakBotDummyImpl implements TeamspeakBot{
    @Override
    void refreshServerName() {

    }

    @Override
    Client getClientByIp(String ip) {
        def map = new HashMap()
        map.put(ClientProperty.CLIENT_UNIQUE_IDENTIFIER.getName(), "DUMMY")
        map.put(ClientProperty.CLIENT_DATABASE_ID.getName(), "1")
        Client client = new Client(map)
        return client
    }

    @Override
    TS3Api getApi() {
        return null
    }

    @Override
    boolean isOnline(String uid) {
        return false
    }

    @Override
    DatabaseClientInfo getClientByDatabaseId(int clientDatabaseId) {
        return null
    }

    @Override
    Client getClient(String uid) {
        return null
    }

    @Override
    def getClients() {

    }

    @Override
    def getClientsGroupedByChannels() {
        return null
    }

    @Override
    List<Client> getClientChannelClients(Object uid) {
        return null
    }

    @Override
    int getServerGroupId(String serverGroup) {
        return 0
    }

    @Override
    void addClientToServerGroup(Client client, ServerGroupType serverGroupType) {

    }

    @Override
    void addClientToServerGroup(int clientDatabaseId, ServerGroupType serverGroupType) {

    }

    @Override
    void addClientToServerGroup(int clientDatabaseId, String serverGroup) {

    }

    @Override
    void removeClientFromServerGroup(int clientDatabaseId, String serverGroup) {

    }

    @Override
    boolean isClientInServerGroup(int clientDatabaseId, String serverGroup) {
        return false
    }

    @Override
    boolean isClientInServerGroupByClient(Client client, String serverGroup) {
        return false
    }

    @Override
    void sendOfflineMessage(String clientUId, String subject, String message) {

    }

    @Override
    void sendPrivateMessage(Integer clientId, String message) {

    }
}
