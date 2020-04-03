package dev.westernpine.listener;

import dev.westernpine.Chip;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Chip.getInstance().getBackend().deleteGuild(event.getGuild().getId());
    }
    
}
