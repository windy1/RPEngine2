package tv.twitch.moonmoon.rpengine2.model.select.impl;

import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A user-defined select allows player to choose a value for an attribute based on a pre-determined
 * set of choices.
 */
public class DefaultSelect implements Select {

    private final int id;
    private final Instant created;
    private final String name;
    private final Map<String, Option> options;
    private final Map<Integer, Option> optionIdMap;

    public DefaultSelect(int id, Instant created, String name, Set<Option> options) {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<>(options.values()));
    }

    @Override
    public Optional<Option> getOption(String name) {
        return Optional.ofNullable(options.get(name));
    }

    @Override
    public Optional<Option> getOption(int id) {
        return Optional.ofNullable(optionIdMap.get(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultSelect select = (DefaultSelect) o;
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
