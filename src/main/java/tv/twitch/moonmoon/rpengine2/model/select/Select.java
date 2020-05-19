package tv.twitch.moonmoon.rpengine2.model.select;

import tv.twitch.moonmoon.rpengine2.model.Model;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A user-defined select allows player to choose a value for an attribute based on a pre-determined
 * set of choices.
 */
public class Select implements Model {

    private final int id;
    private final Instant created;
    private final String name;
    private final Map<String, Option> options;
    private final Map<Integer, Option> optionIdMap;

    public Select(int id, Instant created, String name, Set<Option> options) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.name = Objects.requireNonNull(name);
        this.options = Objects.requireNonNull(options).stream()
            .collect(Collectors.toMap(Option::getName, Function.identity()));
        optionIdMap = options.stream()
            .collect(Collectors.toMap(Option::getId, Function.identity()));
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    /**
     * Returns this select's unique name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a set of this select's {@link Option}s.
     *
     * @return Select options
     */
    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<>(options.values()));
    }

    /**
     * Returns the option in this select with the specified name
     *
     * @param name Option name
     * @return Option or empty if not found
     */
    public Optional<Option> getOption(String name) {
        return Optional.ofNullable(options.get(name));
    }

    /**
     * Returns the option in this select with the specified ID
     *
     * @param id Option ID
     * @return Option or empty if not found
     */
    public Optional<Option> getOption(int id) {
        return Optional.ofNullable(optionIdMap.get(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Select select = (Select) o;
        return Objects.equals(name, select.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Select{" +
            "id=" + id +
            ", created=" + created +
            ", name='" + name + '\'' +
            ", options=" + options +
            '}';
    }
}
