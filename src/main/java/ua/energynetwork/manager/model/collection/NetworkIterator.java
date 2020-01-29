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
package ua.energynetwork.manager.model.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 29.01.2020
 * User: Andrey Dashchyk
 */
public class NetworkIterator<E extends NetworkNodePointer> implements Iterator<E> {
    private List<Node<E>> currentLayer;
    private boolean ifLayerAllowChild;
    private int index;

    public NetworkIterator(Node<E> root) {
        currentLayer = new ArrayList<>();
        currentLayer.add(root);
    }


    @Override
    public boolean hasNext() {
        boolean lastChild = index == currentLayer.size();

        if (lastChild) {
            updateCurrentLayer();
        }

        return !lastChild;
    }

    private void updateCurrentLayer() {
        currentLayer.stream()
                .map(node -> node.getValue().allowChild())
                .reduce((x, y) -> x || y)
                .ifPresent(this::setIfLayerAllowChild);

        if (ifLayerAllowChild) {
            index = 0;
            currentLayer = currentLayer.stream()
                    .flatMap(node -> node.getChildren().stream())
                    .collect(Collectors.toList());
        }
    }

    private void  setIfLayerAllowChild(boolean ifLayerAllowChild) {
        this.ifLayerAllowChild = ifLayerAllowChild;
    }

    @Override
    public E next() {
        return currentLayer.get(index++).getValue();
    }

    public Node<E> nextNode() {
        return currentLayer.get(index++);
    }
}