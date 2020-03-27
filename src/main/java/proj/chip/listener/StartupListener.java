package proj.chip.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.chip.Chip;

public class StartupListener extends ListenerAdapter {
    
    @Override
    public void onReady(ReadyEvent event) {
        Chip.getInstance().getBackend().synchronizeJDA(event.getJDA());
    }

}
