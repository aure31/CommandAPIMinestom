package dev.jorel.commandapi;

import java.util.function.Function;

/**
 * This class represents a suggestion for an argument with a hover tooltip text
 * for that suggestion. This class is parameterized over some object S that
 * represents the safe cast type for argument suggestions. This class is to be
 * used with safe suggestion overrides, via the
 * SafeOverrideableArgument.safeOverrideSuggestionsT method.
 *
 * @param <S> the object that the argument suggestions use
 */
public class Tooltip<S> {

	private final S object;
	private final String tooltip;
	
	private Tooltip(S object, String tooltip) {
		this.object = object;
		this.tooltip = tooltip;
	}
	
	/**
	 * Constructs a Tooltip<S> with a suggestion and a tooltip
	 * 
	 * @param <S> the object that the argument suggestions use
	 * @param object the suggestion to provide to the user
	 * @param tooltip    the tooltip to show to the user when they hover over the
	 *                   suggestion
	 * @return a Tooltip<S> representing this suggestion and tooltip
	 */
	public static <S> Tooltip<S> of(S object, String tooltip) {
		return new Tooltip<S>(object, tooltip);
	}
	
	/**
	 * Constructs a Tooltip<S> with a suggestion and no tooltip
	 * 
	 * @param <S> the object that the argument suggestions use
	 * @param object the suggestion to provide to the user
	 * @return a Tooltip<S> representing this suggestion
	 */
	public static <S> Tooltip<S> none(S object) {
		return new Tooltip<S>(object, null);
	}
	
	/**
	 * Constructs a Tooltip<S>[] from an array of Tooltip<S> via varargs. This
	 * method takes advantage of Java's varargs to construct a generic array
	 * parameterised over S for the purpose of type safety for the
	 * safeOverrideSuggestionsT method, because Java doesn't allow you to create generic arrays.
	 * 
	 * @param <S> the object that the argument suggestions use
	 * @param tooltips an array of Tooltips<S> to be converted into Tooltip<S>[]
	 * @return a Tooltip<S>[] from the provided Tooltip<S>
	 */
	@SafeVarargs
	public static <S> Tooltip<S>[] arrayOf(Tooltip<S>... tooltips) {
		return tooltips;
	}
	
	/**
	 * Constructs a function that maps the current Tooltip<S> into a StringTooltip,
	 * using a standard mapping function which is defined for a given argument. This
	 * method is used internally by the CommandAPI.
	 * 
	 * @param <S> the object that the argument suggestions use
	 * @param mapper a mapping function that converts an S instance into a String
	 * @return the mapping function from this tooltip into a StringTooltip
	 */
	public static <S> Function<Tooltip<S>, StringTooltip> build(Function<S, String> mapper) {
		return t -> StringTooltip.of(mapper.apply(t.object), t.tooltip);
	}
	
}
