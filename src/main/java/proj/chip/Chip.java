package proj.chip;

import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import proj.api.marble.builders.config.Config;
import proj.api.marble.builders.config.ConfigType;
import proj.chip.backend.Backend;
import proj.chip.listener.CommandListener;
import proj.chip.listener.GuildJoin;
import proj.chip.listener.GuildLeave;
import proj.chip.listener.MemberLeave;
import proj.chip.listener.StartupListener;
import proj.chip.listener.WordCounter;
import proj.chip.utility.Output;

public class Chip {

    @Getter
    private static Chip instance;

    @Getter
    private static Config config;

    @Getter
    private ShardManager manager;

    @Getter
    private Backend backend;
    
    @Getter
    private String prefix = "~";

    public static void main(String[] args) throws LoginException, InterruptedException {
        
        Output.print("Retrieving bot token.");

        /**
         * Start via configuration file.
         */
        config = Config.builder().configType(ConfigType.YAML).fileName("ChipConfig.yml").updating(true).build();
        instance = new Chip();
        instance.init((String) config.get("Token", "Bot-Token-Here"));
        return;
    }

    public void init(String token) {

        Output.print("Retrieving database information.");

        String ip = config.get("SQL.IP", "localhost").toString();
        String port = config.get("SQL.Port", "3306").toString();
        String database = config.get("SQL.Database", "chip").toString();
        String username = config.get("SQL.Username", "root").toString();
        String password = config.get("SQL.Password", "password").toString();

        Output.print("Initializing database backend.");

        Backend sql = new Backend(ip, port, database, username, password);
        if (!sql.initialize()) {
            Output.fatal("Unable to connect to MySQL backend.");
            return;
        }
        this.backend = sql;

        Output.success("Database backend initialized.");
        Output.print("Attempting login.");

        /**
         * If unable to launch bot, return error and exit program.
         */
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(token);
            builder.addEventListeners(new CommandListener());
            builder.addEventListeners(new GuildJoin());
            builder.addEventListeners(new GuildLeave());
            builder.addEventListeners(new MemberLeave());
            builder.addEventListeners(new StartupListener());
            builder.addEventListeners(new WordCounter());
            manager = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            Output.fatal("There was an error logging into discord servers.");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Output.fatal("The bot token provided was invalid.");
            return;
        }
        
        Output.success("Bot startup completed.");

    }

}
