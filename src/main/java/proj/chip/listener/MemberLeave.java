package proj.chip.listener;

import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import proj.chip.Chip;

public class MemberLeave extends ListenerAdapter {
    
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        Chip.getInstance().getBackend().deleteUser(event.getGuild().getId(), event.getUser().getId());
    }

}
