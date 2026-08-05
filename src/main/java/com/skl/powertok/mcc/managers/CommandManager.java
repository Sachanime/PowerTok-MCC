package com.skl.powertok.mcc.managers;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class CommandManager {
    
    private final JavaPlugin plugin;
    AdvancedWorldManager advancedWorldManager = new AdvancedWorldManager();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand() {

        this.plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();
            registerAWMCommand(registrar);
        });

    }

    @SuppressWarnings("null")
    private void registerAWMCommand(Commands registrar) {

        String commandDesc = "Gérer les mondes";

        LiteralCommandNode<CommandSourceStack> commandNode = Commands.literal("awm")
        .requires(source -> source.getSender().hasPermission("mcc.command.awm"))
        .then(Commands.literal("convert")
            .then(Commands.argument("worldname", StringArgumentType.word())
                .then(Commands.argument("slimename", StringArgumentType.word())
                    .executes(ctx -> {

                        CommandSender sender = ctx.getSource().getSender();
                        String worldName = StringArgumentType.getString(ctx, "worldname");
                        String slimeName = StringArgumentType.getString(ctx, "slimename");

                        advancedWorldManager.createSlime(sender, worldName, slimeName);

                        return(1);

                    })
                )
            )
        )
        .build();

        registrar.register(commandNode, commandDesc);

    }

}
