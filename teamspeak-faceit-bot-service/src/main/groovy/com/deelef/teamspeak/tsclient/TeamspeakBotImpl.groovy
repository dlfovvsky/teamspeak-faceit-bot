package com.deelef.teamspeak.tsclient

import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty
import com.github.theholywaffle.teamspeak3.api.event.*
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.util.function.Function
import java.util.logging.Level
import java.util.stream.Collectors

/**
 * Created by gontareka on 2017-03-11.
 */
@Profile("run-teamspeak-bot")
@Service
@Slf4j
class TeamspeakBotImpl implements InitializingBean, TeamspeakBot {
    TS3Config config
    TS3Query query
    TS3Api api
    Map<String, ServerGroup> serverGroups = new HashMap<>();
    def teamspeakClients = []
    def teamspeakClientsRefreshed


    @Value("\${teamspeak.runbot}")
    boolean runBot;

    @Value("\${teamspeak.host}")
    def host = "";

    @Value("\${teamspeak.login.username}")
    def loginUsername = "serveradmin";

    @Value("\${teamspeak.name}")
    def teamspeakName = "CHANGE IT";

    @Value("\${teamspeak.login.password}")
    def loginPassword = "changeit";

    @Value("\${teamspeak.nickname}")
    def nickname = "teamspeak-bot";


    public void connectAndTest() {

    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (runBot) {
            log.info("Connecting to teamspeak server: " + host)
            config = new TS3Config();
            config.setHost(host);
            config.setDebugLevel(Level.ALL);

            query = new TS3Query(config);
            query.connect();

            api = query.getApi();
            api.login(loginUsername, loginPassword);
            api.selectVirtualServerById(1);
            api.setNickname(nickname);


            try {
                refreshServerName();
            } catch (NullPointerException exc) {
                log.error("Error occurred while refreshing name, please check if you provided correct server admin credentials: ", exc)
                throw exc;
            }
            api.registerEvent(TS3EventType.SERVER)
            api.addTS3Listeners(new TS3Listener() {
                @Override
                void onTextMessage(TextMessageEvent textMessageEvent) {

                }

                @Override
                void onClientJoin(ClientJoinEvent clientJoinEvent) {
                    refreshServerName();
                }

                @Override
                void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
                    refreshServerName();
                }

                @Override
                void onServerEdit(ServerEditedEvent serverEditedEvent) {

                }

                @Override
                void onChannelEdit(ChannelEditedEvent channelEditedEvent) {

                }

                @Override
                void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {

                }

                @Override
                void onClientMoved(ClientMovedEvent clientMovedEvent) {

                }

                @Override
                void onChannelCreate(ChannelCreateEvent channelCreateEvent) {

                }

                @Override
                void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {

                }

                @Override
                void onChannelMoved(ChannelMovedEvent channelMovedEvent) {

                }

                @Override
                void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {

                }

                @Override
                void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {

                }
            })

            api.getServerGroups().stream().forEach({ serverGroup ->
                serverGroups.put(serverGroup.name, serverGroup)
            })
            loadTeamspeakClients()
        } else {
            log.info("Not connecting to teamspeak server, bot is set as off")
        }

    }

    public TS3Api getApi() {
        return api;
    }

    @Scheduled(fixedDelay = 60000l)
    public def loadTeamspeakClients() {
        teamspeakClients = api.getClients()
        teamspeakClientsRefreshed = new Date()
        return teamspeakClients
    }

    Map<Integer, List<Client>> getClientsGroupedByChannels() {
        return getClients().stream().collect(Collectors.groupingBy((Function) { Client c -> return c.getChannelId() }))
    }

    @Override
    List<Client> getClientChannelClients(Object uid) {
        Map<Integer, List<Client>> grouped = getClientsGroupedByChannels()
        for (Map.Entry<Integer, List<Client>> entry : grouped.entrySet()){
            def key = entry.getKey();
            def list = entry.getValue()
            log.info("key: " + key + " list" + list.size())
            Optional<Client> client = list.stream().filter({ c -> (c.getUniqueIdentifier() == uid) }).findAny()
            if (client.isPresent()) {
                def returned = list.stream().filter({ c -> c.getUniqueIdentifier() != uid }).collect(Collectors.toList())
                return returned
            }
        }
        return []
    }

    def getClients() {
        if (!teamspeakClientsRefreshed) {
            loadTeamspeakClients()
        } else {
            def difference = (new Date().getTime() - teamspeakClientsRefreshed.getTime()) / 1000
            if (difference > 60) {
                loadTeamspeakClients()
            }

        }
        return teamspeakClients
    }

    public void refreshServerName() {
        Map<VirtualServerProperty, String> options = new HashMap<>()
        options.put(VirtualServerProperty.VIRTUALSERVER_NAME, "${teamspeakName} [Online: " + api.getServerInfo().clientsOnline + "/512]")
        api.editServer(options)
    }

    Client getClientByIp(String ip) {
        List<Client> clients = api.getClients().stream().filter({ c ->
            c.getIp() == ip
        }).collect(Collectors.toList())
        if (clients.size() != 1) {
            throw new RuntimeException("U need to be on teamspeak server to be found")
        }
        return clients.first()
    }


    boolean isOnline(String uid) {
        Optional<Client> client = this.teamspeakClients.stream().filter({ Client c -> (c.getUniqueIdentifier() == uid) }).findAny()
        return client.isPresent()
//        return api.getDatabaseClientInfo(clientDatabaseId);
//        ClientInfo clientInfo = api.getClientByUId(uid);
//        if(clientInfo){
//            return true
//        }else{
//            return false
//        }
    }

    Client getClient(String uid) {
        Optional<Client> client = this.teamspeakClients.stream().filter({ Client c -> (c.getUniqueIdentifier() == uid) }).findAny()
        if(client.isPresent()) {
            return client.get()
        } else {
            return null;
        }
//        return api.getDatabaseClientInfo(clientDatabaseId);
//        ClientInfo clientInfo = api.getClientByUId(uid);
//        if(clientInfo){
//            return true
//        }else{
//            return false
//        }
    }

    DatabaseClientInfo getClientByDatabaseId(int clientDatabaseId) {
        return api.getDatabaseClientInfo(clientDatabaseId)
    }

    int getServerGroupId(String serverGroup) {
        return serverGroups.get(serverGroup.toString()).id
    }

    void addClientToServerGroup(Client client, ServerGroupType serverGroupType) {
        this.addClientToServerGroup(client.getDatabaseId(), serverGroupType.toString())
    }

    void addClientToServerGroup(int clientDatabaseId, ServerGroupType serverGroupType) {
        this.addClientToServerGroup(clientDatabaseId, serverGroupType.toString())
    }

    void addClientToServerGroup(int clientDatabaseId, String serverGroup) {
        CommandExecutor.execute({ ->
            api.addClientToServerGroup(getServerGroupId(serverGroup), clientDatabaseId)
        })
    }

    void removeClientFromServerGroup(int clientDatabaseId, String serverGroup) {
        CommandExecutor.execute({ ->
            api.removeClientFromServerGroup(getServerGroupId(serverGroup), clientDatabaseId)
        })
    }

    boolean isClientInServerGroup(int clientDatabaseId, String serverGroup) {
        int id = getServerGroupId(serverGroup)
        List<ServerGroup> clientServerGroups = api.getServerGroupsByClientId(clientDatabaseId)
        return clientServerGroups.parallelStream().anyMatch({ sg ->
            sg.id == id
        })
    }

    boolean isClientInServerGroupByClient(Client client, String serverGroup) {
        int id = getServerGroupId(serverGroup)
        List<ServerGroup> clientServerGroups = api.getServerGroupsByClient(client)
        return clientServerGroups.parallelStream().anyMatch({ sg ->
            sg.id == id
        })
    }

    void sendOfflineMessage(String clientUId, String subject, String message) {
        api.sendOfflineMessage(clientUId, subject, message)
    }

    void sendPrivateMessage(Integer clientId, String message) {
        api.sendPrivateMessage(clientId, message)
    }
}
