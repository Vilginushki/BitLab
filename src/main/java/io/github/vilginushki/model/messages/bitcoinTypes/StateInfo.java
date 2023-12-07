package io.github.vilginushki.model.messages.bitcoinTypes;


import io.github.vilginushki.enums.TypeOfAction;
import io.github.vilginushki.model.messages.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateInfo {

    private final String ip;
    private final int port;
    private final long connectionTry;
    private Version version;
    private boolean timeout;
    private boolean success;
    private Throwable exception;
    private TypeOfAction typeOfAction;


    public StateInfo(String ip, int port, long connectionTry) {
        this.ip = ip;
        this.port = port;
        this.connectionTry = connectionTry;
        timeout = false;
        success = false;
        typeOfAction = TypeOfAction.NOTSET;
    }

    public StateInfo(final IPWrapper ip, final int port, final long connectionTry) {
        this(ip.toString(), port, connectionTry);
    }

}
