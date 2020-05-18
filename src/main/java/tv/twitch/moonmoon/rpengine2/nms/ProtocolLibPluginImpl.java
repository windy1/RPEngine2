package tv.twitch.moonmoon.rpengine2.nms;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class ProtocolLibPluginImpl implements ProtocolLibPlugin {

    private final PlayerInfoPacketAdapter playerInfoAdapter;

    @Inject
    public ProtocolLibPluginImpl(PlayerInfoPacketAdapter playerInfoAdapter) {
        this.playerInfoAdapter = Objects.requireNonNull(playerInfoAdapter);
    }

    @Override
    public void init() {
        ProtocolLibrary.getProtocolManager().addPacketListener(playerInfoAdapter);
    }

    static class PlayerInfoPacketAdapter extends PacketAdapter {

        private final RpPlayerRepo playerRepo;
        private final Logger log;

        @Inject
        public PlayerInfoPacketAdapter(
            Plugin plugin,
            RpPlayerRepo playerRepo,
            @PluginLogger Logger log
        ) {
            super(plugin, PacketType.Play.Server.PLAYER_INFO);
            this.playerRepo = Objects.requireNonNull(playerRepo);
            this.log = Objects.requireNonNull(log);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            WrapperPlayServerPlayerInfo wrapper =
                new WrapperPlayServerPlayerInfo(event.getPacket());

            if (wrapper.getAction() != EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                return;
            }

            List<PlayerInfoData> newData = new ArrayList<>();

            for (PlayerInfoData data : wrapper.getData()) {
                Player player;

                if (data == null
                    || data.getProfile() == null
                    || (player = Bukkit.getPlayer(data.getProfile().getUUID())) == null
                    || !player.isOnline()) {
                    newData.add(data);
                    continue;
                }

                System.out.println("player " + player);

                Result<RpPlayer> p = playerRepo.getPlayer(player);

                Optional<String> err = p.getError();
                if (err.isPresent()) {
                    log.warning(err.get());
                    newData.add(data);
                    return;
                }

                String newName = playerRepo.getIdentityPlain(p.get());

                WrappedGameProfile profile = data.getProfile();
                WrappedGameProfile newProfile = profile.withName(newName);

                PlayerInfoData newPlayerInfoData = new PlayerInfoData(
                    newProfile, data.getLatency(), data.getGameMode(), data.getDisplayName()
                );

                newProfile.getProperties().putAll(profile.getProperties());
                newData.add(newPlayerInfoData);
            }

            wrapper.setData(newData);
        }
    }
}
