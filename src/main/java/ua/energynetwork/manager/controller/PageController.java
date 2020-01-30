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
package ua.energynetwork.manager.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.energynetwork.manager.dto.NetworkNodeDTO;
import ua.energynetwork.manager.model.entity.NetworkHierarchy;
import ua.energynetwork.manager.model.service.NetworkHierarchyService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Date: 28.01.2020
 * User: Andrey Dashchyk
 */
@RestController
public class PageController {
    Logger logger = LogManager.getLogger();
    private final NetworkHierarchyService networkHierarchyService;

    public PageController(NetworkHierarchyService networkHierarchyService) {
        this.networkHierarchyService = networkHierarchyService;
    }

    @RequestMapping("networks_list")
    public List<NetworkHierarchy> SearchPage() {
            return networkHierarchyService.findAll();
    }

    @RequestMapping("nodes_list")
    public List<NetworkHierarchy> SearchNodesPage(@RequestParam Long id) {
            return networkHierarchyService.findByRootId(id);
    }

    /**
     * I cant solve problem with mongoDB
     *
     * It do not transform and save my custom collection
     * But it works without this dependence
     */
    @RequestMapping("creat_network")
    public String  createNetworkPage(@RequestParam String requestParams){
        try {
            NetworkNodeDTO rootDTO = convertToNetworkNodeDTO(makeMap(requestParams));
            networkHierarchyService.creatNetwork(rootDTO);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("@RequestMapping(\"creat_network\"): " + e.toString());
        }

        return "networks_list";
    }

    private Map<String, String> makeMap(String requestParams) {
        Map<String, String> map = new HashMap<>();

        Arrays.stream(requestParams.split(","))
                .map(pair -> pair.split("="))
                .forEach(pairArr -> map.put(pairArr[0], pairArr[1]));

        return map;
    }

    private NetworkNodeDTO convertToNetworkNodeDTO(Map<String,String> requestParams)
            throws NoSuchFieldException, IllegalAccessException {

        NetworkNodeDTO networkNodeDTO = new NetworkNodeDTO();
        logger.info(requestParams.keySet());

        for(String key : requestParams.keySet()) {
            Field field = NetworkNodeDTO.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(networkNodeDTO, requestParams.get(key));
        }

        logger.info(networkNodeDTO);
        return networkNodeDTO;
    }
}