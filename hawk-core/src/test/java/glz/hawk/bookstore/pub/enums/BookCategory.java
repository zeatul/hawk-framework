package glz.hawk.bookstore.pub.enums;

import glz.hawkframework.core.helper.MapHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.StateSupport.stateNotNull;

public enum BookCategory {
    /**
     * Biology
     */
    BIOLOGY("biology"),
    /**
     * Chemistry
     */
    CHEMISTRY("chemistry"),
    /**
     * History
     */
    HISTORY("history");


    final public String value;

    private final static Map<String, BookCategory> valueMap = MapHelper.<String, BookCategory>builder()
        .put(BIOLOGY.value, BIOLOGY)
        .put(CHEMISTRY.value, CHEMISTRY)
        .put(HISTORY.value, HISTORY)
    .buildHashMap();

    private final static Map<String, BookCategory> nameMap = MapHelper.<String, BookCategory>builder()
        .put(BIOLOGY.name(), BIOLOGY)
        .put(CHEMISTRY.name(), CHEMISTRY)
        .put(HISTORY.name(), HISTORY)
    .buildHashMap();

    BookCategory(String value) {
        this.value = value;
    }

    @Nullable
    public static BookCategory fromValue(@Nullable String value) {
        if (value == null) return null;
        return valueMap.get(value);
    }

    @Nullable
    public static BookCategory fromName(@Nullable String name) {
        if (name == null) return null;
        return nameMap.get(name);
    }

    @Nonnull
    public static BookCategory parseByValue(@Nonnull String value) {
        return stateNotNull(fromValue(argNotNull(value)), () -> String.format("Found no BookCategory with value %s", value));
    }

    @Nonnull
    public static BookCategory parseByName(@Nonnull String name) {
        return stateNotNull(fromName(argNotNull(name)), () -> String.format("Found no BookCategory named %s", name));
    }
}
