package de.fzi.cep.sepa.runtime.param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OutputStrategy {

	private final String outEventName;

	public OutputStrategy(String outEventName) {
		this.outEventName = outEventName;
	}

	public String getName() {
		return outEventName;
	}

	// public OutputStrategy appendWith(OutputStrategy other) { // name is kept from this
	// return new Combined(this, other);
	// }

	public abstract Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes);

	// public static class Combined extends OutputStrategy {
	//
	// private final OutputStrategy first;
	// private final OutputStrategy second;
	//
	// public Combined(OutputStrategy nameProviding, OutputStrategy other) {
	// super(nameProviding.getName());
	// this.first = nameProviding;
	// this.second = other;
	// }
	//
	// @Override
	// public Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes) {
	// Map<String, Class<?>> combined = first.buildOutEventType(inEventTypes);
	// combined.putAll(second.buildOutEventType(inEventTypes));
	// return combined;
	// }
	//
	// }

	public static class Rename extends OutputStrategy {

		private final String baseType;

		public Rename(String outEventName, String baseType) {
			super(outEventName);
			this.baseType = baseType;
		}

		@Override
		public Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes) {
			if (!inEventTypes.containsKey(baseType))
				throw new IllegalArgumentException("No event type to base on: " + baseType);
			return new HashMap<>(inEventTypes.get(baseType));
		}
	}

	public static class Append extends OutputStrategy {

		private final String baseType;

		private final Map<String, Class<?>> newProperties;

		public Append(Map<String, Class<?>> newProperties, String outEventName, String baseType) {
			super(outEventName);
			this.baseType = baseType;
			this.newProperties = newProperties;
		}

		@Override
		public Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes) {
			if (!inEventTypes.containsKey(baseType))
				throw new IllegalArgumentException("No event type to base on: " + baseType);
			Map<String, Class<?>> outEventType = new HashMap<>(inEventTypes.get(baseType));
			outEventType.putAll(newProperties);
			return outEventType;
		}
	}

	public static class Extract extends OutputStrategy {

		private final String baseType;

		private final List<String> propertiesToDrop;

		public Extract(List<String> propertiesToDrop, String outEventName, String baseType) {
			super(outEventName);
			this.baseType = baseType;
			this.propertiesToDrop = propertiesToDrop;
		}

		@Override
		public Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes) {
			if (!inEventTypes.containsKey(baseType))
				throw new IllegalArgumentException("No event type to base on: " + baseType);
			Map<String, Class<?>> outEventType = new HashMap<>(inEventTypes.get(baseType));
			propertiesToDrop.forEach(outEventType::remove);
			return outEventType;
		}
	}

	public static class Custom extends OutputStrategy {

		private final Map<String, Class<?>> newEventType;

		public Custom(Map<String, Class<?>> newEventType, String outEventName) {
			super(outEventName);
			this.newEventType = newEventType;
		}

		@Override
		public Map<String, Class<?>> buildOutEventType(Map<String, Map<String, Class<?>>> inEventTypes) {
			return new HashMap<>(newEventType);
		}
	}
}
