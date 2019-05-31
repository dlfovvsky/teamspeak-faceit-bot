package com.deelef.teamspeak.tsclient

import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo

/**
 * Created by gontareka on 2017-04-01.
 */
interface TeamspeakBot {
    public void refreshServerName();

    Client getClientByIp(String ip);

    TS3Api getApi()

    boolean isOnline(String uid);

    DatabaseClientInfo getClientByDatabaseId(int clientDatabaseId);

    Client getClient(String uid)

    def getClients();

    def getClientsGroupedByChannels()

    List<Client> getClientChannelClients(uid)

    int getServerGroupId(String serverGroup);

    void addClientToServerGroup(Client client, ServerGroupType serverGroupType);

    void addClientToServerGroup(int clientDatabaseId, ServerGroupType serverGroupType);

    void addClientToServerGroup(int clientDatabaseId, String serverGroup);

    void removeClientFromServerGroup(int clientDatabaseId, String serverGroup);

    boolean isClientInServerGroup(int clientDatabaseId, String serverGroup);

    boolean isClientInServerGroupByClient(Client client, String serverGroup);

    void sendOfflineMessage(String clientUId, String subject, String message)

    void sendPrivateMessage(Integer clientId, String message)}
