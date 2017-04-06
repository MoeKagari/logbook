package logbook.util;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ToolUtils {

	public static double division(int a, int b) {
		return a * 1.0 / b;
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean isNotNull(Object obj) {
		return obj != null;
	}

	public static <S> S returnOneself(S s) {
		return s;
	}

	/*----------------------------------------------------------------------------------------------------------*/

	public static void ifHandle(boolean b, Runnable run) {
		if (b) run.run();
	}

	public static void ifNotHandle(boolean b, Runnable run) {
		ifHandle(!b, run);
	}

	public static int ifHandle(boolean b, IntSupplier sup, int defaultValue) {
		return b ? sup.getAsInt() : defaultValue;
	}

	public static int ifNotHandle(boolean b, IntSupplier sup, int defaultValue) {
		return ifHandle(!b, sup, defaultValue);
	}

	public static <S> S ifHandle(boolean b, Supplier<S> sup, S defaultValue) {
		return b ? sup.get() : defaultValue;
	}

	public static <S> S ifNotHandle(boolean b, Supplier<S> sup, S defaultValue) {
		return ifHandle(!b, sup, defaultValue);
	}

	public static void ifHandle(int value, IntPredicate pre, IntConsumer con) {
		if (pre.test(value)) con.accept(value);
	}

	public static void ifNotHandle(int value, IntPredicate pre, IntConsumer con) {
		ifHandle(value, pre.negate(), con);
	}

	public static <S> void ifHandle(S s, Predicate<S> pre, Consumer<S> con) {
		if (pre.test(s)) con.accept(s);
	}

	public static <S> void ifNotHandle(S s, Predicate<S> pre, Consumer<S> con) {
		ifHandle(s, pre.negate(), con);
	}

	public static <S, T> T ifHandle(S s, Predicate<S> pre, Function<S, T> fun, T defaultValue) {
		return pre.test(s) ? fun.apply(s) : defaultValue;
	}

	public static <S, T> T ifNotHandle(S s, Predicate<S> pre, Function<S, T> fun, T defaultValue) {
		return ifHandle(s, pre.negate(), fun, defaultValue);
	}

	/*----------------------------------------------------------------------------------------------------------*/

	public static <S> boolean notNullThenHandle(S s, Predicate<S> handler, boolean defaultValue) {
		return s != null ? handler.test(s) : defaultValue;
	}

	public static <S> int notNullThenHandle(S s, ToIntFunction<S> handler, int defaultValue) {
		return ifHandle(s != null, () -> handler.applyAsInt(s), defaultValue);
	}

	public static <S, T> T notNullThenHandle(S s, Function<S, T> handler, T defaultValue) {
		return ifHandle(s != null, () -> handler.apply(s), defaultValue);
	}

	public static <S> void notNullThenHandle(S s, Consumer<S> handler) {
		ifHandle(s != null, () -> handler.accept(s));
	}

	/*----------------------------------------------------------------------------------------------------------*/

	public static <S, T> void forEach(S[] ss, T[] ts, BiConsumer<S, T> bc) {
		if (ss.length != ts.length) return;
		int len = ss.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ss[i], ts[i]);
		}
	}

	public static <S> void forEach(S[] ss, int[] is, ObjIntConsumer<S> bc) {
		if (ss.length != is.length) return;
		int len = ss.length;
		for (int i = 0; i < len; i++) {
			bc.accept(ss[i], is[i]);
		}
	}

	public static <S> void forEach(S[] ss, Consumer<S> consu) {
		for (S s : ss) {
			consu.accept(s);
		}
	}

	public static <S> void forEach(S[] ss, ObjIntConsumer<S> consu) {
		for (int i = 0; i < ss.length; i++) {
			consu.accept(ss[i], i);
		}
	}

	public static void forEach(int[] intArray, IntConsumer consu) {
		for (int i : intArray) {
			consu.accept(i);
		}
	}

	/*----------------------------------------------------------------------------------------------------------*/

	public static int[] arrayCopy(int[] is) {
		return Arrays.copyOf(is, is.length);
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
		return doubleToInteger(ds, d -> (int) Math.floor(d));
	}

	public static <S> int[] toIntArray(S[] ss, ToIntFunction<S> fun) {
		int len = ss.length;
		int[] is = new int[len];
		for (int i = 0; i < len; i++) {
			is[i] = fun.applyAsInt(ss[i]);
		}
		return is;
	}

	public static String[] toStringArray(int[] is, IntFunction<String> fun) {
		int len = is.length;
		String[] ss = new String[len];
		for (int i = 0; i < len; i++) {
			ss[i] = fun.apply(is[i]);
		}
		return ss;
	}

	public static <S> String[] toStringArray(S[] ss, Function<S, String> fun) {
		int len = ss.length;
		String[] stringArray = new String[len];
		for (int i = 0; i < len; i++) {
			stringArray[i] = fun.apply(ss[i]);
		}
		return stringArray;
	}

	public static String[] toStringArray(int length, IntFunction<String> fun) {
		String[] stringArray = new String[length];
		for (int i = 0; i < length; i++) {
			stringArray[i] = fun.apply(i);
		}
		return stringArray;
	}

}
