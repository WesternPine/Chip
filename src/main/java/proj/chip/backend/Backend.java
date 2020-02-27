package proj.chip.backend;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

import net.dv8tion.jda.core.JDA;
import proj.api.marble.builders.sql.DatabaseType;
import proj.api.marble.builders.sql.SQL;
import proj.chip.Chip;

public class Backend {

    private String ip;
    private String port;
    private String database;
    private String username;
    private String password;

    private SQL sql;

    public Backend(String ip, String port, String database, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public boolean initialize() {
        this.sql = SQL.getBuilder().setIp(ip).setPort(port).setDatabase(database).setUsername(username)
                .setPassword(password).setDatabaseType(DatabaseType.MYSQL).build();
        
        return this.sql.getConnection().open() != null;
    }
    
    public void synchronizeJDA(JDA jda) {
        jda.getGuilds().forEach(guild -> {
            addGuild(guild.getId());
            synchronizeUsers(guild.getId());
        });
    }
    
    public void synchronizeUsers(String guild) {
        Set<String> dbUsers = getUsers(guild).keySet();
        Chip.getInstance().getManager().getGuildById(guild).getMembers().forEach(member -> dbUsers.remove(member.getUser().getId()));
        dbUsers.forEach(user -> deleteUser(guild, user));
    }
    
    public void deleteUser(String guild, String user) {
        sql.update("DELETE FROM `" + guild + "` WHERE user=?;", user);
    }
    
    public void addGuild(String guild) {
        sql.update("CREATE TABLE IF NOT EXISTS `" + guild + "` (`user` varchar(255) NOT NULL, `words` BIGINT(200) NOT NULL, PRIMARY KEY (user)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }
    
    public void deleteGuild(String guild) {
        sql.update("DROP TABLE `" + guild + "`;");
    }
    
    public long getWordCount(String guild, String user) {
        Optional<Object> wordCount = sql.query(rs -> {
            try {
                if(rs.next())
                    return rs.getLong("words");
            } catch (Exception e) {}
            return 0L;
        }, "SELECT * FROM `" + guild + "` WHERE user=?;", user);
        return (long) wordCount.get();
    }
    
    public long addWordCount(String guild, String user, long words) {
        long previousWords = getWordCount(guild, user);
        long newAmount = previousWords + words;
        sql.update("DELETE FROM `" + guild + "` WHERE user=?;", user);
        sql.update("INSERT INTO `" + guild + "` VALUES (?,?);", user, newAmount);
        return newAmount;
    }
    
    @SuppressWarnings("unchecked")
    public HashMap<String, Long> getUsers(String guild){
//        synchronizeUsers(guild);
        Optional<Object> users = sql.query(rs -> {
            HashMap<String, Long> result = new HashMap<>();
            try {
                while(rs.next())
                    result.put(rs.getString("user"), rs.getLong("words"));
            } catch (Exception e) {}
            return result;
        }, "SELECT * FROM `" + guild + "`;");
        return (HashMap<String, Long>) users.get();
    }
    
    public LinkedHashMap<String, Long> getTopUsers(String guild) {
        HashMap<String, Long> users = getUsers(guild);
        LinkedHashMap<String, Long> top = new LinkedHashMap<>();
        String most = "";
        long max = 0;
        for(String user : users.keySet()) {
            long sent = users.get(user);
            if(sent >= max) {
                most = user;
                max = sent;
            }
        }
        if(most.equals(""))
            return top;
        for(long i = max; top.size() < users.size(); i--) {
            for(String user : users.keySet()) {
                if(users.get(user) == i) {
                    top.put(user, i);
                }
            }
        }
        return top;
    }

}
