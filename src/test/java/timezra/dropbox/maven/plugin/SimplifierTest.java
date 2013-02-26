package timezra.dropbox.maven.plugin;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class SimplifierTest {

    @Test
    public void should_simplify_null() throws IllegalAccessException {
        assertThat(Simplifier.simplify(null), nullValue());
    }

    @Test
    public void should_simplify_numbers() throws IllegalAccessException {
        final Integer zero = Integer.valueOf(0);
        assertThat(Simplifier.<Integer> simplify(zero), sameInstance(zero));
    }

    @Test
    public void should_simplify_booleans() throws IllegalAccessException {
        final Boolean aBoolean = Boolean.TRUE;
        assertThat(Simplifier.<Boolean> simplify(aBoolean), sameInstance(aBoolean));
    }

    @Test
    public void should_simplify_strings() throws IllegalAccessException {
        final String aString = "a string";
        assertThat(Simplifier.<String> simplify(aString), sameInstance(aString));
    }

    @Test
    public void should_simplify_characters() throws IllegalAccessException {
        final char aCharacter = 'a';
        assertThat(Simplifier.<Character> simplify('a'), equalTo(aCharacter));
    }

    @Test
    public void should_simplify_collections() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);

        final Collection<? super Object> simplified = Simplifier
                .<Collection<? super Object>> simplify(asList(aString, zero));

        assertThat(simplified, hasItem(aString));
        assertThat(simplified, hasItem(zero));
        assertThat(simplified.size(), is(2));
    }

    @Test
    public void should_simplify_maps() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);
        final Map<Object, Object> toSimplify = new HashMap<>();
        toSimplify.put("key1", aString);
        toSimplify.put(2, zero);

        final Map<? super Object, ? super Object> simplified = Simplifier
                .<Map<? super Object, ? super Object>> simplify(toSimplify);

        assertThat((String) simplified.get("key1"), is(aString));
        assertThat((Integer) simplified.get(2), is(zero));
        assertThat(simplified.size(), is(2));
    }

    @Test
    public void should_simplify_arrays() throws IllegalAccessException {
        final Collection<? super Object> simplified = Simplifier.<Collection<? super Object>> simplify(new int[] { 1, 2 });

        final Iterator<? super Object> iterator = simplified.iterator();
        assertThat((Integer) iterator.next(), equalTo(1));
        assertThat((Integer) iterator.next(), equalTo(2));
        assertThat(simplified.size(), is(2));
    }

    @Test
    public void should_simplify_structs() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);

        final Map<? super Object, ? super Object> simplified = Simplifier
                .<Map<? super Object, ? super Object>> simplify(new Struct(aString, zero));

        assertThat((String) simplified.get("field1"), is(aString));
        assertThat((Integer) simplified.get("field2"), is(zero));
        assertThat(simplified.size(), is(2));
    }

    @Test
    public void should_simplify_collections_of_structs() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);

        final Collection<Map<? super Object, ? super Object>> simplified = Simplifier
                .<Collection<Map<? super Object, ? super Object>>> simplify(asList(new Struct(aString, zero)));

        assertThat(simplified.size(), is(1));
        final Map<? super Object, ? super Object> simplifiedStruct = simplified.iterator().next();
        assertThat((String) simplifiedStruct.get("field1"), is(aString));
        assertThat((Integer) simplifiedStruct.get("field2"), is(zero));
    }

    @Test
    public void should_simplify_arrays_of_structs() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);

        final Collection<Map<? super Object, ? super Object>> simplified = Simplifier
                .<Collection<Map<? super Object, ? super Object>>> simplify(new Struct[] { new Struct(aString, zero) });

        assertThat(simplified.size(), is(1));
        final Map<? super Object, ? super Object> simplifiedStruct = simplified.iterator().next();
        assertThat((String) simplifiedStruct.get("field1"), is(aString));
        assertThat((Integer) simplifiedStruct.get("field2"), is(zero));
    }

    @Test
    public void should_simplify_maps_of_structs() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);
        final Map<Object, Object> toSimplify = new LinkedHashMap<>();
        toSimplify.put(new Struct(aString, zero), 1);
        toSimplify.put(2, new Struct(zero, aString));

        final Map<? super Object, ? super Object> simplified = Simplifier
                .<Map<? super Object, ? super Object>> simplify(toSimplify);

        final Iterator<?> entries = simplified.entrySet().iterator();
        @SuppressWarnings("unchecked")
        final Entry<Map<? super Object, ? super Object>, Integer> first = (Entry<Map<? super Object, ? super Object>, Integer>) entries
                .next();
        @SuppressWarnings("unchecked")
        final Entry<Integer, Map<? super Object, ? super Object>> second = (Entry<Integer, Map<? super Object, ? super Object>>) entries
                .next();
        assertThat((String) first.getKey().get("field1"), is(aString));
        assertThat((Integer) first.getKey().get("field2"), is(zero));
        assertThat(first.getValue(), equalTo(1));
        assertThat((Integer) second.getValue().get("field1"), is(zero));
        assertThat((String) second.getValue().get("field2"), is(aString));
        assertThat(second.getKey(), equalTo(2));

        assertThat(simplified.size(), is(2));
    }

    @Test
    public void should_simplify_nested_structs() throws IllegalAccessException {
        final String aString = "a string";
        final Integer zero = Integer.valueOf(0);

        final Map<? super Object, ? super Object> simplified = Simplifier
                .<Map<? super Object, ? super Object>> simplify(new Struct(new Struct(aString, zero), new Struct(zero,
                        aString)));

        assertThat(simplified.size(), is(2));
        @SuppressWarnings("unchecked")
        final Map<? super Object, ? super Object> nestedStruct1 = (Map<? super Object, ? super Object>) simplified
                .get("field1");
        @SuppressWarnings("unchecked")
        final Map<? super Object, ? super Object> nestedStruct2 = (Map<? super Object, ? super Object>) simplified
                .get("field2");

        assertThat((String) nestedStruct1.get("field1"), is(aString));
        assertThat((Integer) nestedStruct1.get("field2"), is(zero));
        assertThat((Integer) nestedStruct2.get("field1"), is(zero));
        assertThat((String) nestedStruct2.get("field2"), is(aString));
    }

    static final class Struct {
        public final Object field1;
        public final Object field2;

        Struct(final Object field1, final Object field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }
}
