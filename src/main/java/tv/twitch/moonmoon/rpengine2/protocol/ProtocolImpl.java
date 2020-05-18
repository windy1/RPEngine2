package tv.twitch.moonmoon.rpengine2.protocol;

import com.comphenix.protocol.ProtocolLibrary;

import javax.inject.Inject;
import java.util.Objects;

public class ProtocolImpl implements Protocol {

    private final PlayerInfoPacketAdapter playerInfoAdapter;

    @Inject
    public ProtocolImpl(PlayerInfoPacketAdapter playerInfoAdapter) {
        this.playerInfoAdapter = Objects.requireNonNull(playerInfoAdapter);
    }

    @Override
    public void init() {
        ProtocolLibrary.getProtocolManager().addPacketListener(playerInfoAdapter);
    }
}
