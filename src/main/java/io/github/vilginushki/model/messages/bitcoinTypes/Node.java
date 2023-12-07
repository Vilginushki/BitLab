package io.github.vilginushki.model.messages.bitcoinTypes;

import java.util.LinkedList;

public class Node {

    private final LinkedList<NetAddr> list;
    private final LinkedList<Tag> tag;
    private int duplicated;

    public Node(NetAddr netAddr, StateInfo bundle) {
        duplicated = 0;
        list = new LinkedList<>();
        tag = new LinkedList<>();
        list.add(netAddr);
        tag.add(new Tag(bundle.getIp(), bundle.getConnectionTry()));
    }

    public void add(NetAddr netAddr, StateInfo bundle) {
        list.add(netAddr);
        tag.add(new Tag(bundle.getIp(), bundle.getConnectionTry()));
    }

    public void duplicated() {
        duplicated++;
    }

}
