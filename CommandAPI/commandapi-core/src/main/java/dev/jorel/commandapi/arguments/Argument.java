package dev.jorel.commandapi.arguments;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.StringTooltip;

/**
 * The core abstract class for Command API arguments
 * @param <S>
 */
public abstract class Argument implements IOverrideableSuggestions {

	/**
	 * Returns the primitive type of the current Argument. After executing a
	 * command, this argument should yield an object of this returned class.
	 * 
	 * @return the type that this argument yields when the command is run
	 */
	public abstract Class<?> getPrimitiveType();

	/**
	 * Returns the argument type for this argument.
	 * 
	 * @return the argument type for this argument
	 */
	public abstract CommandAPIArgumentType getArgumentType();

	////////////////////////
	// Raw Argument Types //
	////////////////////////

	private final ArgumentType<?> rawType;

	/**
	 * Constructs an argument with a given NMS/brigadier type.
	 * 
	 * @param rawType the NMS or brigadier type to be used for this argument
	 */
	protected Argument(ArgumentType<?> rawType) {
		this.rawType = rawType;
	}

	/**
	 * Returns the NMS or brigadier type for this argument.
	 * 
	 * @param <T> the object that the argument type yields, in its lowest level in
	 *            the code
	 * @return the NMS or brigadier type for this argument
	 */
	@SuppressWarnings("unchecked")
	public final <T> ArgumentType<T> getRawType() {
		return (ArgumentType<T>) rawType;
	}

	/////////////////
	// Suggestions //
	/////////////////

	Optional<BiFunction<CommandSender, Object[], StringTooltip[]>> suggestions = Optional.empty();
	

	
	/**
	 * Maps a String[] of suggestions to a StringTooltip[], using StringTooltip.none.
	 * This is used internally by the CommandAPI.
	 * 
	 * @param suggestions the array of suggestions to convert
	 * @return a StringTooltip[] representation of the provided suggestions
	 */
	private final StringTooltip[] fromSuggestions(String[] suggestions) {
		return Arrays.stream(suggestions).map(StringTooltip::none).toArray(StringTooltip[]::new);
	}

	/**
	 * Override the suggestions of this argument with a String array. Typically,
	 * this is the supplier <code>s -> suggestions</code>.
	 * 
	 * @param suggestions the string array to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestions(String... suggestions) {
		this.suggestions = Optional.of((c, m) -> fromSuggestions(suggestions));
		return this;
	}

	/**
	 * Override the suggestions of this argument with a function that maps the
	 * command sender to a String array.
	 * 
	 * @param suggestions the function to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestions(Function<CommandSender, String[]> suggestions) {
		this.suggestions =  Optional.of((c, m) -> fromSuggestions(suggestions.apply(c)));
		return this;
	}
	
	/**
	 * Override the suggestions of this argument with a function that maps the
	 * command sender and a data set of previously declared arguments to a String array.
	 * 
	 * @param suggestions the function to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestions(BiFunction<CommandSender, Object[], String[]> suggestions) {
		this.suggestions =  Optional.of((c, m) -> fromSuggestions(suggestions.apply(c, m)));
		return this;
	}
	
	/**
	 * Override the suggestions of this argument with a String array. Typically,
	 * this is the supplier <code>s -> suggestions</code>.
	 * 
	 * @param suggestions the string array to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestionsT(StringTooltip... suggestions) {
		this.suggestions = Optional.of((c, m) -> suggestions);
		return this;
	}

	/**
	 * Override the suggestions of this argument with a function that maps the
	 * command sender to a String array.
	 * 
	 * @param suggestions the function to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestionsT(Function<CommandSender, StringTooltip[]> suggestions) {
		this.suggestions =  Optional.of((c, m) -> suggestions.apply(c));
		return this;
	}
	
	/**
	 * Override the suggestions of this argument with a function that maps the
	 * command sender and a data set of previously declared arguments to a String array.
	 * 
	 * @param suggestions the function to override suggestions with
	 * @return the current argument
	 */
	@Override
	public final Argument overrideSuggestionsT(BiFunction<CommandSender, Object[], StringTooltip[]> suggestions) {
		this.suggestions =  Optional.of(suggestions);
		return this;
	}

	/**
	 * Returns a function that maps the command sender to a String array of
	 * suggestions for the current command, or <code>null</code> if this is not
	 * overridden.
	 * 
	 * @return a function that provides suggestions, or <code>Optional.empty()</code> if there
	 *         are no overridden suggestions.
	 */
	@Override
	public final Optional<BiFunction<CommandSender, Object[], StringTooltip[]>> getOverriddenSuggestions() {
		return suggestions;
	}

	/////////////////
	// Permissions //
	/////////////////

	private CommandPermission permission = CommandPermission.NONE;

	/**
	 * Assigns the given permission as a requirement to execute this command.
	 * 
	 * @param permission the permission required to execute this command
	 * @return this current argument
	 */
	public final Argument withPermission(CommandPermission permission) {
		this.permission = permission;
		return this;
	}

	/**
	 * Returns the permission required to run this command
	 * @return the permission required to run this command
	 */
	public final CommandPermission getArgumentPermission() {
		return permission;
	}
	
	//////////////////
	// Requirements //
	//////////////////
	
	private Predicate<CommandSender> requirements = s -> true;
	
	
	/**
	 * Returns the requirements required to run this command
	 * @return the requirements required to run this command
	 */
	public final Predicate<CommandSender> getRequirements() {
		return this.requirements;
	}
	
	/**
	 * Adds a requirement that has to be satisfied to use this argument. This method
	 * can be used multiple times and each use of this method will AND its
	 * requirement with the previously declared ones
	 * 
	 * @param requirement the predicate that must be satisfied to use this argument
	 * @return this current argument
	 */
	public final Argument withRequirement(Predicate<CommandSender> requirement) {
		this.requirements = this.requirements.and(requirement);
		return this;
	}

}