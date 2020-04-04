package dev.westernpine.chip.listener;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import dev.westernpine.chip.Chip;
import dev.westernpine.chip.backend.Backend;
import dev.westernpine.chip.utility.Levels;

import java.util.Optional;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.api.marble.lib.emoji.Emoji;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String prefix = Chip.getInstance().getPrefix();
        String message = event.getMessage().getContentRaw();
        if(message.startsWith(prefix)) {
            if(message.startsWith(prefix + "levels") || message.startsWith(prefix + "chips") || message.startsWith(prefix + "top")) {
                LinkedHashMap<String, Long> top = Chip.getInstance().getBackend().getUsersTop(event.getGuild().getId(), 5);
                
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.PINK);
                builder.setTitle("Top Overall Chatters:");
                
                StringBuilder string = new StringBuilder();
                LinkedList<Entry<String, Long>> entries = new LinkedList<>();
                top.entrySet().forEach(en -> entries.offerLast(en));
                int limit = 5;
                for(int i = 0; i < limit; i++) {
                    if(i > entries.size()-1)
                        break;
                    Entry<String, Long> entry = entries.get(i);
                    string.append("`" + (i+1) + "`. **[" + Levels.getLevel(Chip.getInstance().getBackend().getWordCount(event.getGuild().getId(), entry.getKey())) + "]**" + Chip.getInstance().getManager().getUserById(entry.getKey()).getAsMention() + " >> *" + entry.getValue() + " Words* \n\n");
                }
                builder.setDescription(string.toString());
                
                event.getChannel().sendMessage(builder.build()).queue();
                
            } else if(message.startsWith(prefix + "chip") || message.startsWith(prefix + "level")) {
                String guild = event.getGuild().getId();
                String target = event.getMessage().getAuthor().getId();
                if(message.contains(" ")) {
                    if(event.getMessage().getMentionedUsers().size() > 0) {
                        target = event.getMessage().getMentionedUsers().get(0).getId();
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(Emoji.CrossMark + " **Please tag the user!**").build()).queue();
                        return;
                    }
                }
                
                Chip chip = Chip.getInstance();
                Backend backend = chip.getBackend();
                long words = backend.getWordCount(guild, target);
                long level = Levels.getLevel(words);
                
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.PINK);
                
                User targetUser = Chip.getInstance().getManager().getUserById(target);
                builder.setAuthor(targetUser.getName() + "#" + targetUser.getDiscriminator() + " Chip Information", null, targetUser.getAvatarUrl());
                builder.addField("Chips:", "`" + level + "`", true);
                builder.addField("Words Typed:", "`" + words + "` Words", true);
                builder.addField("Until Next chip:", "`" + Levels.untilNextLevel(words) + "` More Words", true);
                
                User author = event.getMessage().getAuthor();
                builder.setFooter("Requested By: " + author.getName() + "#" + author.getDiscriminator(), author.getAvatarUrl());
                
                event.getChannel().sendMessage(builder.build()).queue();
            } else if(message.startsWith(prefix + "oc")) {
                if(!event.getMember().hasPermission(Permission.MANAGE_SERVER))
                    return;
                
                if(message.contains(" ") && message.split(" ").length >= 2) {
                    
                    if(message.startsWith(prefix + "oc off")) {
                        Chip.getInstance().getBackend().setOutput(event.getGuild().getId(), Optional.empty());
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.PINK).setDescription(Emoji.Pencil2 + " **Output channel turned off!**").build()).queue();
                        return;
                    }
                    
                    List<TextChannel> channels = event.getMessage().getMentionedChannels();
                    if(channels.isEmpty() || channels.size() > 1) {
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(Emoji.CrossMark + " **Please use the tag of a single channel!**").build()).queue();
                        return;
                    }
                    
                    Chip.getInstance().getBackend().setOutput(event.getGuild().getId(), Optional.of(channels.get(0).getIdLong()));
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.PINK).setDescription(Emoji.Pencil2 + " **Output channel set to:** " + channels.get(0).getAsMention()).build()).queue();
                    
                } else {
                    boolean anyChannel = true;
                    Optional<Long> oc = Chip.getInstance().getBackend().getOutput(event.getGuild().getId());
                    if(!oc.isPresent())
                        anyChannel = true;
                    else
                        anyChannel = event.getGuild().getTextChannelById(oc.get()) == null;
                    
                    
                    if(anyChannel) {
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.PINK).setDescription(Emoji.Pencil2 + " **Output channel off!**").build()).queue();
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.PINK).setDescription(Emoji.Pencil2 + " **Current output channel:** " + event.getGuild().getTextChannelById(oc.get()).getAsMention()).build()).queue();
                    }
                }
            }
        }
    }
    
}
