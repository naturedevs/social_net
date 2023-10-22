package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.domain.entity.CongestionLevel;
import com.orbvpn.api.domain.entity.Server;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.ServerEditMapper;
import com.orbvpn.api.mapper.ServerViewMapper;
import com.orbvpn.api.reposiitory.CongestionLevelRepository;
import com.orbvpn.api.reposiitory.ServerRepository;
import com.orbvpn.api.service.common.SshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServerService {
    private final ServerRepository serverRepository;
    private final CongestionLevelRepository congestionLevelRepository;
    private final ServerEditMapper serverEditMapper;
    private final ServerViewMapper serverViewMapper;
    private final UserService userService;

    private final RadiusService radiusService;

    public ServerView createServer(ServerEdit serverEdit) {
        log.info("Creating server with data {}", serverEdit);
        Server server = serverEditMapper.create(serverEdit);

        serverRepository.save(server);
        radiusService.createNas(server);
        ServerView serverView = serverViewMapper.toView(server);
        log.info("Created server {}", serverView);
        return serverView;
    }

    public ServerView editServer(int id, ServerEdit serverEdit) {
        log.info("Editing server with id {} with data {}", id, serverEdit);

        Server server = getServerById(id);
        String publicIp = server.getPublicIp();
        server = serverEditMapper.edit(server, serverEdit);
        serverRepository.save(server);
        radiusService.editNas(publicIp, server);

        ServerView serverView = serverViewMapper.toView(server);
        log.info("Edited server {}", serverView);
        return serverView;
    }

    public ServerView deleteServer(int id) {
        log.info("Deleting server with id {}", id);

        Server server = getServerById(id);
        serverRepository.delete(server);
        radiusService.deleteNas(server);

        ServerView serverView = serverViewMapper.toView(server);
        log.info("Deleted server {}", serverView);
        return serverView;
    }

    public ServerView getServer(int id) {
        Server server = getServerById(id);

        return serverViewMapper.toView(server);
    }

    public List<ServerView> getServers() {
        return serverRepository.findAll()
                .stream()
                .map(serverViewMapper::toView)
                .collect(Collectors.toList());
    }

    public List<ClientServerView> getClientServers() {
        return serverRepository.findAll()
                .stream().filter(s -> s.getHide() == 0)
                .map(serverViewMapper::toClientView)
                .collect(Collectors.toList());
    }

    public List<ClientServerView> getClientSortedServers(String sortBy, String parameter) {
        User user = userService.getUser();
        String sortProperties;

        switch (sortBy) {
            case "recent-connection":
                String email = user.getEmail();
                return serverRepository.findServerByRecentConnection(email)
                        .stream()
                        .map(serverViewMapper::toClientView).collect(Collectors.toList());
            case "congestion":
                List<ClientServerView> ClientServerViewList = getClientServers();
                List<Server> ServerList = serverRepository.findAll()
                        .stream().filter(s -> s.getHide() == 0)
                        .collect(Collectors.toList());

                List<CongestionLevel> CongestionLevelList = new ArrayList<>(congestionLevelRepository.findAll());

                int totalUserCount = 0;
                for (Server server : ServerList) {
                    int connectedUserCount = SshUtil.getServerConnectedUsers(server);
                    totalUserCount += connectedUserCount;
                    ClientServerView clientServerView = ClientServerViewList.stream().filter(s -> s.getId() == server.getId()).findAny().orElse(null);
                    if (clientServerView == null)
                        clientServerView.setConnectedUserCount(0);
                    else
                        clientServerView.setConnectedUserCount(connectedUserCount);
                }
                for (ClientServerView server : ClientServerViewList) {
                    var percent = server.getConnectedUserCount() / totalUserCount * 100;

                    for (CongestionLevel congestionLevel : CongestionLevelList) {
                        if ((percent >= congestionLevel.getMin()) && (percent <= congestionLevel.getMax())) {
                            server.setCongestionLevel(congestionLevel.getName());
                        }
                    }
                }
                ClientServerViewList.sort((o1, o2) -> {
                    if (o1.getConnectedUserCount() == o2.getConnectedUserCount()) {
                        return 0;
                    } else if (o1.getConnectedUserCount() < o2.getConnectedUserCount()) {
                        return 1;
                    }
                    return -1;
                });
                return ClientServerViewList;
            case "alphabetic":
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "hostName"))
                        .stream().filter(s -> s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "continental":
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "continent"))
                        .stream().filter(s -> s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "crypto-friendly":
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "hostName"))
                        .stream().filter(s -> s.getCryptoFriendly() == 1 && s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "hero":
                sortProperties = parameter.length() > 0 ? parameter : "hero";
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, sortProperties))
                        .stream().filter(s -> s.getHero() != null && s.getHero().length() > 0 && s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "spot":
                sortProperties = parameter.length() > 0 ? parameter : "spot";
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, sortProperties))
                        .stream().filter(s -> s.getSpot() != null && s.getSpot().length() > 0 && s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "zeus":
                sortProperties = parameter.length() > 0 ? parameter : "zeus";
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, sortProperties))
                        .stream().filter(s -> s.getZeus() != null && s.getZeus().length() > 0 && s.getHide() == 0)
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            case "orb":
                sortProperties = parameter.length() > 0 ? parameter : "hostName";
                return serverRepository.findAll(Sort.by(Sort.Direction.ASC, sortProperties))
                        .stream()
                        .filter(s -> s.getHide() == 0 && ((s.getZeus() == null || s.getZeus().length() == 0) &&
                                        (s.getSpot() == null || s.getSpot().length() == 0) &&
                                        (s.getHero() == null || s.getHero().length() == 0)))
                        .map(serverViewMapper::toClientView)
                        .collect(Collectors.toList());
            default:
                ClientServerViewList = getClientServers();
                return ClientServerViewList;
        }
    }

    public Server getServerById(int id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Server.class, id));
    }
}
