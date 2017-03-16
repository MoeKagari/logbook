package logbook.util;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class ToolUtils {

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean isNotNull(Object obj) {
		return obj != null;
	}

	public static <S> boolean notNullThenHandle(S s, Predicate<S> handler, boolean defaultValue) {
		return s != null ? handler.test(s) : defaultValue;
	}

	public static <S> int notNullThenHandle(S s, ToIntFunction<S> handler, int defaultValue) {
		return s != null ? handler.applyAsInt(s) : defaultValue;
	}

	public static <S, T> T notNullThenHandle(S s, Function<S, T> handler, T defaultValue) {
		return s != null ? handler.apply(s) : defaultValue;
	}

	public static <T> void notNullThenHandle(T t, Consumer<T> handler) {
		if (t != null) handler.accept(t);
	}

	public static <S, T> void forEach(S[] ss, T[] ts, BiConsumer<S, T> bc) {
		if (ss.length != ts.length) return;
		int len = ss.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ss[i], ts[i]);
		}
	}

	public static <T> void forEach(T[] ts, Consumer<T> consu) {
		for (T t : ts) {
			consu.accept(t);
		}
	}

	public static <T> void forEach(T[] ts, ObjIntConsumer<T> consu) {
		for (int i = 0; i < ts.length; i++) {
			consu.accept(ts[i], i);
		}
	}

	public static void forEach(int[] intArray, IntConsumer consu) {
		for (int i : intArray) {
			consu.accept(i);
		}
	}

	public static <T> void forEach(T[] ts, int[] is, ObjIntConsumer<T> bc) {
		if (ts.length != is.length) return;
		int len = ts.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ts[i], is[i]);
		}
	}

	public static int[] arrayCopy(int[] ts) {
		return Arrays.copyOf(ts, ts.length);
	}

	public static int[] doubleToInteger(double[] ds, DoubleToIntFunction fun) {
		int len = ds.length;
		int[] is = new int[len];
		for (int index = 0; index < len; index++) {
			is[index] = fun.applyAsInt(ds[index]);
		}
		return is;
	}

	public static int[] doubleToIntegerFloor(double[] ds) {
		int len = ds.length;
		int[] is = new int[len];
		for (int index = 0; index < len; index++) {
			is[index] = (int) Math.floor(ds[index]);
		}
		return is;
	}

	public static String[] intToString(int[] is, IntFunction<String> fun) {
		int len = is.length;
		String[] ss = new String[len];
		for (int i = 0; i < len; i++) {
			ss[i] = fun.apply(is[i]);
		}
		return ss;
	}

	public static <T> int[] toIntArray(T[] ts, ToIntFunction<T> fun) {
		int len = ts.length;
		int[] is = new int[len];
		for (int i = 0; i < len; i++) {
			is[i] = fun.applyAsInt(ts[i]);
		}
		return is;
	}

	public static <T> String[] toStringArray(T[] ts, Function<T, String> fun) {
		int len = ts.length;
		String[] stringArray = new String[len];
		for (int i = 0; i < len; i++) {
			stringArray[i] = fun.apply(ts[i]);
		}
		return stringArray;
	}

	public static String[] toStringArray(int count, IntFunction<String> fun) {
		String[] stringArray = new String[count];
		for (int i = 0; i < count; i++) {
			stringArray[i] = fun.apply(i);
		}
		return stringArray;
	}

}
