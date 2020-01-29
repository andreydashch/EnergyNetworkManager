/*
 * MIT License
 *
 * Copyright (c) 2020 Andrey Dashchyk
 *
 * Permission is hereby granted, free of charge,to any
 * person obtaining a copy of this software and
 * associated documentation files (the "Software"), to
 * deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission
 * notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package ua.energynetwork.manager.model.service;

import org.apache.logging.log4j.*;
import ua.energynetwork.manager.dto.*;
import ua.energynetwork.manager.model.entity.*;
import org.springframework.stereotype.Service;
import ua.energynetwork.manager.persistence.EnergyNetworkManagerRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Date: 29.01.2020
 * User: Andrey Dashchyk
 */
@Service
public class NetworkHierarchyService {
    Logger logger = LogManager.getLogger();
    private final EnergyNetworkManagerRepository energyNetworkManagerRepository;

    public NetworkHierarchyService(EnergyNetworkManagerRepository energyNetworkManagerRepository) {
        this.energyNetworkManagerRepository = energyNetworkManagerRepository;
    }

    public List<NetworkHierarchy> findAll() {
        logger.info("ServiceRunFindAllNetworks");
        return energyNetworkManagerRepository.findAll();
    }

    public List<NetworkHierarchy> findByRootId(Long rootId) {
        logger.info("ServiceRunFindByRootId");
        return energyNetworkManagerRepository.findByRootId(rootId);
    }

    public List<NetworkNode> findNetworkNodes(Long rootId, Long nodeId) {
        logger.info("ServiceRunFindNetworkNodes");
        List<NetworkNode> networkNodes = new ArrayList<>();
        List<NetworkHierarchy> networkHierarchies = findByRootId(rootId);

        for(NetworkHierarchy networkHierarchy : networkHierarchies) {
            networkHierarchy.getNode(nodeId).ifPresent(networkNodes::add);
        }

        return networkNodes;
    }

    public void delNetworks(Long rootId) {
        logger.info("ServiceRunDelNetworks");
        energyNetworkManagerRepository.deleteByRootId(rootId);
    }

    public void addNodeInNetwork(NetworkNodeDTO networkNodeDTO) {
        logger.info("ServiceRunAddNodeInNetworks");
        NetworkNode networkNode = createNetworkNode(networkNodeDTO);

        List<NetworkHierarchy> networkHierarchies = findByRootId(networkNodeDTO.getRootId());

        networkHierarchies.forEach(networkHierarchy -> networkHierarchy.addNode(networkNodeDTO.getParentId(), networkNode));

        energyNetworkManagerRepository.saveAll(networkHierarchies);
    }

    private NetworkNode createNetworkNode(NetworkNodeDTO networkNodeDTO) {
        logger.info("ServiceRunCreatNetworkNode");
        return NetworkNode.builder()
                .id(networkNodeDTO.getId())
                .type(networkNodeDTO.getType())
                .name(networkNodeDTO.getName())
                .description(networkNodeDTO.getDescription())
                .params(networkNodeDTO.getParams())
                .build();
    }

    public void creatNetwork(NetworkNodeDTO rootDTO) {
        logger.info("ServiceRunCreatNetwork");
        NetworkNode root = createNetworkNode(rootDTO);

        NetworkHierarchy networkHierarchy = new NetworkHierarchy(root);

        energyNetworkManagerRepository.save(networkHierarchy);
    }

    public void creatWholeNetwork(NetworkHierarchyDTO networkHierarchyDTO) {
        logger.info("ServiceRunCreatWholeNetwork");
        NetworkNode root = createNetworkNode(networkHierarchyDTO.networkHierarchy.get(0));
        NetworkHierarchy networkHierarchy = new NetworkHierarchy(root);

        networkHierarchyDTO.networkHierarchy.stream()
                .skip(1)
                .forEach(nodeDto -> networkHierarchy.addNode(nodeDto.getParentId(), createNetworkNode(nodeDto)));

        energyNetworkManagerRepository.save(networkHierarchy);
    }

    public void delNodeInNetwork(Long rootId, Long parentId, Long id) {
        logger.info("ServiceRunDelNodeInNetwork");
        List<NetworkHierarchy> networkHierarchies = findByRootId(rootId);

        networkHierarchies.forEach(networkHierarchy -> networkHierarchy.delNode(parentId, id));

        energyNetworkManagerRepository.saveAll(networkHierarchies);
    }

    public boolean verifyIfConsistent(Long rootId) {
        logger.info("ServiceRunVerifyIfConsistent");
        boolean consistent = false;
        List<NetworkHierarchy> networkHierarchies = findByRootId(rootId);

        if (networkHierarchies.size() == 1) {
            consistent = isConsistent(networkHierarchies);
        }

        return consistent;
    }

    private boolean isConsistent(List<NetworkHierarchy> networkHierarchies) {
        boolean consistent = true;
        Set<Long> uniqueIds = new HashSet<>();
        Long nodeId;

        for (NetworkNode networkNode : networkHierarchies.get(0).getNetwork()) {
            nodeId = networkNode.getId();

            if (uniqueIds.contains(nodeId)) {
                consistent = false;
                break;
            } else {
                uniqueIds.add(nodeId);
            }
        }

        return consistent;
    }


}