package org.streampipes.model.schema;

/**
 * A property scope defines the scope of an event property. Although an event schema consists of a flat list of event
 * properties, properties might differ in terms of their purpose. For instance, a property indicating a timestamp
 * are typically not be used for performing calculations (e.g., filter operations or sensor measurement value
 * transformations). In contrast, a property that describes an identifier (e.g., machineId) is often used for
 * partitioning a stream (e.g., calculating seperate temperature values per machine).
 *
 * Use the SDK to assign property scopes to stream descriptions and stream (property) requirements.
 *
 * For better understanding the meaning of property scopes, think of the meaning of variables in the data warehousing
 * domains:
 * Measurements are often numbers, while the dimension is what numbers are “sliced and diced” by.
 */
public enum PropertyScope {

  /**
   *  A property that defines meta-information about the event, for instance its occurrence time.
   *
   */
  HEADER_PROPERTY,

  /**
   * A property that describes context of a measurement, e.g., the ID of a machine or a thing.
   */
  DIMENSION_PROPERTY,

  /**
   * A property that contains (often quantitative) measurement values.
   */
  MEASUREMENT_PROPERTY,

  /**
   * The property scope is not further described. Use this scope for defining property requirements where the data
   * processor's functionality does not require any specific property scope (e.g., a text filter that can filter any
   * text-based property).
   */
  NONE;

}
