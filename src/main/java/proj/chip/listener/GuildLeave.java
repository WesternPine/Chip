package proj.chip.listener;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.chip.Chip;

public class GuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Chip.getInstance().getBackend().deleteGuild(event.getGuild().getId());
    }
    
}
