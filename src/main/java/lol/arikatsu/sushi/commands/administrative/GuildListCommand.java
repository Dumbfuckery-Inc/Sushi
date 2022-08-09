package lol.arikatsu.sushi.commands.administrative;

import lol.arikatsu.sushi.Sushi;
import lol.arikatsu.sushi.annotations.BotCommand;
import lol.arikatsu.sushi.enums.EmbedType;
import lol.arikatsu.sushi.utils.CommandUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tech.xigam.cch.command.Callable;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Callback;
import tech.xigam.cch.utils.Interaction;

import java.util.LinkedList;
import java.util.List;

@BotCommand
public final class GuildListCommand extends Command implements Callable {
    public GuildListCommand() {
        super("guildlist", "Lists all the guilds the bot is in.");
    }

    @Override public void execute(Interaction interaction) {
        // Check if the executor is authorized.
        if(!CommandUtils.isAdministrator(interaction)) return;

        // Get the guilds.
        var guilds = Sushi.getJdaInstance().getGuilds();
        guilds = guilds.subList(0, Math.min(10, guilds.size()));
        // Create required buttons.
        if(guilds.size() > 10)
            interaction.addButtons(Button.primary("2", "Next Page"));

        // Reply with the guilds.
        interaction.reply(this.makeEmbed(guilds, 1));
    }

    @Override public void callback(Callback callback) {
        // Get the current page.
        var page = Integer.parseInt(callback.getReference());
        // Get the guilds.
        var startIndex = 10 * page;
        var guilds = Sushi.getJdaInstance().getGuilds()
            .subList(startIndex, Math.min(startIndex, 10 * (page + 1)));

        // Create required buttons.
        var buttons = new LinkedList<Button>();

        // Check if there are more pages.
        if(page < Math.ceil(Sushi.getJdaInstance().getGuilds().size() / 10.0))
            // Add the next page button.
            buttons.add(Button.primary(String.valueOf(page + 1), "Next Page"));

        // Check if there are previous pages.
        if(page > 1)
            // Add the previous page button.
            buttons.add(Button.primary(String.valueOf(page - 1), "Previous Page"));

        // Reply with the guilds.
        callback.edit(buttons.toArray(new Button[0]))
            .edit(this.makeEmbed(guilds, page));
    }

    /**
     * Generates an embed from a collection of guilds.
     * @param guilds The collection of guilds.
     * @return The generated embed.
     */
    private MessageEmbed makeEmbed(List<Guild> guilds, int page) {
        var description = new StringBuilder();
        for(int i = 0; i < Math.min(guilds.size(), 9); i++) {
            var guild = guilds.get(i);
            description.append("**%s.** `%s` | %s Members (`%s`)\n"
                .formatted(i + 1, guild.getName(), guild.getMemberCount(), guild.getId()));
        }

        var guildsTotal = Sushi.getJdaInstance().getGuilds().size();
        return new EmbedBuilder()
            .setColor(EmbedType.SIMPLE.getEmbedColor())
            .setTitle("Total Servers: " + guildsTotal)
            .setFooter("Page: %s/%s".formatted(page, (int) Math.ceil(guildsTotal / 10.0)))
            .setDescription(description).build();
    }
}
