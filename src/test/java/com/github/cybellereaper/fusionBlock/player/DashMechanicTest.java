package com.github.cybellereaper.fusionBlock.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DashMechanicTest {

    private MockedStatic<Bukkit> bukkit;
    private Server server;
    private PluginManager pluginManager;

    @BeforeEach
    void setUp() {
        bukkit = Mockito.mockStatic(Bukkit.class);
        server = mock(Server.class);
        pluginManager = mock(PluginManager.class);
        bukkit.when(Bukkit::getServer).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void registersListenersOnConstruction() {
        JavaPlugin plugin = mock(JavaPlugin.class);

        new DashMechanic(plugin);

        verify(pluginManager).registerEvents(any(DashMechanic.class), eq(plugin));
    }

    @Test
    void appliesCooldownAndSendsWarning() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        AtomicLong time = new AtomicLong(10_000L);
        DashMechanic mechanic = new DashMechanic(plugin, time::get);

        Player player = preparePlayerMock();

        mechanic.onDoubleTapW(new PlayerToggleSprintEvent(player, true));
        time.addAndGet(1_000L);
        mechanic.onDoubleTapW(new PlayerToggleSprintEvent(player, true));

        verify(player).sendMessage(contains("cannot dash"));
    }

    @Test
    void boostsVelocityWhenOffCooldown() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        DashMechanic mechanic = new DashMechanic(plugin, () -> 10_000L);

        Player player = preparePlayerMock();

        mechanic.onDoubleTapW(new PlayerToggleSprintEvent(player, true));

        verify(player).setVelocity(argThat(vector -> vector.getX() != 0 || vector.getZ() != 0));
        verify(pluginManager).registerEvents(any(DashMechanic.class), eq(plugin));
    }

    private Player preparePlayerMock() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        Location location = mock(Location.class);
        World world = mock(World.class);
        when(location.getWorld()).thenReturn(world);
        when(location.clone()).thenReturn(location);
        when(location.add(anyDouble(), anyDouble(), anyDouble())).thenReturn(location);
        doNothing().when(location).setYaw(anyFloat());

        Vector direction = new Vector(1, 0, 0);
        when(location.getDirection()).thenReturn(direction);
        when(location.getYaw()).thenReturn(0f);

        when(player.getLocation()).thenReturn(location);
        when(player.getVelocity()).thenReturn(new Vector());
        when(player.isFlying()).thenReturn(false);

        doAnswer(invocation -> {
            Particle particle = invocation.getArgument(0);
            return null;
        }).when(world).spawnParticle(eq(Particle.CLOUD), any(Location.class), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble());

        return player;
    }
}
