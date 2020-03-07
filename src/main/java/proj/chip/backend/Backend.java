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
        dbUsers.remove("bot");
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
        Optional<Object> users = sql.query(rs -> {
            HashMap<String, Long> result = new HashMap<>();
            try {
                while(rs.next()) {
                    String user = rs.getString("user");
                    long amt = rs.getLong("words");
                    if(user!="bot")
                        result.put(user, amt);
                }
            } catch (Exception e) {}
            return result;
        }, "SELECT * FROM `" + guild + "`;");
        return (HashMap<String, Long>) users.get();
    }
    
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Long> getUsersTop(String guild, int top){
        Optional<Object> users = sql.query(rs -> {
            LinkedHashMap<String, Long> result = new LinkedHashMap<>();
            try {
                while(rs.next()) {
                    if(result.size() >= top) {
                        System.out.println(result.size() + " >= " + top);
                        break;
                    }
                    
                    String user = rs.getString("user");
                    long amt = rs.getLong("words");
                    if(!user.equals("bot"))
                        result.put(user, amt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }, "SELECT user, words FROM `" + guild + "` ORDER BY words DESC LIMIT " + (top+1) + ";");
        return (LinkedHashMap<String, Long>) users.get();
    }
    
    public void setOutput(String guild, Optional<Long> channel) {
        sql.update("DELETE FROM `" + guild + "` WHERE user=?;", "bot");
        if(channel.isPresent())
            sql.update("INSERT INTO `" + guild + "` VALUES (?,?);", "bot", channel.get());
    }
    
    public Optional<Long> getOutput(String guild){
        Optional<Object> channel = sql.query(rs -> {
            try {
                if(rs.next()) {
                    return rs.getLong("words");
                }
            } catch (Exception e) {}
            return null;
        }, "SELECT * FROM `" + guild + "` WHERE user=?;", "bot");
        return channel.isPresent() ? Optional.of((Long) channel.get()) : Optional.empty();
    }

}
