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
package ua.energynetwork.manager.entity.service;

import org.springframework.stereotype.Service;
import ua.energynetwork.manager.dto.NetworkNodeDTO;
import ua.energynetwork.manager.entity.NetworkHierarchy;
import ua.energynetwork.manager.entity.NetworkNode;
import ua.energynetwork.manager.persistence.EnergyNetworkManagerRepository;

import java.util.List;

/**
 * Date: 29.01.2020
 * User: Andrey Dashchyk
 */
@Service
public class NetworkHierarchyService {
    private final EnergyNetworkManagerRepository energyNetworkManagerRepository;

    public NetworkHierarchyService(EnergyNetworkManagerRepository energyNetworkManagerRepository) {
        this.energyNetworkManagerRepository = energyNetworkManagerRepository;
    }

    public List<NetworkHierarchy> findAll() {
        return energyNetworkManagerRepository.findAll();
    }

    public void addNodeInNetwork(NetworkNodeDTO networkNodeDTO) {
        NetworkNode networkNode = NetworkNode.builder()
                .id(networkNodeDTO.getId())
                .type(networkNodeDTO.getType())
                .name(networkNodeDTO.getName())
                .description(networkNodeDTO.getDescription())
                .params(networkNodeDTO.getParams())
                .build();

        List<NetworkHierarchy> networkHierarchies = energyNetworkManagerRepository.findByName(networkNodeDTO.getRootId());

        networkHierarchies.forEach(networkHierarchy -> networkHierarchy.addNode(networkNodeDTO.getParentId(), networkNode));

        energyNetworkManagerRepository.saveAll(networkHierarchies);
    }


}