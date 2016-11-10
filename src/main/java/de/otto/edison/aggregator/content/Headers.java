package de.otto.edison.aggregator.content;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.copyOf;

/**
 * Meta-information about {@link Content} items.
 * <p>
 *     The keys used to select header values are case-insensitive.
 * </p>
 */
public final class Headers {
    private final ImmutableMap<String,List<Object>> headers;

    public Headers(final Map<String, List<Object>> headers) {
        this.headers = lowerCaseKeysOf(headers);
    }

    public <T> Optional<T> getValue(final String key, final Class<T> asType) {
        return Optional.ofNullable(
                getValues(key, asType).isEmpty()
                        ? null
                        : getValues(key, asType).iterator().next());
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableCollection<T> getValues(final String key, final Class<T> asType) {
        final String caseInsensitiveKey = key.toLowerCase();
        final ImmutableCollection<T> values = (ImmutableCollection<T>) headers.get(caseInsensitiveKey);
        return values == null || values.isEmpty()
                        ? ImmutableList.of()
                        : values;
    }

    private ImmutableMap<String, List<Object>> lowerCaseKeysOf(final Map<String, List<Object>> headers) {
        final ImmutableMap.Builder<String, List<Object>> builder = ImmutableMap.builder();
        headers.entrySet().forEach(entry -> {
            List<Object> values = entry.getValue();
            builder.put(entry.getKey().toLowerCase(), copyOf(values));
        });
        return builder.build();
    }
}
