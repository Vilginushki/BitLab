package io.github.vilginushki.model.messages.bitcoinTypes;


import java.util.Set;
import java.util.TreeMap;

public class NodeHashMap {

    private final TreeMap<String, Node> peers;

    public NodeHashMap() {
        peers = new TreeMap<>();
    }

    synchronized public boolean insert(NetAddr netAddr, StateInfo bundle) {
        String ip = netAddr.getIp().toString();

        if (!peers.containsKey(ip)) {
            peers.put(ip, new Node(netAddr, bundle));
            return true;
        } else {
            Node node = peers.get(ip);
            node.add(netAddr, bundle);
            node.duplicated();
            return false;
        }
    }

    public int size() {
        return peers.size();
    }

    public Set<String> getPeers() {
        return peers.keySet();
    }
}
