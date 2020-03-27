package proj.chip.listener;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.chip.Chip;

public class GuildJoin extends ListenerAdapter {
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Chip.getInstance().getBackend().deleteGuild(event.getGuild().getId());
        Chip.getInstance().getBackend().addGuild(event.getGuild().getId());
    }
    
}
