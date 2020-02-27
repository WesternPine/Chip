package proj.chip.listener;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import proj.api.marble.lib.emoji.Emoji;
import proj.chip.Chip;
import proj.chip.backend.Backend;
import proj.chip.utility.Levels;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String prefix = Chip.getInstance().getPrefix();
        String message = event.getMessage().getContentRaw();
        if(message.startsWith(prefix)) {
            if(message.startsWith(prefix + "levels") || message.startsWith(prefix + "chips") || message.startsWith(prefix + "top")) {
                
                LinkedHashMap<String, Long> top = Chip.getInstance().getBackend().getTopUsers(event.getGuild().getId());
                
                int limit = 5;
                
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.PINK);
                builder.setTitle("Top Overall Chatters:");
                StringBuilder string = new StringBuilder();
                Iterator<Entry<String, Long>> it = top.entrySet().iterator();
                for(int i = 0; i < limit; i++) {
                    if(it.hasNext()) {
                        Entry<String, Long> entry = it.next();
                        string.append("`" + (i+1) + "`. **[" + Levels.getLevel(Chip.getInstance().getBackend().getWordCount(event.getGuild().getId(), entry.getKey())) + "]**" + Chip.getInstance().getManager().getUserById(entry.getKey()).getAsMention() + " >> *" + entry.getValue() + " Words* \n\n");
                    }
                }
                builder.setDescription(string.toString());
                event.getChannel().sendMessage(builder.build()).queue();
                
                
            }
            
            if(message.startsWith(prefix + "chip") || message.startsWith(prefix + "level")) {
                String guild = event.getGuild().getId();
                String target = event.getMessage().getAuthor().getId();
                if(message.contains(" ")) {
                    if(event.getMessage().getMentionedUsers().size() > 0) {
                        target = event.getMessage().getMentionedUsers().get(0).getId();
                    } else {
                        //error no tagges member
                        event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(Emoji.CrossMark + " **Please tag the user!**").build()).queue();
                    }
                }
                
                Chip chip = Chip.getInstance();
                Backend backend = chip.getBackend();
                long words = backend.getWordCount(guild, target);
                long level = Levels.getLevel(words);
                
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.PINK);
                
                User targetUser = Chip.getInstance().getManager().getUserById(target);
                builder.setThumbnail(targetUser.getAvatarUrl());
                builder.setDescription(targetUser.getAsMention() + " **Chip Information.**");
                
                builder.addBlankField(false);
                builder.addField("Chips:", "`" + level + "`", true);
                builder.addField("Words Typed:", "`" + words + "` Words", true);
                builder.addField("Until Next chip:", "`" + Levels.untilNextLevel(words) + "` More Words", true);
                
                User author = event.getMessage().getAuthor();
                builder.setFooter("Requested By: " + author.getName() + "#" + author.getDiscriminator(), author.getAvatarUrl());
                
                event.getChannel().sendMessage(builder.build()).queue();
                
            }
            
        }
    }
    
}
