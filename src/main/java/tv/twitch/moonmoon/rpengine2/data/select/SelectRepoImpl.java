package tv.twitch.moonmoon.rpengine2.data.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.OptionArgs;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class SelectRepoImpl implements SelectRepo {

    private final SelectDbo selectDbo;
    private final AttributeRepo attributeRepo;
    private final Logger log;
    private Map<String, Select> selects;

    @Inject
    public SelectRepoImpl(
        SelectDbo selectDbo,
        AttributeRepo attributeRepo,
        @PluginLogger Logger log
    ) {
        this.selectDbo = Objects.requireNonNull(selectDbo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Set<Select> getSelects() {
        return Collections.unmodifiableSet(new HashSet<>(selects.values()));
    }

    @Override
    public Optional<Select> getSelect(String name) {
        return Optional.ofNullable(selects.get(name));
    }

    @Override
    public void createSelectAsync(String name, Callback<Void> callback) {
        if (selects.get(name) != null) {
            callback.accept(Result.error("Select already exists"));
            return;
        }
        selectDbo.insertSelectAsync(name, r -> callback.accept(handleCreateSelect(r)));
    }

    @Override
    public void createSelect(String name) {
        if (selects.get(name) != null) {
            return;
        }
        handleCreateSelect(selectDbo.insertSelect(name)).getError().ifPresent(log::warning);
    }

    @Override
    public void removeSelectAsync(String name, Callback<Void> callback) {
        Select select = selects.get(name);
        if (select == null) {
            callback.accept(Result.error("Select not found"));
            return;
        }

        int selectId = select.getId();

        if (attributeRepo.getAttribute(name).isPresent()) {
            String message =
                "Cannot remove select that is currently added to attributes " +
                    "(/rpengine attribute remove)";
            callback.accept(Result.error(message));
            return;
        }

        selectDbo.deleteSelectAsync(selectId, r -> {
            Optional<String> err = handleResult(() -> r).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                selects.remove(name);
                callback.accept(Result.ok(null));
            }
        });
    }

    @Override
    public void createOptionAsync(
        String selectName,
        String option,
        String display,
        ChatColor color,
        Callback<Void> callback
    ) {
        Result<OptionArgs> args = new OptionArgs(
            selectName, option, display, color, selects.get(selectName)
        ).clean();

        Optional<String> argsErr = args.getError();
        if (argsErr.isPresent()) {
            callback.accept(Result.error(argsErr.get()));
            return;
        }

        int selectId = args.get().getSelectId();

        selectDbo.insertOptionAsync(selectId, option, display, color, r ->
            callback.accept(handleCreateOption(selectId, r))
        );
    }

    @Override
    public void createOption(String selectName, String option, String display, ChatColor color) {
        Result<OptionArgs> args = new OptionArgs(
            selectName,
            option,
            display,
            color,
            selects.get(selectName)
        ).clean();

        Optional<String> argsErr = args.getError();
        if (argsErr.isPresent()) {
            log.warning(argsErr.get());
            return;
        }

        int selectId = args.get().getSelectId();

        handleCreateOption(selectId, selectDbo.insertOption(selectId, option, display, color))
            .getError().ifPresent(log::warning);
    }

    @Override
    public void removeOptionAsync(
        String selectName,
        String optionName,
        Callback<Void> callback
    ) {
        Select select = selects.get(selectName);
        if (select == null) {
            callback.accept(Result.error("Select not found"));
            return;
        }

        Optional<Option> option = select.getOption(optionName);
        if (!option.isPresent()) {
            callback.accept(Result.error("Option not found"));
            return;
        }

        if (attributeRepo.getAttribute(selectName).isPresent()) {
            String message =
                "Cannot remove an option on a select that is currently added to attributes";
            callback.accept(Result.error(message));
            return;
        }

        selectDbo.deleteOptionAsync(option.get().getId(), r -> {
            Optional<String> err = handleResult(() -> r).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                callback.accept(reloadSelect(select.getId()));
            }
        });
    }

    private Result<Void> handleCreateOption(int selectId, Result<Void> r) {
        return handleResult(() -> r).getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> reloadSelect(selectId).getError()
                .<Result<Void>>map(Result::error)
                .orElseGet(() -> Result.ok(null))
            );
    }

    private Result<Void> reloadSelect(int selectId) {
        Result<Select> updatedSelect = handleResult(() -> selectDbo.selectSelect(selectId));

        Optional<String> err = updatedSelect.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        Select s = updatedSelect.get();
        selects.put(s.getName(), s);

        return Result.ok(null);
    }

    @Override
    public Result<Void> load() {
        Result<Set<Select>> r = selectDbo.selectSelects();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            selects = Collections.synchronizedMap(r.get().stream()
                .collect(Collectors.toMap(Select::getName, Function.identity())));
            return Result.ok(null);
        }
    }

    private Result<Void> handleCreateSelect(Result<Long> r) {
        Result<Long> result = handleResult(() -> r);

        Optional<String> err = result.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        long selectId = result.get();

        if (selectId == 0) {
            // already exists
            return Result.ok(null);
        } else {
            return reloadSelect((int) selectId);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
