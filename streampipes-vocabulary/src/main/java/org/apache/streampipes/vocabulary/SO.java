/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.vocabulary;


import java.util.Arrays;
import java.util.List;

public class SO {

  public static final String NS = "http://schema.org/";
  public static final String NS_PREFIX = "so";

  public static final String GEO_JSON_LINE_STRING = "http://streampipes.org/GeoJsonLineString";
  public static final String GEO_JSON_POLYGON = "http://streampipes.org/GeoJsonPolygon";
  public static final String GEO_JSON_MULTI_POINT = "http://streampipes.org/GeoJsonMultiPoint";
  public static final String GEO_JSON_MULTI_LINE_STRING = "http://streampipes.org/GeoJsonMultiLineString";
  public static final String GEO_JSON_MULTI_POLYGON = "http://streampipes.org/GeoJsonMultiPolygon";
  public static final String QUANTITATIVE_VALUE = "http://schema.org/QuantitativeValue";
  public static final String PROPERTY_VALUE_SPECIFICATION = "http://schema.org/PropertyValueSpecification";
  public static final String NUMBER = "http://schema.org/Number";
  public static final String ORG_ENUMERATION = "http://schema.org/Enumeration";
  public static final String ALTITUDE = "http://streampipes.org/Altitude";
  public static final String ACCEPTS_RESERVATIONS = "http://schema.org/acceptsReservations";
  public static final String ACCESS_CODE = "http://schema.org/accessCode";
  public static final String ACCESSIBILITY_API = "http://schema.org/accessibilityAPI";
  public static final String ACCESSIBILITY_CONTROL = "http://schema.org/accessibilityControl";
  public static final String ACCESSIBILITY_FEATURE = "http://schema.org/accessibilityFeature";
  public static final String ACCESSIBILITY_HAZARD = "http://schema.org/accessibilityHazard";
  public static final String ACTION = "http://schema.org/action";
  public static final String ORG_ACTIVE_INGREDIENT = "http://schema.org/activeIngredient";
  public static final String ACTIVITY_DURATION = "http://schema.org/activityDuration";
  public static final String ACTIVITY_FREQUENCY = "http://schema.org/activityFrequency";
  public static final String ADDITIONAL_NAME = "http://schema.org/additionalName";
  public static final String ADDITIONAL_PROPERTY = "http://schema.org/additionalProperty";
  public static final String ADDITIONAL_VARIABLE = "http://schema.org/additionalVariable";
  public static final String ADDRESS_LOCALITY = "http://schema.org/addressLocality";
  public static final String ORG_ADDRESS_REGION = "http://schema.org/addressRegion";
  public static final String ADMINISTRATION_ROUTE = "http://schema.org/administrationRoute";
  public static final String ALCOHOL_WARNING = "http://schema.org/alcoholWarning";
  public static final String ALGORITHM = "http://schema.org/algorithm";
  public static final String ALIGNMENT_TYPE = "http://schema.org/alignmentType";
  public static final String ALTERNATE_NAME = "http://schema.org/alternateName";
  public static final String ALTERNATIVE_HEADLINE = "http://schema.org/alternativeHeadline";
  public static final String AMOUNT_OF_THIS_GOOD = "http://schema.org/amountOfThisGood";
  public static final String ANSWER_COUNT = "http://schema.org/answerCount";
  public static final String APPLICATION_CATEGORY = "http://schema.org/applicationCategory";
  public static final String APPLICATION_SUB_CATEGORY = "http://schema.org/applicationSubCategory";
  public static final String APPLICATION_SUITE = "http://schema.org/applicationSuite";
  public static final String ARRIVAL_GATE = "http://schema.org/arrivalGate";
  public static final String ARRIVAL_PLATFORM = "http://schema.org/arrivalPlatform";
  public static final String ARRIVAL_TERMINAL = "http://schema.org/arrivalTerminal";
  public static final String ORG_ARRIVAL_TIME = "http://schema.org/arrivalTime";
  public static final String ARTICLE_BODY = "http://schema.org/articleBody";
  public static final String ARTICLE_SECTION = "http://schema.org/articleSection";
  public static final String ASPECT = "http://schema.org/aspect";
  public static final String ASSEMBLY = "http://schema.org/assembly";
  public static final String ASSEMBLY_VERSION = "http://schema.org/assemblyVersion";
  public static final String ASSOCIATED_PATHOPHYSIOLOGY = "http://schema.org/associatedPathophysiology";
  public static final String AUDIENCE_TYPE = "http://schema.org/audienceType";
  public static final String AVAILABILITY_ENDS = "http://schema.org/availabilityEnds";
  public static final String AVAILABILITY_STARTS = "http://schema.org/availabilityStarts";
  public static final String AVAILABLE_FROM = "http://schema.org/availableFrom";
  public static final String AVAILABLE_THROUGH = "http://schema.org/availableThrough";
  public static final String AWARD = "http://schema.org/award";
  public static final String BACKGROUND = "http://schema.org/background";
  public static final String BASE_SALARY = "http://schema.org/baseSalary";
  public static final String BENEFITS = "http://schema.org/benefits";
  public static final String BEST_RATING = "http://schema.org/bestRating";
  public static final String BILLING_INCREMENT = "http://schema.org/billingIncrement";
  public static final String BIOMECHNICAL_CLASS = "http://schema.org/biomechnicalClass";
  public static final String BIRTH_DATE = "http://schema.org/birthDate";
  public static final String BITRATE = "http://schema.org/bitrate";
  public static final String BOARDING_GROUP = "http://schema.org/boardingGroup";
  public static final String BODY_LOCATION = "http://schema.org/bodyLocation";
  public static final String BOOK_EDITION = "http://schema.org/bookEdition";
  public static final String BOOKING_TIME = "http://schema.org/bookingTime";
  public static final String BOOLEAN = "http://schema.org/Boolean";
  public static final String BOX = "http://schema.org/box";
  public static final String BREADCRUMB = "http://schema.org/breadcrumb";
  public static final String BREASTFEEDING_WARNING = "http://schema.org/breastfeedingWarning";
  public static final String BROWSER_REQUIREMENTS = "http://schema.org/browserRequirements";
  public static final String BUS_NAME = "http://schema.org/busName";
  public static final String BUS_NUMBER = "http://schema.org/busNumber";
  public static final String CALORIES = "http://schema.org/calories";
  public static final String CAPTION = "http://schema.org/caption";
  public static final String CARBOHYDRATE_CONTENT = "http://schema.org/carbohydrateContent";
  public static final String CARRIER_REQUIREMENTS = "http://schema.org/carrierRequirements";
  public static final String CHARACTER_NAME = "http://schema.org/characterName";
  public static final String CHECKIN_TIME = "http://schema.org/checkinTime";
  public static final String CHECKOUT_TIME = "http://schema.org/checkoutTime";
  public static final String CHILD_MAX_AGE = "http://schema.org/childMaxAge";
  public static final String CHILD_MIN_AGE = "http://schema.org/childMinAge";
  public static final String CHOLESTEROL_CONTENT = "http://schema.org/cholesterolContent";
  public static final String CIRCLE = "http://schema.org/circle";
  public static final String CLINCAL_PHARMACOLOGY = "http://schema.org/clincalPharmacology";
  public static final String CLIP_NUMBER = "http://schema.org/clipNumber";
  public static final String CLOSES = "http://schema.org/closes";
  public static final String CODE_REPOSITORY = "http://schema.org/codeRepository";
  public static final String CODE_VALUE = "http://schema.org/codeValue";
  public static final String CODING_SYSTEM = "http://schema.org/codingSystem";
  public static final String COLOR = "http://schema.org/color";
  public static final String COMMENT_COUNT = "http://schema.org/commentCount";
  public static final String COMMENT_TEXT = "http://schema.org/commentText";
  public static final String COMMENT_TIME = "http://schema.org/commentTime";
  public static final String CONFIRMATION_NUMBER = "http://schema.org/confirmationNumber";
  public static final String CONTACT_TYPE = "http://schema.org/contactType";
  public static final String CONTENT_RATING = "http://schema.org/contentRating";
  public static final String CONTENT_SIZE = "http://schema.org/contentSize";
  public static final String CONTENT_TYPE = "http://schema.org/contentType";
  public static final String CONTENT_URL = "http://schema.org/contentUrl";
  public static final String COOK_TIME = "http://schema.org/cookTime";
  public static final String COOKING_METHOD = "http://schema.org/cookingMethod";
  public static final String COPYRIGHT_YEAR = "http://schema.org/copyrightYear";
  public static final String COST_CURRENCY = "http://schema.org/costCurrency";
  public static final String COST_ORIGIN = "http://schema.org/costOrigin";
  public static final String COST_PER_UNIT = "http://schema.org/costPerUnit";
  public static final String COUNTRIES_NOT_SUPPORTED = "http://schema.org/countriesNotSupported";
  public static final String COUNTRIES_SUPPORTED = "http://schema.org/countriesSupported";
  public static final String CURRENCIES_ACCEPTED = "http://schema.org/currenciesAccepted";
  public static final String DATE_CREATED = "http://schema.org/dateCreated";
  public static final String DATE_ISSUED = "http://schema.org/dateIssued";
  public static final String DATE_MODIFIED = "http://schema.org/dateModified";
  public static final String DATE_POSTED = "http://schema.org/datePosted";
  public static final String ORG_DATE_PUBLISHED = "http://schema.org/datePublished";
  public static final String DATE_TIME = "http://schema.org/DateTime";
  public static final String DATELINE = "http://schema.org/dateline";
  public static final String DEATH_DATE = "http://schema.org/deathDate";
  public static final String DEPARTURE_GATE = "http://schema.org/departureGate";
  public static final String DEPARTURE_PLATFORM = "http://schema.org/departurePlatform";
  public static final String DEPARTURE_TERMINAL = "http://schema.org/departureTerminal";
  public static final String DEPARTURE_TIME = "http://schema.org/departureTime";
  public static final String DEPENDENCIES = "http://schema.org/dependencies";
  public static final String DEVICE = "http://schema.org/device";
  public static final String DIET_FEATURES = "http://schema.org/dietFeatures";
  public static final String DISCOUNT = "http://schema.org/discount";
  public static final String DISCOUNT_CODE = "http://schema.org/discountCode";
  public static final String DISCOUNT_CURRENCY = "http://schema.org/discountCurrency";
  public static final String DISCUSSION_URL = "http://schema.org/discussionUrl";
  public static final String DISSOLUTION_DATE = "http://schema.org/dissolutionDate";
  public static final String DISTANCE = "http://schema.org/distance";
  public static final String DOOR_TIME = "http://schema.org/doorTime";
  public static final String DOSAGE_FORM = "http://schema.org/dosageForm";
  public static final String DOSE_UNIT = "http://schema.org/doseUnit";
  public static final String DOSE_VALUE = "http://schema.org/doseValue";
  public static final String DOWNLOAD_URL = "http://schema.org/downloadUrl";
  public static final String DOWNVOTE_COUNT = "http://schema.org/downvoteCount";
  public static final String DROPOFF_TIME = "http://schema.org/dropoffTime";
  public static final String DRUG_UNIT = "http://schema.org/drugUnit";
  public static final String DUNS = "http://schema.org/duns";
  public static final String DURATION = "http://schema.org/duration";
  public static final String EDUCATION_REQUIREMENTS = "http://schema.org/educationRequirements";
  public static final String EDUCATIONAL_FRAMEWORK = "http://schema.org/educationalFramework";
  public static final String EDUCATIONAL_ROLE = "http://schema.org/educationalRole";
  public static final String ORG_EDUCATIONAL_USE = "http://schema.org/educationalUse";
  public static final String ELEVATION = "http://schema.org/elevation";
  public static final String EMAIL = "http://schema.org/email";
  public static final String EMBED_URL = "http://schema.org/embedUrl";
  public static final String EMPLOYMENT_TYPE = "http://schema.org/employmentType";
  public static final String ENCODING_FORMAT = "http://schema.org/encodingFormat";
  public static final String ENCODING_TYPE = "http://schema.org/encodingType";
  public static final String END_DATE = "http://schema.org/endDate";
  public static final String END_TIME = "http://schema.org/endTime";
  public static final String ORG_EPIDEMIOLOGY = "http://schema.org/epidemiology";
  public static final String EPISODE_NUMBER = "http://schema.org/episodeNumber";
  public static final String ESTIMATED_FLIGHT_DURATION = "http://schema.org/estimatedFlightDuration";
  public static final String EVIDENCE_ORIGIN = "http://schema.org/evidenceOrigin";
  public static final String ORG_EXERCISE_TYPE = "http://schema.org/exerciseType";
  public static final String EXIF_DATA = "http://schema.org/exifData";
  public static final String EXPECTED_ARRIVAL_FROM = "http://schema.org/expectedArrivalFrom";
  public static final String EXPECTED_ARRIVAL_UNTIL = "http://schema.org/expectedArrivalUntil";
  public static final String EXPECTED_PROGNOSIS = "http://schema.org/expectedPrognosis";
  public static final String EXPERIENCE_REQUIREMENTS = "http://schema.org/experienceRequirements";
  public static final String EXPERT_CONSIDERATIONS = "http://schema.org/expertConsiderations";
  public static final String EXPIRES = "http://schema.org/expires";
  public static final String FAMILY_NAME = "http://schema.org/familyName";
  public static final String FAT_CONTENT = "http://schema.org/fatContent";
  public static final String FAX_NUMBER = "http://schema.org/faxNumber";
  public static final String FEATURE_LIST = "http://schema.org/featureList";
  public static final String FIBER_CONTENT = "http://schema.org/fiberContent";
  public static final String FILE_FORMAT = "http://schema.org/fileFormat";
  public static final String FILE_SIZE = "http://schema.org/fileSize";
  public static final String FLIGHT_DISTANCE = "http://schema.org/flightDistance";
  public static final String FLIGHT_NUMBER = "http://schema.org/flightNumber";
  public static final String FOLLOWUP = "http://schema.org/followup";
  public static final String FOOD_WARNING = "http://schema.org/foodWarning";
  public static final String FOUNDING_DATE = "http://schema.org/foundingDate";
  public static final String FREE = "http://schema.org/free";
  public static final String FREQUENCY = "http://schema.org/frequency";
  public static final String FROM_LOCATION = "http://schema.org/fromLocation";
  public static final String FUNCTION = "http://schema.org/function";
  public static final String FUNCTIONAL_CLASS = "http://schema.org/functionalClass";
  public static final String GENDER = "http://schema.org/gender";
  public static final String GENRE = "http://schema.org/genre";
  public static final String GIVEN_NAME = "http://schema.org/givenName";
  public static final String GLOBAL_LOCATION_NUMBER = "http://schema.org/globalLocationNumber";
  public static final String GTIN_13 = "http://schema.org/gtin13";
  public static final String GTIN_14 = "http://schema.org/gtin14";
  public static final String GTIN_8 = "http://schema.org/gtin8";
  public static final String GUIDELINE_DATE = "http://schema.org/guidelineDate";
  public static final String HAS_MAP = "http://schema.org/hasMap";
  public static final String HEADLINE = "http://schema.org/headline";
  public static final String HIGH_PRICE = "http://schema.org/highPrice";
  public static final String HONORIFIC_PREFIX = "http://schema.org/honorificPrefix";
  public static final String HONORIFIC_SUFFIX = "http://schema.org/honorificSuffix";
  public static final String HOW_PERFORMED = "http://schema.org/howPerformed";
  public static final String HTTP_METHOD = "http://schema.org/httpMethod";
  public static final String IATA_CODE = "http://schema.org/iataCode";
  public static final String ICAO_CODE = "http://schema.org/icaoCode";
  public static final String IMAGE = "http://schema.org/image";
  public static final String IN_LANGUAGE = "http://schema.org/inLanguage";
  public static final String INCENTIVES = "http://schema.org/incentives";
  public static final String INDUSTRY = "http://schema.org/industry";
  public static final String INFECTIOUS_AGENT = "http://schema.org/infectiousAgent";
  public static final String INGREDIENTS = "http://schema.org/ingredients";
  public static final String INSTALL_URL = "http://schema.org/installUrl";
  public static final String INTENSITY = "http://schema.org/intensity";
  public static final String INTERACTION_COUNT = "http://schema.org/interactionCount";
  public static final String INTERACTIVITY_TYPE = "http://schema.org/interactivityType";
  public static final String IS_AVAILABLE_GENERICALLY = "http://schema.org/isAvailableGenerically";
  public static final String IS_BASED_ON_URL = "http://schema.org/isBasedOnUrl";
  public static final String IS_FAMILY_FRIENDLY = "http://schema.org/isFamilyFriendly";
  public static final String IS_GIFT = "http://schema.org/isGift";
  public static final String IS_PROPRIETARY = "http://schema.org/isProprietary";
  public static final String ISBN = "http://schema.org/isbn";
  public static final String ISIC_V_4 = "http://schema.org/isicV4";
  public static final String ISSN = "http://schema.org/issn";
  public static final String ISSUE_NUMBER = "http://schema.org/issueNumber";
  public static final String ITEM_LIST_ELEMENT = "http://schema.org/itemListElement";
  public static final String ITEM_LIST_ORDER = "http://schema.org/itemListOrder";
  public static final String JOB_TITLE = "http://schema.org/jobTitle";
  public static final String KEYWORDS = "http://schema.org/keywords";
  public static final String LABEL_DETAILS = "http://schema.org/labelDetails";
  public static final String LAST_REVIEWED = "http://schema.org/lastReviewed";
  public static final String LATITUDE = "http://schema.org/latitude";
  public static final String LEARNING_RESOURCE_TYPE = "http://schema.org/learningResourceType";
  public static final String LEGAL_NAME = "http://schema.org/legalName";
  public static final String LICENSE = "http://schema.org/license";
  public static final String LINE = "http://schema.org/line";
  public static final String LODGING_UNIT_DESCRIPTION = "http://schema.org/lodgingUnitDescription";
  public static final String LOGO = "http://schema.org/logo";
  public static final String LONGITUDE = "http://schema.org/longitude";
  public static final String LOW_PRICE = "http://schema.org/lowPrice";
  public static final String MAP = "http://schema.org/map";
  public static final String MAX_PRICE = "http://schema.org/maxPrice";
  public static final String MAX_VALUE = "http://schema.org/maxValue";
  public static final String MEAL_SERVICE = "http://schema.org/mealService";
  public static final String MECHANISM_OF_ACTION = "http://schema.org/mechanismOfAction";
  public static final String MEMBERSHIP_NUMBER = "http://schema.org/membershipNumber";
  public static final String MEMORY_REQUIREMENTS = "http://schema.org/memoryRequirements";
  public static final String MENU = "http://schema.org/menu";
  public static final String MIN_PRICE = "http://schema.org/minPrice";
  public static final String MIN_VALUE = "http://schema.org/minValue";
  public static final String MODIFIED_TIME = "http://schema.org/modifiedTime";
  public static final String MPN = "http://schema.org/mpn";
  public static final String MULTIPLE_VALUES = "http://schema.org/multipleValues";
  public static final String MUSCLE_ACTION = "http://schema.org/muscleAction";
  public static final String NAICS = "http://schema.org/naics";
  public static final String NAMED_POSITION = "http://schema.org/namedPosition";
  public static final String NATURAL_PROGRESSION = "http://schema.org/naturalProgression";
  public static final String NON_PROPRIETARY_NAME = "http://schema.org/nonProprietaryName";
  public static final String NORMAL_RANGE = "http://schema.org/normalRange";
  public static final String NUM_ADULTS = "http://schema.org/numAdults";
  public static final String NUM_CHILDREN = "http://schema.org/numChildren";
  public static final String NUM_TRACKS = "http://schema.org/numTracks";
  public static final String NUMBER_OF_EPISODES = "http://schema.org/numberOfEpisodes";
  public static final String NUMBER_OF_PAGES = "http://schema.org/numberOfPages";
  public static final String NUMBER_OF_SEASONS = "http://schema.org/numberOfSeasons";
  public static final String OCCUPATIONAL_CATEGORY = "http://schema.org/occupationalCategory";
  public static final String OFFER_COUNT = "http://schema.org/offerCount";
  public static final String OPENING_HOURS = "http://schema.org/openingHours";
  public static final String OPENS = "http://schema.org/opens";
  public static final String OPERATING_SYSTEM = "http://schema.org/operatingSystem";
  public static final String ORDER_DATE = "http://schema.org/orderDate";
  public static final String ORDER_NUMBER = "http://schema.org/orderNumber";
  public static final String OUTCOME = "http://schema.org/outcome";
  public static final String OVERDOSAGE = "http://schema.org/overdosage";
  public static final String OVERVIEW = "http://schema.org/overview";
  public static final String OWNED_FROM = "http://schema.org/ownedFrom";
  public static final String OWNED_THROUGH = "http://schema.org/ownedThrough";
  public static final String PAGE_END = "http://schema.org/pageEnd";
  public static final String ORG_PAGE_START = "http://schema.org/pageStart";
  public static final String PAGINATION = "http://schema.org/pagination";
  public static final String PARTY_SIZE = "http://schema.org/partySize";
  public static final String PATHOPHYSIOLOGY = "http://schema.org/pathophysiology";
  public static final String PAYMENT_ACCEPTED = "http://schema.org/paymentAccepted";
  public static final String PAYMENT_DUE = "http://schema.org/paymentDue";
  public static final String PAYMENT_METHOD_ID = "http://schema.org/paymentMethodId";
  public static final String PAYMENT_URL = "http://schema.org/paymentUrl";
  public static final String PERMISSIONS = "http://schema.org/permissions";
  public static final String PHASE = "http://schema.org/phase";
  public static final String PHYSIOLOGICAL_BENEFITS = "http://schema.org/physiologicalBenefits";
  public static final String PICKUP_TIME = "http://schema.org/pickupTime";
  public static final String PLAYER_TYPE = "http://schema.org/playerType";
  public static final String POLYGON = "http://schema.org/polygon";
  public static final String POPULATION = "http://schema.org/population";
  public static final String POSITION = "http://schema.org/position";
  public static final String POSSIBLE_COMPLICATION = "http://schema.org/possibleComplication";
  public static final String POST_OFFICE_BOX_NUMBER = "http://schema.org/postOfficeBoxNumber";
  public static final String POST_OP = "http://schema.org/postOp";
  public static final String POSTAL_CODE = "http://schema.org/postalCode";
  public static final String PRE_OP = "http://schema.org/preOp";
  public static final String PREGNANCY_WARNING = "http://schema.org/pregnancyWarning";
  public static final String PREP_TIME = "http://schema.org/prepTime";
  public static final String PREPARATION = "http://schema.org/preparation";
  public static final String PRESCRIBING_INFO = "http://schema.org/prescribingInfo";
  public static final String PREVIOUS_START_DATE = "http://schema.org/previousStartDate";
  public static final String PRICE = "http://schema.org/price";
  public static final String PRICE_CURRENCY = "http://schema.org/priceCurrency";
  public static final String PRICE_RANGE = "http://schema.org/priceRange";
  public static final String PRICE_TYPE = "http://schema.org/priceType";
  public static final String PRICE_VALID_UNTIL = "http://schema.org/priceValidUntil";
  public static final String PRINT_COLUMN = "http://schema.org/printColumn";
  public static final String PRINT_EDITION = "http://schema.org/printEdition";
  public static final String PRINT_PAGE = "http://schema.org/printPage";
  public static final String PRINT_SECTION = "http://schema.org/printSection";
  public static final String PROCEDURE = "http://schema.org/procedure";
  public static final String PROCESSING_TIME = "http://schema.org/processingTime";
  public static final String PROCESSOR_REQUIREMENTS = "http://schema.org/processorRequirements";
  public static final String PRODUCT_ID = "http://schema.org/productID";
  public static final String PROFICIENCY_LEVEL = "http://schema.org/proficiencyLevel";
  public static final String PROGRAM_NAME = "http://schema.org/programName";
  public static final String PROGRAMMING_MODEL = "http://schema.org/programmingModel";
  public static final String PROPRIETARY_NAME = "http://schema.org/proprietaryName";
  public static final String PROTEIN_CONTENT = "http://schema.org/proteinContent";
  public static final String PUBLICATION_TYPE = "http://schema.org/publicationType";
  public static final String PUBLISHING_PRINCIPLES = "http://schema.org/publishingPrinciples";
  public static final String QUALIFICATIONS = "http://schema.org/qualifications";
  public static final String QUESTION = "http://schema.org/question";
  public static final String RATING_COUNT = "http://schema.org/ratingCount";
  public static final String RATING_VALUE = "http://schema.org/ratingValue";
  public static final String READONLY_VALUE = "http://schema.org/readonlyValue";
  public static final String RECIPE_CATEGORY = "http://schema.org/recipeCategory";
  public static final String RECIPE_CUISINE = "http://schema.org/recipeCuisine";
  public static final String RECIPE_INSTRUCTIONS = "http://schema.org/recipeInstructions";
  public static final String RECIPE_YIELD = "http://schema.org/recipeYield";
  public static final String RECOMMENDATION_STRENGTH = "http://schema.org/recommendationStrength";
  public static final String RELATED_LINK = "http://schema.org/relatedLink";
  public static final String RELEASE_DATE = "http://schema.org/releaseDate";
  public static final String RELEASE_NOTES = "http://schema.org/releaseNotes";
  public static final String REPETITIONS = "http://schema.org/repetitions";
  public static final String REPLY_TO_URL = "http://schema.org/replyToUrl";
  public static final String REPRESENTATIVE_OF_PAGE = "http://schema.org/representativeOfPage";
  public static final String REQUIRED_GENDER = "http://schema.org/requiredGender";
  public static final String REQUIRED_MAX_AGE = "http://schema.org/requiredMaxAge";
  public static final String REQUIRED_MIN_AGE = "http://schema.org/requiredMinAge";
  public static final String REQUIREMENTS = "http://schema.org/requirements";
  public static final String REQUIRES_SUBSCRIPTION = "http://schema.org/requiresSubscription";
  public static final String RESERVATION_ID = "http://schema.org/reservationId";
  public static final String RESPONSIBILITIES = "http://schema.org/responsibilities";
  public static final String REST_PERIODS = "http://schema.org/restPeriods";
  public static final String REVIEW_BODY = "http://schema.org/reviewBody";
  public static final String REVIEW_COUNT = "http://schema.org/reviewCount";
  public static final String RISKS = "http://schema.org/risks";
  public static final String RUNTIME = "http://schema.org/runtime";
  public static final String SAFETY_CONSIDERATION = "http://schema.org/safetyConsideration";
  public static final String SALARY_CURRENCY = "http://schema.org/salaryCurrency";
  public static final String SAME_AS = "http://schema.org/sameAs";
  public static final String SAMPLE_TYPE = "http://schema.org/sampleType";
  public static final String SATURATED_FAT_CONTENT = "http://schema.org/saturatedFatContent";
  public static final String SCHEDULED_TIME = "http://schema.org/scheduledTime";
  public static final String SCREENSHOT = "http://schema.org/screenshot";
  public static final String SEASON_NUMBER = "http://schema.org/seasonNumber";
  public static final String SEAT_NUMBER = "http://schema.org/seatNumber";
  public static final String SEAT_ROW = "http://schema.org/seatRow";
  public static final String SEAT_SECTION = "http://schema.org/seatSection";
  public static final String SERIAL_NUMBER = "http://schema.org/serialNumber";
  public static final String SERVES_CUISINE = "http://schema.org/servesCuisine";
  public static final String SERVICE_TYPE = "http://schema.org/serviceType";
  public static final String SERVICE_URL = "http://schema.org/serviceUrl";
  public static final String SERVING_SIZE = "http://schema.org/servingSize";
  public static final String SIGNIFICANCE = "http://schema.org/significance";
  public static final String SIGNIFICANT_LINK = "http://schema.org/significantLink";
  public static final String SKILLS = "http://schema.org/skills";
  public static final String SKU = "http://schema.org/sku";
  public static final String SODIUM_CONTENT = "http://schema.org/sodiumContent";
  public static final String SOFTWARE_VERSION = "http://schema.org/softwareVersion";
  public static final String SPECIAL_COMMITMENTS = "http://schema.org/specialCommitments";
  public static final String STAGE_AS_NUMBER = "http://schema.org/stageAsNumber";
  public static final String STATUS = "http://schema.org/status";
  public static final String START_DATE = "http://schema.org/startDate";
  public static final String START_TIME = "http://schema.org/startTime";
  public static final String STEP = "http://schema.org/step";
  public static final String STEP_VALUE = "http://schema.org/stepValue";
  public static final String STORAGE_REQUIREMENTS = "http://schema.org/storageRequirements";
  public static final String STREET_ADDRESS = "http://schema.org/streetAddress";
  public static final String STRENGTH_UNIT = "http://schema.org/strengthUnit";
  public static final String STRENGTH_VALUE = "http://schema.org/strengthValue";
  public static final String STRUCTURAL_CLASS = "http://schema.org/structuralClass";
  public static final String SUB_STAGE_SUFFIX = "http://schema.org/subStageSuffix";
  public static final String SUBTYPE = "http://schema.org/subtype";
  public static final String SUGAR_CONTENT = "http://schema.org/sugarContent";
  public static final String SUGGESTED_GENDER = "http://schema.org/suggestedGender";
  public static final String SUGGESTED_MAX_AGE = "http://schema.org/suggestedMaxAge";
  public static final String SUGGESTED_MIN_AGE = "http://schema.org/suggestedMinAge";
  public static final String TARGET_DESCRIPTION = "http://schema.org/targetDescription";
  public static final String TARGET_NAME = "http://schema.org/targetName";
  public static final String TARGET_PLATFORM = "http://schema.org/targetPlatform";
  public static final String TARGET_POPULATION = "http://schema.org/targetPopulation";
  public static final String TARGET_URL = "http://schema.org/targetUrl";
  public static final String TAX_ID = "http://schema.org/taxID";
  public static final String TELEPHONE = "http://schema.org/telephone";
  public static final String TEMPORAL = "http://schema.org/temporal";
  public static final String TEXT = "http://schema.org/text";
  public static final String THUMBNAIL_URL = "http://schema.org/thumbnailUrl";
  public static final String TICKER_SYMBOL = "http://schema.org/tickerSymbol";
  public static final String TICKET_NUMBER = "http://schema.org/ticketNumber";
  public static final String TICKET_TOKEN = "http://schema.org/ticketToken";
  public static final String TIME_REQUIRED = "http://schema.org/timeRequired";
  public static final String TISSUE_SAMPLE = "http://schema.org/tissueSample";
  public static final String TITLE = "http://schema.org/title";
  public static final String TO_LOCATION = "http://schema.org/toLocation";
  public static final String TOTAL_PRICE = "http://schema.org/totalPrice";
  public static final String TOTAL_TIME = "http://schema.org/totalTime";
  public static final String TRACKING_NUMBER = "http://schema.org/trackingNumber";
  public static final String TRACKING_URL = "http://schema.org/trackingUrl";
  public static final String TRAIN_NAME = "http://schema.org/trainName";
  public static final String TRAIN_NUMBER = "http://schema.org/trainNumber";
  public static final String TRANS_FAT_CONTENT = "http://schema.org/transFatContent";
  public static final String TRANSCRIPT = "http://schema.org/transcript";
  public static final String TRANSMISSION_METHOD = "http://schema.org/transmissionMethod";
  public static final String TYPICAL_AGE_RANGE = "http://schema.org/typicalAgeRange";
  public static final String UNIT_CODE = "http://schema.org/unitCode";
  public static final String UNSATURATED_FAT_CONTENT = "http://schema.org/unsaturatedFatContent";
  public static final String UPLOAD_DATE = "http://schema.org/uploadDate";
  public static final String UPVOTE_COUNT = "http://schema.org/upvoteCount";
  public static final String URL_TEMPLATE = "http://schema.org/urlTemplate";
  public static final String VALID_FOR = "http://schema.org/validFor";
  public static final String VALID_FROM = "http://schema.org/validFrom";
  public static final String VALID_THROUGH = "http://schema.org/validThrough";
  public static final String VALID_UNTIL = "http://schema.org/validUntil";
  public static final String VALUE = "http://schema.org/value";
  public static final String VALUE_ADDED_TAX_INCLUDED = "http://schema.org/valueAddedTaxIncluded";
  public static final String VALUE_MAX_LENGTH = "http://schema.org/valueMaxLength";
  public static final String VALUE_MIN_LENGTH = "http://schema.org/valueMinLength";
  public static final String VALUE_NAME = "http://schema.org/valueName";
  public static final String VALUE_PATTERN = "http://schema.org/valuePattern";
  public static final String VALUE_REQUIRED = "http://schema.org/valueRequired";
  public static final String VAT_ID = "http://schema.org/vatID";
  public static final String VERSION = "http://schema.org/version";
  public static final String VIDEO_FRAME_SIZE = "http://schema.org/videoFrameSize";
  public static final String VIDEO_QUALITY = "http://schema.org/videoQuality";
  public static final String VOLUME_NUMBER = "http://schema.org/volumeNumber";
  public static final String WARNING = "http://schema.org/warning";
  public static final String WEB_CHECKIN_TIME = "http://schema.org/webCheckinTime";
  public static final String WORD_COUNT = "http://schema.org/wordCount";
  public static final String WORK_HOURS = "http://schema.org/workHours";
  public static final String WORKLOAD = "http://schema.org/workload";
  public static final String WORST_RATING = "http://schema.org/worstRating";

  public static List<String> getAll() {
    return Arrays.asList(GEO_JSON_LINE_STRING,
        GEO_JSON_POLYGON,
        GEO_JSON_MULTI_POINT,
        GEO_JSON_MULTI_LINE_STRING,
        GEO_JSON_MULTI_POLYGON,
        QUANTITATIVE_VALUE,
        PROPERTY_VALUE_SPECIFICATION,
        NUMBER,
        ORG_ENUMERATION,
        ALTITUDE,
        ACCEPTS_RESERVATIONS,
        ACCESS_CODE,
        ACCESSIBILITY_API,
        ACCESSIBILITY_CONTROL,
        ACCESSIBILITY_FEATURE,
        ACCESSIBILITY_HAZARD,
        ACTION,
        ORG_ACTIVE_INGREDIENT,
        ACTIVITY_DURATION,
        ACTIVITY_FREQUENCY,
        ADDITIONAL_NAME,
        ADDITIONAL_VARIABLE,
        ADDRESS_LOCALITY,
        ORG_ADDRESS_REGION,
        ADMINISTRATION_ROUTE,
        ALCOHOL_WARNING,
        ALGORITHM,
        ALIGNMENT_TYPE,
        ALTERNATE_NAME,
        ALTERNATIVE_HEADLINE,
        AMOUNT_OF_THIS_GOOD,
        ANSWER_COUNT,
        APPLICATION_CATEGORY,
        APPLICATION_SUB_CATEGORY,
        APPLICATION_SUITE,
        ARRIVAL_GATE,
        ARRIVAL_PLATFORM,
        ARRIVAL_TERMINAL,
        ORG_ARRIVAL_TIME,
        ARTICLE_BODY,
        ARTICLE_SECTION,
        ASPECT,
        ASSEMBLY,
        ASSEMBLY_VERSION,
        ASSOCIATED_PATHOPHYSIOLOGY,
        AUDIENCE_TYPE,
        AVAILABILITY_ENDS,
        AVAILABILITY_STARTS,
        AVAILABLE_FROM,
        AVAILABLE_THROUGH,
        AWARD,
        BACKGROUND,
        BASE_SALARY,
        BENEFITS,
        BEST_RATING,
        BILLING_INCREMENT,
        BIOMECHNICAL_CLASS,
        BIRTH_DATE,
        BITRATE,
        BOARDING_GROUP,
        BODY_LOCATION,
        BOOK_EDITION,
        BOOKING_TIME,
        BOOLEAN,
        BOX,
        BREADCRUMB,
        BREASTFEEDING_WARNING,
        BROWSER_REQUIREMENTS,
        BUS_NAME,
        BUS_NUMBER,
        CALORIES,
        CAPTION,
        CARBOHYDRATE_CONTENT,
        CARRIER_REQUIREMENTS,
        CHARACTER_NAME,
        CHECKIN_TIME,
        CHECKOUT_TIME,
        CHILD_MAX_AGE,
        CHILD_MIN_AGE,
        CHOLESTEROL_CONTENT,
        CIRCLE,
        CLINCAL_PHARMACOLOGY,
        CLIP_NUMBER,
        CLOSES,
        CODE_REPOSITORY,
        CODE_VALUE,
        CODING_SYSTEM,
        COLOR,
        COMMENT_COUNT,
        COMMENT_TEXT,
        COMMENT_TIME,
        CONFIRMATION_NUMBER,
        CONTACT_TYPE,
        CONTENT_RATING,
        CONTENT_SIZE,
        CONTENT_TYPE,
        CONTENT_URL,
        COOK_TIME,
        COOKING_METHOD,
        COPYRIGHT_YEAR,
        COST_CURRENCY,
        COST_ORIGIN,
        COST_PER_UNIT,
        COUNTRIES_NOT_SUPPORTED,
        COUNTRIES_SUPPORTED,
        CURRENCIES_ACCEPTED,
        DATE_CREATED,
        DATE_ISSUED,
        DATE_MODIFIED,
        DATE_POSTED,
        ORG_DATE_PUBLISHED,
        DATE_TIME,
        DATELINE,
        DEATH_DATE,
        DEPARTURE_GATE,
        DEPARTURE_PLATFORM,
        DEPARTURE_TERMINAL,
        DEPARTURE_TIME,
        DEPENDENCIES,
        DEVICE,
        DIET_FEATURES,
        DISCOUNT,
        DISCOUNT_CODE,
        DISCOUNT_CURRENCY,
        DISCUSSION_URL,
        DISSOLUTION_DATE,
        DISTANCE,
        DOOR_TIME,
        DOSAGE_FORM,
        DOSE_UNIT,
        DOSE_VALUE,
        DOWNLOAD_URL,
        DOWNVOTE_COUNT,
        DROPOFF_TIME,
        DRUG_UNIT,
        DUNS,
        DURATION,
        EDUCATION_REQUIREMENTS,
        EDUCATIONAL_FRAMEWORK,
        EDUCATIONAL_ROLE,
        ORG_EDUCATIONAL_USE,
        ELEVATION,
        EMAIL,
        EMBED_URL,
        EMPLOYMENT_TYPE,
        ENCODING_FORMAT,
        ENCODING_TYPE,
        END_DATE,
        END_TIME,
        ORG_EPIDEMIOLOGY,
        EPISODE_NUMBER,
        ESTIMATED_FLIGHT_DURATION,
        EVIDENCE_ORIGIN,
        ORG_EXERCISE_TYPE,
        EXIF_DATA,
        EXPECTED_ARRIVAL_FROM,
        EXPECTED_ARRIVAL_UNTIL,
        EXPECTED_PROGNOSIS,
        EXPERIENCE_REQUIREMENTS,
        EXPERT_CONSIDERATIONS,
        EXPIRES,
        FAMILY_NAME,
        FAT_CONTENT,
        FAX_NUMBER,
        FEATURE_LIST,
        FIBER_CONTENT,
        FILE_FORMAT,
        FILE_SIZE,
        FLIGHT_DISTANCE,
        FLIGHT_NUMBER,
        FOLLOWUP,
        FOOD_WARNING,
        FOUNDING_DATE,
        FREE,
        FREQUENCY,
        FROM_LOCATION,
        FUNCTION,
        FUNCTIONAL_CLASS,
        GENDER,
        GENRE,
        GIVEN_NAME,
        GLOBAL_LOCATION_NUMBER,
        GTIN_13,
        GTIN_14,
        GTIN_8,
        GUIDELINE_DATE,
        HAS_MAP,
        HEADLINE,
        HIGH_PRICE,
        HONORIFIC_PREFIX,
        HONORIFIC_SUFFIX,
        HOW_PERFORMED,
        HTTP_METHOD,
        IATA_CODE,
        ICAO_CODE,
        IMAGE,
        IN_LANGUAGE,
        INCENTIVES,
        INDUSTRY,
        INFECTIOUS_AGENT,
        INGREDIENTS,
        INSTALL_URL,
        INTENSITY,
        INTERACTION_COUNT,
        INTERACTIVITY_TYPE,
        IS_AVAILABLE_GENERICALLY,
        IS_BASED_ON_URL,
        IS_FAMILY_FRIENDLY,
        IS_GIFT,
        IS_PROPRIETARY,
        ISBN,
        ISIC_V_4,
        ISSN,
        ISSUE_NUMBER,
        ITEM_LIST_ELEMENT,
        ITEM_LIST_ORDER,
        JOB_TITLE,
        KEYWORDS,
        LABEL_DETAILS,
        LAST_REVIEWED,
        LATITUDE,
        LEARNING_RESOURCE_TYPE,
        LEGAL_NAME,
        LICENSE,
        LINE,
        LODGING_UNIT_DESCRIPTION,
        LOGO,
        LONGITUDE,
        LOW_PRICE,
        MAP,
        MAX_PRICE,
        MAX_VALUE,
        MEAL_SERVICE,
        MECHANISM_OF_ACTION,
        MEMBERSHIP_NUMBER,
        MEMORY_REQUIREMENTS,
        MENU,
        MIN_PRICE,
        MIN_VALUE,
        MODIFIED_TIME,
        MPN,
        MULTIPLE_VALUES,
        MUSCLE_ACTION,
        NAICS,
        NAMED_POSITION,
        NATURAL_PROGRESSION,
        NON_PROPRIETARY_NAME,
        NORMAL_RANGE,
        NUM_ADULTS,
        NUM_CHILDREN,
        NUM_TRACKS,
        NUMBER_OF_EPISODES,
        NUMBER_OF_PAGES,
        NUMBER_OF_SEASONS,
        OCCUPATIONAL_CATEGORY,
        OFFER_COUNT,
        OPENING_HOURS,
        OPENS,
        OPERATING_SYSTEM,
        ORDER_DATE,
        ORDER_NUMBER,
        OUTCOME,
        OVERDOSAGE,
        OVERVIEW,
        OWNED_FROM,
        OWNED_THROUGH,
        PAGE_END,
        ORG_PAGE_START,
        PAGINATION,
        PARTY_SIZE,
        PATHOPHYSIOLOGY,
        PAYMENT_ACCEPTED,
        PAYMENT_DUE,
        PAYMENT_METHOD_ID,
        PAYMENT_URL,
        PERMISSIONS,
        PHASE,
        PHYSIOLOGICAL_BENEFITS,
        PICKUP_TIME,
        PLAYER_TYPE,
        POLYGON,
        POPULATION,
        POSITION,
        POSSIBLE_COMPLICATION,
        POST_OFFICE_BOX_NUMBER,
        POST_OP,
        POSTAL_CODE,
        PRE_OP,
        PREGNANCY_WARNING,
        PREP_TIME,
        PREPARATION,
        PRESCRIBING_INFO,
        PREVIOUS_START_DATE,
        PRICE,
        PRICE_CURRENCY,
        PRICE_RANGE,
        PRICE_TYPE,
        PRICE_VALID_UNTIL,
        PRINT_COLUMN,
        PRINT_EDITION,
        PRINT_PAGE,
        PRINT_SECTION,
        PROCEDURE,
        PROCESSING_TIME,
        PROCESSOR_REQUIREMENTS,
        PRODUCT_ID,
        PROFICIENCY_LEVEL,
        PROGRAM_NAME,
        PROGRAMMING_MODEL,
        PROPRIETARY_NAME,
        PROTEIN_CONTENT,
        PUBLICATION_TYPE,
        PUBLISHING_PRINCIPLES,
        QUALIFICATIONS,
        QUESTION,
        RATING_COUNT,
        RATING_VALUE,
        READONLY_VALUE,
        RECIPE_CATEGORY,
        RECIPE_CUISINE,
        RECIPE_INSTRUCTIONS,
        RECIPE_YIELD,
        RECOMMENDATION_STRENGTH,
        RELATED_LINK,
        RELEASE_DATE,
        RELEASE_NOTES,
        REPETITIONS,
        REPLY_TO_URL,
        REPRESENTATIVE_OF_PAGE,
        REQUIRED_GENDER,
        REQUIRED_MAX_AGE,
        REQUIRED_MIN_AGE,
        REQUIREMENTS,
        REQUIRES_SUBSCRIPTION,
        RESERVATION_ID,
        RESPONSIBILITIES,
        REST_PERIODS,
        REVIEW_BODY,
        REVIEW_COUNT,
        RISKS,
        RUNTIME,
        SAFETY_CONSIDERATION,
        SALARY_CURRENCY,
        SAME_AS,
        SAMPLE_TYPE,
        SATURATED_FAT_CONTENT,
        SCHEDULED_TIME,
        SCREENSHOT,
        SEASON_NUMBER,
        SEAT_NUMBER,
        SEAT_ROW,
        SEAT_SECTION,
        SERIAL_NUMBER,
        SERVES_CUISINE,
        SERVICE_TYPE,
        SERVICE_URL,
        SERVING_SIZE,
        SIGNIFICANCE,
        SIGNIFICANT_LINK,
        SKILLS,
        SKU,
        SODIUM_CONTENT,
        SOFTWARE_VERSION,
        SPECIAL_COMMITMENTS,
        STAGE_AS_NUMBER,
        STATUS,
        START_DATE,
        START_TIME,
        STEP,
        STEP_VALUE,
        STORAGE_REQUIREMENTS,
        STREET_ADDRESS,
        STRENGTH_UNIT,
        STRENGTH_VALUE,
        STRUCTURAL_CLASS,
        SUB_STAGE_SUFFIX,
        SUBTYPE,
        SUGAR_CONTENT,
        SUGGESTED_GENDER,
        SUGGESTED_MAX_AGE,
        SUGGESTED_MIN_AGE,
        TARGET_DESCRIPTION,
        TARGET_NAME,
        TARGET_PLATFORM,
        TARGET_POPULATION,
        TARGET_URL,
        TAX_ID,
        TELEPHONE,
        TEMPORAL,
        TEXT,
        THUMBNAIL_URL,
        TICKER_SYMBOL,
        TICKET_NUMBER,
        TICKET_TOKEN,
        TIME_REQUIRED,
        TISSUE_SAMPLE,
        TITLE,
        TO_LOCATION,
        TOTAL_PRICE,
        TOTAL_TIME,
        TRACKING_NUMBER,
        TRACKING_URL,
        TRAIN_NAME,
        TRAIN_NUMBER,
        TRANS_FAT_CONTENT,
        TRANSCRIPT,
        TRANSMISSION_METHOD,
        TYPICAL_AGE_RANGE,
        UNIT_CODE,
        UNSATURATED_FAT_CONTENT,
        UPLOAD_DATE,
        UPVOTE_COUNT,
        URL_TEMPLATE,
        VALID_FOR,
        VALID_FROM,
        VALID_THROUGH,
        VALID_UNTIL,
        VALUE,
        VALUE_ADDED_TAX_INCLUDED,
        VALUE_MAX_LENGTH,
        VALUE_MIN_LENGTH,
        VALUE_NAME,
        VALUE_PATTERN,
        VALUE_REQUIRED,
        VAT_ID,
        VERSION,
        VIDEO_FRAME_SIZE,
        VIDEO_QUALITY,
        VOLUME_NUMBER,
        WARNING,
        WEB_CHECKIN_TIME,
        WORD_COUNT,
        WORK_HOURS,
        WORKLOAD,
        WORST_RATING);
  }
}
