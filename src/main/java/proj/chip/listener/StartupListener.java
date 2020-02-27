package proj.chip.listener;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import proj.chip.Chip;

public class StartupListener extends ListenerAdapter {
    
    @Override
    public void onReady(ReadyEvent event) {
        Chip.getInstance().getBackend().synchronizeJDA(event.getJDA());
    }

}
