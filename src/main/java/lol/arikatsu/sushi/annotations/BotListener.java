package lol.arikatsu.sushi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A command that should be registered on start-up.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BotListener {
}