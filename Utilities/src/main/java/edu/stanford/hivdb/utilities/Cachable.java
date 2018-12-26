/*

    Copyright (C) 2017 Stanford HIVDB team

    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.utilities;

import java.util.List;
import java.util.function.Function;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.commons.io.IOUtils;

public class Cachable {

	static final String STATIC_CACHE_TPL = "__cached_classes/%s/%s.json";
	static final String RESOURCES_PATH = "src/main/resources";
	static final String CACHABLE_PROPERTY = "hivdb.updateCachable";
	static boolean forceUpdate = false;

	private final Class<?> cls;
	private final Runnable _loadStatic;

	private final List<Field> cachableStaticFields;
	private final List<DataLoader<?>> dataLoaders;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface CachableField {};

	public static interface DataLoader<T> {
		public String getFieldName();
		public T load() throws Throwable;
	}

	public static final Cachable setup(final Class<?> cls, final Runnable loadData) {
		Cachable cachable = new Cachable(cls, loadData);
		cachable.loadStatic();
		return cachable;
	}

	public static final Cachable setup(final Class<?> cls) {
		return setup(cls, null);
	}

	/**
	 * An entry point used to force update all cached static variables.
	 *
	 * If this method is called with a system property
	 * "hivdb.forceUpdateCache", all specified cachable classes will be updated
	 */
	public static final void main(String[] args) {
		String cachedClasses = System.getProperty(CACHABLE_PROPERTY);
		if (cachedClasses != null) {
			updateCachedClasses(cachedClasses);
		}
	}

	public static final void updateCachedClasses(String cachedClasses) {
		forceUpdate = true;
		Arrays.asList(cachedClasses.split("[\\s,;]+"))
			.stream()
			.forEach(s -> {
				if (s.length() == 0) {
					return;
				}
				try {
					Class<?> klass = Class.forName(s);
					Class.forName(s, true, klass.getClassLoader());
				} catch (ClassNotFoundException e) {
					throw new ExceptionInInitializerError(e);
				}
				System.out.println("Cache refreshed: " + s);
			});
	}

	public Cachable(final Class<?> cls, final Runnable loadStatic) {
		this.cls = cls;
		this._loadStatic = loadStatic;
		this.cachableStaticFields = getCachableStaticFields();
		this.dataLoaders = getDataLoaders();
	}

	public Cachable(final Class<?> cls) {
		this(cls, null);
	}

	private String staticCachePath(Field field) {
		return String.format(STATIC_CACHE_TPL, cls.getCanonicalName(), field.getName());
	}

	/**
	 * This method should be called in class initializer (static block).
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void loadStatic() {
		if (forceUpdate || !loadStaticCache()) {
			loadStaticFromLoaders();
			saveStaticCache();
		}
	}

	/**
	 * Find all cachable static fields of given class.
	 *
	 * This method scans all fields of a class, returns static fields which are
	 * annotated by @CachableField.
	 */
	private final List<Field> getCachableStaticFields() {
		List<Field> allFields = Arrays.asList(cls.getDeclaredFields());
		return allFields.stream().filter(f ->
			f.isAnnotationPresent(CachableField.class) &&
		   	Modifier.isStatic(f.getModifiers())
		).collect(Collectors.toList());
	}

	private final List<DataLoader<?>> getDataLoaders() {
		List<Class<?>> allClasses = Arrays.asList(cls.getClasses());
		return allClasses.stream().filter(c ->
			DataLoader.class.isAssignableFrom(c) &&
			Modifier.isStatic(c.getModifiers())
		)
		.map(new Function<Class<?>, DataLoader<?>>() {
			@Override
			public DataLoader<?> apply(Class<?> c) {
				try {
					return (DataLoader<?>) c.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		})
		.collect(Collectors.toList());
	}

	private void loadStaticFromLoaders() {
		if (_loadStatic != null) {
			_loadStatic.run();
		}
		for (DataLoader<?> dl : dataLoaders) {
			Object obj = null;
			try {
				obj = dl.load();
			} catch (Throwable e) {
				throw new ExceptionInInitializerError(e);
			}
			try {
				Field field = cls.getDeclaredField(dl.getFieldName());
				boolean isAccessible = field.isAccessible();
				if (!isAccessible) { field.setAccessible(true); }
				field.set(null, obj);
				if (!isAccessible) { field.setAccessible(false); }
			} catch (NoSuchFieldException|IllegalAccessException|IllegalArgumentException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	private boolean loadStaticCache() {
		return cachableStaticFields
			.stream()
			.map(f -> {
				boolean success = false;
				try (
					InputStream stream = cls
						.getClassLoader().getResourceAsStream(staticCachePath(f));
				) {
					String raw = IOUtils.toString(stream, StandardCharsets.UTF_8);
					boolean isAccessible = f.isAccessible();
					if (!isAccessible) { f.setAccessible(true); }
					Object obj = Json.loads(raw, f.getGenericType());
					f.set(null, obj);
					if (!isAccessible) { f.setAccessible(false); }
					success = true;
				} catch (IOException|NullPointerException e) {
					// expected, fallback to loadData
					success = false;
				} catch (IllegalAccessException e) {
					throw new ExceptionInInitializerError(e);
				}
				return success;
			})
			.allMatch(e -> e);
	}

	/**
	 * Save all cachable static fields to resource folder.
	 *
	 * This method should only be called during development. It is used to cache
	 * static fields data populated from any external source (for example, MySQL
	 * database), which are got changed infrequently. Those field should be
	 * annotated using `Cachable.CachableField`.
	 *
	 * The cached results are saved to `main/resource/__cached_classes` folder.
	 */
	private final void saveStaticCache() {
		cachableStaticFields
			.stream()
			.forEach(f -> {
				Object cached = null;
				boolean isAccessible = f.isAccessible();
				if (!isAccessible) { f.setAccessible(true); }
				try {
					cached = f.get(null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} finally {
					if (!isAccessible) { f.setAccessible(false); }
				}
				MyFileUtils.writeFile(
					new File(RESOURCES_PATH, staticCachePath(f)), Json.dumps(cached));
			});
	}
}
