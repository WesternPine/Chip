package proj.chip.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.chip.Chip;

public class MemberLeave extends ListenerAdapter {
    
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Chip.getInstance().getBackend().deleteUser(event.getGuild().getId(), event.getUser().getId());
    }

}
