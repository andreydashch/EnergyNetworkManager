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
package ua.energynetwork.manager.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.Document;
import ua.energynetwork.manager.model.collection.Network;
import ua.energynetwork.manager.model.collection.NetworkTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Date: 29.01.2020
 * User: Andrey Dashchyk
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NetworkHierarchy {
    @Id
    private Long rootId;
    private Network<NetworkNode> network;

    public NetworkHierarchy(NetworkNode root) {
        network = new NetworkTree<>();

        network.create(root);
        rootId = root.getId();
    }
    public Long getRootId() {
        rootId = network.getRoot().getId();
        return rootId;
    }

    public void addNode(Long parentId, NetworkNode networkNode) {
        network.addChildToNode(parentId, networkNode);
    }

    public Optional<NetworkNode> getNode(Long id) { return network.findNode(id); }

    public void delNode(Long parentId, Long id) {
        network.findNode(parentId);
        network.delChild(id);
    }
}