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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Optional;

/**
 * Date: 30.01.2020
 * User: Andrey Dashchyk
 */
@RequiredArgsConstructor
@Getter
class Node<E extends NetworkNodePointer> {
    private final E value;
    private final Node<E> parent;
    private HashSet<Node<E>> children;

    {
        children = new HashSet<>();
    }

    void addChild(Node<E> child) {
        children.add(child);
    }

    boolean delChild(Long id) {
        Optional<Node<E>> child = findChild(id);

        child.ifPresent(node -> children.remove(node));

        return child.isPresent();
    }

    Optional<Node<E>> findChild(Long id) {
        Node<E> child = null;
        boolean isSameNode;

        for(Node<E> itr : children) {
            isSameNode = itr.getValue().getId().equals(id);

            if(isSameNode) {
                child = itr;
            }
        }

        return Optional.ofNullable(child);
    }
}