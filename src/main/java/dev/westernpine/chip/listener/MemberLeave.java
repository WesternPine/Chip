package dev.westernpine.chip.listener;

import dev.westernpine.chip.Chip;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberLeave extends ListenerAdapter {
    
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Chip.getInstance().getBackend().deleteUser(event.getGuild().getId(), event.getUser().getId());
    }

}
