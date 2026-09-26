package io.papermc.paper.permissions;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.util.permissions.CraftDefaultPermissions;
import org.bukkit.permissions.Permission;
import org.bukkit.support.RegistryHelper;
import org.bukkit.support.environment.VanillaFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@VanillaFeature
public class MinecraftCommandPermissionsTest {

    private static PrintStream old;
    @BeforeAll
    public static void before() {
        old = System.out;
        System.setOut(Bootstrap.STDOUT);
    }

    @Test
    public void test() {
        CraftDefaultPermissions.registerCorePermissions();
        Set<String> perms = collectMinecraftCommandPerms();

        Commands commands = new Commands(Commands.CommandSelection.DEDICATED, CommandBuildContext.simple(RegistryHelper.registryAccess(), FeatureFlags.VANILLA_SET), true);
        RootCommandNode<CommandSourceStack> root = commands.getDispatcher().getRoot();
        Set<String> missing = new LinkedHashSet<>();
        Set<String> foundPerms = new HashSet<>();
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            final String vanillaPerm = VanillaCommandWrapper.getPermission(child);
            if (TO_SKIP.contains(vanillaPerm)) continue; // Purpur - Skip junit tests for purpur commands
            if (!perms.contains(vanillaPerm)) {
                missing.add("Missing permission for " + child.getName() + " (" + vanillaPerm + ") command");
            } else {
                foundPerms.add(vanillaPerm);
            }
        }
        assertTrue(missing.isEmpty(), "Commands missing permissions: \n" + String.join("\n", missing));
        perms.removeAll(foundPerms);
        assertTrue(perms.isEmpty(), "Extra permissions not associated with a command: \n" + String.join("\n", perms));
    }

    private static final List<String> TO_SKIP = List.of(
        // Purpur start - Skip junit tests for purpur commands
        "minecraft.command.compass",
        "minecraft.command.credits",
        "minecraft.command.demo",
        "minecraft.command.ping",
        "minecraft.command.ram",
        "minecraft.command.rambar",
        "minecraft.command.tpsbar",
        "minecraft.command.uptime",
        "minecraft.command.debug",
        "minecraft.command.gamemode.adventure",
        "minecraft.command.gamemode.adventure.other",
        "minecraft.command.gamemode.creative",
        "minecraft.command.gamemode.creative.other",
        "minecraft.command.gamemode.spectator",
        "minecraft.command.gamemode.spectator.other",
        "minecraft.command.gamemode.survival",
        "minecraft.command.gamemode.survival.other",
        // Purpur end - Skip junit tests for purpur commands
        "minecraft.command.selector"
    );

    private static Set<String> collectMinecraftCommandPerms() {
        Set<String> perms = new TreeSet<>();
        for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
            if (perm.getName().startsWith("minecraft.command.")) {
                if (TO_SKIP.contains(perm.getName())) {
                    continue;
                }
                perms.add(perm.getName());
            }
        }
        return perms;
    }

    @AfterAll
    public static void after() {
        if (old != null) {
            System.setOut(old);
        }
    }
}
