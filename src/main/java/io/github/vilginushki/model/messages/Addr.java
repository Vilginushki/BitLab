package io.github.vilginushki.model.messages;

import io.github.vilginushki.enums.CommandMap;
import io.github.vilginushki.model.messages.bitcoinTypes.NetAddr;
import io.github.vilginushki.util.ByteParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class Addr extends Message implements Receivable {

    private long count;
    private List<NetAddr> list;


    public Addr(Header header) {
        super(CommandMap.ADDR);
        deserialize(header);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        count = parser.parseVarInt().value();
        list = new ArrayList<>((int) count);
        for (int i = 0; i < count; i++) {
            NetAddr addr = parser.parseNetAddr();
            list.add(addr);
        }
        log.info(count + "  NetAddr have been processed");
    }

    private void deserialize(Header header) {
        this.header = header;
        ByteParser parser = new ByteParser(header.getContent());
        count = parser.parseVarInt().value();
        list = new ArrayList<>((int) count);
        for (int i = 0; i < count; i++) {
            NetAddr addr = parser.parseNetAddr();
            list.add(addr);
        }
        log.info(count + " NetAddr have been processed");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Addr ");
        sb.append("count: ").append(count).append("\n");
        int max = (int) (count > 20 ? 20 : count);
        for (int i = 0; i < max; i++) {
            sb.append(String.format("#%02d", i + 1)).append(" ").append(list.get(i));
            if (i != max - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

}
