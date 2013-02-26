package timezra.dropbox.maven.plugin;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Simplifier {

    private static Collection<?> simplify(final Collection<?> c) throws IllegalAccessException {
        final Collection<Object> simpleTypes = new ArrayList<>();
        for (final Object o : (Collection<?>) c) {
            simpleTypes.add(simplify(o));
        }
        return simpleTypes;
    }

    private static Collection<?> simplifyArray(final Object array) throws IllegalAccessException {
        final int length = Array.getLength(array);
        final ArrayList<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(array, i));
        }
        return simplify(list);
    }

    private static Map<?, ?> simplify(final Map<?, ?> m) throws IllegalAccessException {
        final Map<Object, Object> simpleMap = new LinkedHashMap<>();
        for (final java.util.Map.Entry<?, ?> e : ((Map<?, ?>) m).entrySet()) {
            simpleMap.put(simplify(e.getKey()), simplify(e.getValue()));
        }
        return simpleMap;
    }

    private static Map<?, ?> simplifyStruct(final Object struct) throws IllegalAccessException {
        final Map<Object, Object> simpleMap = new LinkedHashMap<>();
        final List<Field> fields = new ArrayList<>(asList(struct.getClass().getFields()));
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(final Field f1, final Field f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
        for (final Field f : fields) {
            final String name = f.getName();
            final Object value = f.get(struct);
            simpleMap.put(name, simplify(value));
        }
        return simpleMap;
    }

    @SuppressWarnings("unchecked")
    static <T> T simplify(final Object o) throws IllegalAccessException {
        if (o == null || o instanceof Number || o instanceof Boolean || o instanceof Character || o instanceof String) {
            return (T) o;
        } else if (o instanceof Collection) {
            return (T) simplify((Collection<?>) o);
        } else if (o instanceof Map) {
            return (T) simplify((Map<?, ?>) o);
        } else if (o.getClass().isArray()) {
            return (T) simplifyArray(o);
        } else {
            return (T) simplifyStruct(o);
        }
    }
}