package com.aol.cyclops.matcher.builders;

import java.util.Optional;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import org.hamcrest.Matcher;

import com.aol.cyclops.matcher.Predicates;
import com.aol.cyclops.matcher.TypedFunction;
import com.nurkiewicz.lazyseq.LazySeq;


@AllArgsConstructor(access=AccessLevel.PACKAGE)
public class CheckValues<T,R> {
	private final Class<T> clazz;
	protected final _Simpler_Case<R> simplerCase;
	
	public<T1 extends T> FinalCheck<T,T1,R> isType(TypedFunction<T1,R> type){
		return new FinalCheck(type.getType().getClass(),simplerCase,type);
	}
	@AllArgsConstructor
	public static class FinalCheck<T,T1,R>{
		private final Class<T1> clazz;
		private final _Simpler_Case<R> simplerCase;
		private final TypedFunction<T,R> fn;
		public final  <V> CheckValues<T,R> hasValues(V... values) {

			
			Predicate predicate = it -> Optional.of(it)
					.map(v -> v.getClass().isAssignableFrom(clazz))
					.orElse(false);
			// add wildcard support
			
			Predicate<V>[] predicates = LazySeq.of(values)
					.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
					.toArray(new Predicate[0]);

			return new _Simpler_Case(simplerCase.getPatternMatcher().inCaseOfManyType(predicate, fn,
					predicates)).withType(clazz);
		}
		@SafeVarargs
		public final <V> CheckValues<T,R> hasValuesWhere(Predicate<V>... values) {

			
			Predicate predicate = it -> Optional.of(it)
					.map(v -> v.getClass().isAssignableFrom(clazz))
					.orElse(false);
			// add wildcard support
			
			Predicate<V>[] predicates = LazySeq.of(values)
					.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
					.toArray(new Predicate[0]);

			return new _Simpler_Case(simplerCase.getPatternMatcher().inCaseOfManyType(predicate, fn,
					predicates)).withType(clazz);
		}
		@SafeVarargs
		public final <V> CheckValues<T,R>  hasValuesMatching(Matcher<V>... values) {

			
			Predicate predicate = it -> Optional.of(it)
					.map(v -> v.getClass().isAssignableFrom(clazz))
					.orElse(false);
			// add wildcard support
			
			Predicate<V>[] predicates = LazySeq.of(values)
					.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
					.toArray(new Predicate[0]);

			return new _Simpler_Case(simplerCase.getPatternMatcher().inCaseOfManyType(predicate, fn,
					predicates)).withType(clazz);
		}
		public final <V> CheckValues<T,R>  isEmpty() {
			
			
			
			
			Predicate predicate = it -> Optional.of(it)
					.map(v -> v.getClass().isAssignableFrom(clazz))
					.orElse(false);
			// add wildcard support
			
			Predicate<V>[] predicates = new Predicate[]{i->i==SeqUtils.EMPTY};

			return new _Simpler_Case(simplerCase.getPatternMatcher().inCaseOfManyType(predicate, fn,
					predicates)).withType(clazz);

		}
		public final <V> CheckValues<T,R>  anyValues() {
			
			
			
			
			Predicate predicate = it -> Optional.of(it)
					.map(v -> v.getClass().isAssignableFrom(clazz))
					.orElse(false);
			// add wildcard support
			
			Predicate<V>[] predicates = new Predicate[]{i->true};

			return new _Simpler_Case(simplerCase.getPatternMatcher().inCaseOfManyType(predicate, fn,
					predicates)).withType(clazz);

		}
	}
	/**
	 * 
	 * Provide a comparison value, JDK 8 Predicate, or Hamcrest Matcher  for each Element to match on.
	 * 
	 * Further &amp; recursively unwrap any element by Predicates.type(ELEMENT_TYPE.class).with(V... values)
	 * 
	 * @see Predicates#type
	 * 
	 * @param values Matching rules for each element in the decomposed / unapplied user input
	 * @return Pattern Matcher builder with completed Case added to it
	 */
	@SafeVarargs
	@Deprecated
	public final <V> _LastStep<R,V,T> with(V... values) {
		
		return hasValues(values);
		

	}
	
	public final <V> _LastStep<R,V,T> hasValues(V... values) {

		
		Predicate predicate = it -> Optional.of(it)
				.map(v -> v.getClass().isAssignableFrom(clazz))
				.orElse(false);
		// add wildcard support
		
		Predicate<V>[] predicates = LazySeq.of(values)
				.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
				.toArray(new Predicate[0]);

		return new _LastStep<R,V,T>(clazz,predicate,predicates,this.getPatternMatcher());
	}
	@SafeVarargs
	public final <V> _LastStep<R,V,T> hasValuesWhere(Predicate<V>... values) {

		
		Predicate predicate = it -> Optional.of(it)
				.map(v -> v.getClass().isAssignableFrom(clazz))
				.orElse(false);
		// add wildcard support
		
		Predicate<V>[] predicates = LazySeq.of(values)
				.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
				.toArray(new Predicate[0]);

		return new _LastStep<R,V,T>(clazz,predicate,predicates,this.getPatternMatcher());
	}
	@SafeVarargs
	public final <V> _LastStep<R,V,T> hasValuesMatching(Matcher<V>... values) {

		
		Predicate predicate = it -> Optional.of(it)
				.map(v -> v.getClass().isAssignableFrom(clazz))
				.orElse(false);
		// add wildcard support
		
		Predicate<V>[] predicates = LazySeq.of(values)
				.map(nextValue -> simplerCase.convertToPredicate(nextValue)).toList()
				.toArray(new Predicate[0]);

		return new _LastStep<R,V,T>(clazz,predicate,predicates,this.getPatternMatcher());
	}
	public final <V> _LastStep<R,V,T> isEmpty() {
		
		
		
		
		Predicate predicate = it -> Optional.of(it)
				.map(v -> v.getClass().isAssignableFrom(clazz))
				.orElse(false);
		// add wildcard support
		
		Predicate<V>[] predicates = new Predicate[]{i->i==SeqUtils.EMPTY};

		return new _LastStep<R,V,T>(clazz,predicate,predicates,this.getPatternMatcher());

	}


	public PatternMatcher getPatternMatcher() {
		return simplerCase.getPatternMatcher();
	}
}

