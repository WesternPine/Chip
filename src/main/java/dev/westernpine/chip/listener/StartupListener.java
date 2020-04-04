package dev.westernpine.chip.listener;

import dev.westernpine.chip.Chip;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StartupListener extends ListenerAdapter {
    
    @Override
    public void onReady(ReadyEvent event) {
        Chip.getInstance().getBackend().synchronizeJDA(event.getJDA());
    }

}
