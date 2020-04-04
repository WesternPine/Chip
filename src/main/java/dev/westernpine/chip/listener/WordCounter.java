package dev.westernpine.chip.listener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

import dev.westernpine.chip.Chip;
import dev.westernpine.chip.utility.Levels;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proj.api.marble.lib.emoji.Emoji;

public class WordCounter extends ListenerAdapter {

    @Override
    @SneakyThrows
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        
        if(event.getAuthor().isBot())
            return;
        
        String content = event.getMessage().getContentStripped();
        long count = content.contains(" ") ? content.split(" ").length : 1;
        long newTotal = Chip.getInstance().getBackend().addWordCount(event.getGuild().getId(), event.getAuthor().getId(), count);
        long previousTotal = newTotal - count;
        
        long newLevel = Levels.getLevel(newTotal);
        long oldLevel = Levels.getLevel(previousTotal);
        
        if(oldLevel == newLevel)
            return;
        
        //Level Up

        final BufferedImage buffer = ImageIO.read(getClass().getResource("/Bag_Of_Friendchips.png"));
        Graphics g = buffer.getGraphics();
        
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform orig = g2.getTransform();
        g2.rotate(Math.toRadians(35));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        float[] color1 = Color.RGBtoHSB(255, 255, 210, new float[3]);
        g2.setColor(Color.getHSBColor(color1[0], color1[1], color1[2]));
        g2.drawString("Level Up!", 370, 130);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(370, 135, 545, 135);
        int x = 430;
        int y = 245;
        if(newLevel > 9) {x = 410; y = 250;}
        if (newLevel > 99) { x = 385; y = 255;}
        if (newLevel > 999) { x = 380;y = 260;}
        if(newLevel >= 10000){x = 365;y = 265;}
        g2.setFont(new Font("Segoe Script", Font.BOLD, 70));
        g2.drawString(newLevel + "", x, y);
        g2.setTransform(orig);
        
        Graphics2D g3 = (Graphics2D)g;
        AffineTransform orig2 = g3.getTransform();
        g3.rotate(Math.toRadians(-20));
        g3.setFont(new Font("Segoe Script", Font.ITALIC, 40));
        g3.setColor(Color.WHITE);
        g3.drawString("Yummy!", 100, 700);
        g3.setTransform(orig2);
        
        Graphics2D g4 = (Graphics2D)g;
        AffineTransform orig3 = g4.getTransform();
        g4.rotate(Math.toRadians(0));
        g4.setFont(new Font("Segoe Script", Font.PLAIN, 40));
        g4.setColor(Color.WHITE);
        g4.drawString("Kettle Cooked Chips!", 50, 200);
        g4.setTransform(orig3);
        
        g4.dispose();
        g3.dispose();
        g2.dispose();
        g.dispose();
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(buffer, "png", output);
        
        Optional<Long> toSend = Chip.getInstance().getBackend().getOutput(event.getGuild().getId());
        TextChannel ch = toSend.isPresent() ? Chip.getInstance().getManager().getTextChannelById(toSend.get()) : event.getChannel();
        boolean any = toSend.isPresent() ? ch != null ? false : true : true;
        
        (any ? event.getChannel() : ch).sendFile(output.toByteArray(), "FriendChipLevel.png").complete();
        (any ? event.getChannel() : ch).sendMessage(new MessageBuilder(Emoji.Tada + " " + event.getAuthor().getAsMention() + " Leveled Up!").build()).complete();
        
    }
    
}
