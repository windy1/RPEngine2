package tv.twitch.moonmoon.rpengine2.model.select;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Select {

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

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<>(options.values()));
    }

    public Optional<Option> getOption(String name) {
        return Optional.ofNullable(options.get(name));
    }

    public Optional<Option> getOption(int id) {
        return Optional.ofNullable(optionIdMap.get(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Select select = (Select) o;
        return id == select.id &&
            Objects.equals(created, select.created) &&
            Objects.equals(name, select.name) &&
            Objects.equals(options, select.options);
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
