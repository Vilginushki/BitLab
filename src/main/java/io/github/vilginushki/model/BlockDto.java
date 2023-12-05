package io.github.vilginushki.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockDto {
    private String hash;
    private long height;
    private String chain;
    private long total;
    private long fees;
    private long size;
    private long ver;
    private Date time;
    private Date received_time;
    private String coinbase_addr;
    private String relayed_by;
    private long bits;
    private long nonce;
    private long n_tx;
    private String prev_block;
    private String mrkl_root;
    private List<String> txids;
    private long depth;
    private String prev_block_url;
    private String tx_url;
    private String next_txids;

    @Override
    public String toString() {
        return "Block{" + "\n" +
                "   version=" + ver + ",\n" +
                "   prev_block=" + prev_block + "\n" +
                "   merkle_root=" + mrkl_root + "\n" +
                "   timestamp=" + time + ",\n" +
                "   bits=" + bits + ",\n" +
                "   nonce=" + nonce + ",\n" +
                "   txn_count=" + n_tx + ",\n" +
                "   txns=[\n" + txids.stream().map(id -> "       " + id + ",\n").collect(Collectors.joining()) + "     ],\n" +
                '}';
    }
}
