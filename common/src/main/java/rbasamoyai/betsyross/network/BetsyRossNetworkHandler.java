package rbasamoyai.betsyross.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Adapted from {@link net.conczin.immersive_paintings.network.NetworkHandler}
 */
public class BetsyRossNetworkHandler {

    private static Sender sender;

    public static void registerSender(Sender s) { sender = s; }

    public static void sendToClient(ServerPlayer player, BetsyRossPayload payload) { sender.send(player, payload); }

    public static void sendToAllClients(MinecraftServer server, BetsyRossPayload payload) {
        server.getPlayerList().getPlayers().forEach(p -> sendToClient(p, payload));
    }

    public interface Sender {
        void send(ServerPlayer player, BetsyRossPayload payload);
    }

    public static class Client {
        private static Sender sender;

        public static void registerSender(Sender s) { sender = s; }

        public static void sendToServer(BetsyRossPayload payload) { sender.send(payload); }

        public interface Sender {
            void send(BetsyRossPayload payload);
        }
    }

}
