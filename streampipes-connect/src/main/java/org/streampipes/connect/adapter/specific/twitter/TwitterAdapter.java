/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapter.specific.twitter;


import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.XSD;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;

public class TwitterAdapter extends SpecificDataStreamAdapter {
    public static final String ID = "org.streampipes.adapter.specific.twitter";

    private TwitterStream twitterStream;

    public TwitterAdapter() {
        super();
    }

    public TwitterAdapter(AdapterDescription adapterDescription, boolean debug) {
        super(adapterDescription, debug);
    }

    public TwitterAdapter(AdapterDescription adapterDescription) {
        super(adapterDescription);
    }

    @Override
    public AdapterDescription declareModel() {
        AdapterDescription adapterDescription = new AdapterStreamDescription();
        adapterDescription.setAdapterId(ID);
        adapterDescription.setUri("http://streampipes.org/adapter/specific/twitter");

        return adapterDescription;
    }

    @Override
    public void startAdapter() throws AdapterException {

    }

    @Override
    public Adapter getInstance(AdapterDescription adapterDescription) {
        return null;
    }

//    @Override
    public void run(AdapterDescription adapterDescription) {

        StatusListener listener = getListener();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setJSONStoreEnabled(true);
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
//        twitterStream.setOAuthAccessToken(new AccessToken("", ""));


        twitterStream.addListener(listener);

//        twitterStream.sample();


        FilterQuery tweetFilterQuery = new FilterQuery(); // See
        tweetFilterQuery.track(new String[]{"Bieber", "Teletubbies"}); // OR on keywords
        tweetFilterQuery.locations(new double[][]{new double[]{-126.562500,30.448674},
                new double[]{-61.171875,44.087585
                }}); // See https://dev.twitter.com/docs/streaming-apis/parameters#locations for proper location doc.
//Note that not all tweets have location metadata set.
        tweetFilterQuery.language(new String[]{"en"}); //

        twitterStream.filter(tweetFilterQuery);


    }

    @Override
    public GuessSchema getSchema(AdapterDescription adapterDescription) {
        //TODO not needed or return fixed schema
        GuessSchema guessSchema = new GuessSchema();
        EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
        eventPropertyPrimitive.setRuntimeName("bb");
        eventPropertyPrimitive.setRuntimeType(XSD._string.toString());
        EventSchema eventSchema = new EventSchema();
        eventSchema.setEventProperties(Arrays.asList(eventPropertyPrimitive));

        guessSchema.setEventSchema(eventSchema);

        return guessSchema;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void stopAdapter() {
        twitterStream.shutdown();
    }

    private StatusListener getListener() {
        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
//                System.out.println("Got a status deletion notice id:" + arg.getStatusId());
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
//                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
//                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onStatus(Status status) {

                String s = "{\"a\" : [{\"user\": \"" + status.getUser().getName() + "\", \"text\":\"" + status.getText() + "\"}]}";
                // TODO uncomment again
//
//                try {
//                    System.out.println(s);
//                    parser.parse(IOUtils.toInputStream(s, "UTF-8"), stp);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                System.out.println("============================");
                System.out.println(status.getUser().getName() + " : " + status.getText());
                System.out.println(TwitterObjectFactory.getRawJSON(status));
                System.out.println("============================");
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
        };

        return listener;


    }
}

//    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/twitter";
//
//    public static void main(String... args) throws TwitterException {
//        streamFeed(null, null);
//    }
//
//    public static Twitter getTwitterinstance() {
//        ConfigurationBuilder cb = new ConfigurationBuilder();
//        cb.setDebugEnabled(true);
//        TwitterFactory tf = new TwitterFactory(cb.build());
//        return tf.getInstance();
//
//    }
//
//    public static List<String> getTimeLine() throws TwitterException {
//        Twitter twitter = getTwitterinstance();
//
//        return twitter.getHomeTimeline().stream()
//                .map(item -> item.getText())
//                .collect(Collectors.toList());
//    }
//
//    public static void streamFeed(SendToPipeline stp, Parser parser) {
//
//        StatusListener listener = new StatusListener(){
//
//            @Override
//            public void onException(Exception e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onDeletionNotice(StatusDeletionNotice arg) {
////                System.out.println("Got a status deletion notice id:" + arg.getStatusId());
//            }
//
//            @Override
//            public void onScrubGeo(long userId, long upToStatusId) {
////                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
//            }
//
//            @Override
//            public void onStallWarning(StallWarning warning) {
////                System.out.println("Got stall warning:" + warning);
//            }
//
//            @Override
//            public void onStatus(Status status) {
//
//                String s = "{\"a\" : [{\"user\": \"" + status.getUser().getName() + "\", \"text\":\"" + status.getText() + "\"}]}";
//
//                try {
//                    System.out.println(s);
//                    parser.parse(IOUtils.toInputStream(s, "UTF-8"), stp);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("============================");
//                System.out.println(status.getUser().getName() + " : " + status.getText());
//                System.out.println(TwitterObjectFactory.getRawJSON(status));
//                System.out.println("============================");
//            }
//
//            @Override
//            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
//            }
//        };
//
//
//    }
//
//
//    public TwitterProtocol() {
//    }
//
//    public TwitterProtocol(Parser parser, Format format) {
//        super(parser, format);
//    }
//
//
//    @Override
//    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
//        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
//        String brokerUrl = extractor.singleValue("broker_url");
//
//        return new TwitterProtocol(parser, format);
//    }
//
//    @Override
//    public ProtocolDescription declareModel() {
//        ProtocolDescription pd = new ProtocolDescription(ID,"Twitter (Stream)","This is the " +
//                "description for the Twitter connection");
//        FreeTextStaticProperty broker = new FreeTextStaticProperty("broker_url", "Broker URL",
//                "This property defines the URL of the Kafka broker.");
//
//        pd.setSourceType("STREAM");
//
//        //TODO remove, just for debugging
////        broker.setValue("141.21.42.75:9092");
////        topic.setValue("SEPA.SEP.Random.Number.Json");
//
//        pd.addConfig(broker);
//        return pd;
//    }
//
//    @Override
//    public GuessSchema getGuessSchema() {
//        //TODO not needed or return fixed schema
//        GuessSchema guessSchema = new GuessSchema();
//        EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
//        eventPropertyPrimitive.setRuntimeName("bb");
//        eventPropertyPrimitive.setRuntimeType(XSD._string.toString());
//        EventSchema eventSchema = new EventSchema();
//        eventSchema.setEventProperties(Arrays.asList(eventPropertyPrimitive));
//
//        guessSchema.setEventSchema(eventSchema);
//
//        return guessSchema;
//    }
//
//    @Override
//    public List<Map<String, Object>> getNElements(int n) {
//        // TODO not needed
//        return new ArrayList<>();
//    }
//
//    @Override
//    public void run(AdapterPipeline adapterPipeline) {
//        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
//        streamFeed(stk, parser);
//    }
//
//    @Override
//    public void stopAdapter() {
//
//        // TODO
//    }
//
//    @Override
//    public String getId() {
//        return ID;
//    }
