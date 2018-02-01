package org.streampipes.manager.util;

import org.apache.commons.lang.RandomStringUtils;

public class TopicGenerator {

	public static String generateRandomTopic()
	{
		return "FZI.SEPA." +RandomStringUtils.randomAlphabetic(20);
	}
}
