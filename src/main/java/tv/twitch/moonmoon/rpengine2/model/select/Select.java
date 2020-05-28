package tv.twitch.moonmoon.rpengine2.model.select;

import tv.twitch.moonmoon.rpengine2.model.Model;

import java.util.Optional;
import java.util.Set;

public interface Select extends Model {
    /**
     * Returns this select's unique name
     *
     * @return Name
     */
    String getName();

    /**
     * Returns a set of this select's {@link Option}s.
     *
     * @return Select options
     */
    Set<Option> getOptions();

    /**
     * Returns the option in this select with the specified name
     *
     * @param name Option name
     * @return Option or empty if not found
     */
    Optional<Option> getOption(String name);

    /**
     * Returns the option in this select with the specified ID
     *
     * @param id Option ID
     * @return Option or empty if not found
     */
    Optional<Option> getOption(int id);
}
