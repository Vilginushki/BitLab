package io.github.vilginushki.model.messages.bitcoinTypes;


import io.github.vilginushki.Config;
import io.github.vilginushki.enums.NodeType;

import java.util.ArrayList;

public class Service {
    private final long value;
    private final ArrayList<NodeType> types;

    public Service(long value) {
        this.value = value;
        types = new ArrayList<>(Config.NODE_TYPES.length);
        for (int i = 0; i < Config.NODE_TYPES.length; i++) {
            if ((value & 0x01) == 0x01)
                types.add(Config.NODE_TYPES[i]);
            value = value >> 1;
        }
    }

    public String toString() {
        if (this.value == 0) return "UNKNOWN_NODE";
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i) != null)
                sb.append(types.get(i));
            if (i != types.size() - 1)
                sb.append("/");
        }
        return sb.toString();
    }
}
