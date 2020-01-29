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

import java.util.*;

/**
 * Date: 28.01.2020
 * User: Andrey Dashchyk
 */
public class NetworkTree<E extends NetworkNodePointer> implements Network<E>, Iterable<E>{
    private Node<E> root;
    private Node<E> present;

    @Override
    public void create(E root) {
        if (this.root == null) {
            this.root = new Node<>(root, null);
        } else {
            throw new IllegalStateException("Trying to add new root to not empty tree!");
        }
        present = this.root;
    }

    @Override
    public void delNetwork() {
        root = null;
        present = null;
    }

    @Override
    public boolean delChild(E child) {
        return present.delChild(child);
    }

    @Override
    public boolean addChild(E child) {
        boolean ifNodeAllowChildren;
        ifNodeAllowChildren = present.getValue().allowChild();

        if (ifNodeAllowChildren) {
            present.addChild(new Node<>(child, present));
        }

        return ifNodeAllowChildren;
    }

    @Override
    public E getRoot() {
        return root.getValue();
    }

    @Override
    public E getPresent() {
        return present.getValue();
    }

    @Override
    public Optional<E> goToChild(Long Id) {
        Optional<Node<E>> child = present.findChild(Id);

        child.ifPresent(node -> present = node);

        return Optional.ofNullable(present.getValue());
    }

    @Override
    public HashSet<E> getChildren() {
        HashSet<E> childrenValues = new HashSet<>();

        for(Node<E> child : present.getChildren()) {
            childrenValues.add(child.getValue());
        }

        return childrenValues;
    }

    @Override
    public boolean addChildToNode(Long parentId, E networkNode) {
        goToNode(parentId).ifPresent(node -> addChild(networkNode));

        return present.getValue().getId().equals(parentId);
    }

    @Override
    public Optional<E> goToNode(Long id) {
        Optional<E> searchedNode;
        Node<E> presentTemp = present;

        searchedNode = findNode(id);

        if(searchedNode.isEmpty()) {
            present = presentTemp;
        }

        return searchedNode;
    }

    private  Optional<E> findNode(Long id) {

        var networkIterator = new NetworkIterator<>(root) {
            public Node<E> nextNode() {
                return currentLayer.get(index++);
            }
        };

        while((networkIterator.hasNext())) {
            present = networkIterator.nextNode();

            if(present.getValue().getId().equals(id)) {
                return Optional.of(present.getValue());
            }
        }

        return Optional.empty();
    }

    @Override
    public Iterator<E> iterator() {
        return new NetworkIterator<>(root);
    }
}


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

    boolean delChild(E childValue) {
        Optional<Node<E>> child = findChild(childValue.getId());

        child.ifPresent(node -> node.parent.children.remove(node));

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