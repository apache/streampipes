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
package org.apache.streampipes.pe.flink.processor.absence;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.flink.processor.and.TimeUnitConverter;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternFlatSelectFunction;
import org.apache.flink.cep.PatternFlatTimeoutFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class AbsenceProgram extends FlinkDataProcessorProgram<AbsenceParameters> {

  public AbsenceProgram(AbsenceParameters params) {
    super(params);
  }

  @Override
  public DataStream<Event> getApplicationLogic(DataStream<Event>... messageStream) {

    Time time = TimeUnitConverter.toTime(params.getTimeUnit(), params.getTimeWindowSize());

    DataStream<Tuple2<Boolean, Event>> stream1 =
        messageStream[0].flatMap(new FlatMapFunction<Event, Tuple2<Boolean, Event>>() {
          @Override
          public void flatMap(Event in, Collector<Tuple2<Boolean, Event>> out) throws
              Exception {
            out.collect(new Tuple2<>(true, in));
          }
        });

    DataStream<Tuple2<Boolean, Event>> stream2 =
        messageStream[1].flatMap(new FlatMapFunction<Event, Tuple2<Boolean, Event>>() {
          @Override
          public void flatMap(Event in, Collector<Tuple2<Boolean, Event>> out) throws
              Exception {
            out.collect(new Tuple2<>(false, in));
          }
        });

    DataStream<Tuple2<Boolean, Event>> joinedStreams = stream2.union(stream1);


    Pattern<Tuple2<Boolean, Event>, Tuple2<Boolean, Event>> matchedEvents =
        Pattern.<Tuple2<Boolean, Event>>begin("start")
            .where(new SimpleCondition<Tuple2<Boolean, Event>>() {
              @Override
              public boolean filter(Tuple2<Boolean, Event> ride) throws Exception {
                return ride.f0;
              }
            })
            .next("end")
            .where(new SimpleCondition<Tuple2<Boolean, Event>>() {
              @Override
              public boolean filter(Tuple2<Boolean, Event> ride) throws Exception {
                return !ride.f0;
              }
            });

    PatternStream<Tuple2<Boolean, Event>> patternStream = CEP.pattern(joinedStreams, matchedEvents
        .within(time));

    OutputTag<Tuple2<Boolean, Event>> timedout = new OutputTag<Tuple2<Boolean, Event>>("timedout") {
    };

    SingleOutputStreamOperator<Tuple2<Boolean, Event>> matched = patternStream.flatSelect(
        timedout,
        new TimedOut(),
        new FlatSelectNothing<>()
    );

    return matched.getSideOutput(timedout).flatMap(new FlatMapFunction<Tuple2<Boolean, Event>,
        Event>() {
      @Override
      public void flatMap(Tuple2<Boolean, Event> in, Collector<Event> out) throws
          Exception {
        out.collect(in.f1);
      }
    });
  }

  public static class TimedOut implements PatternFlatTimeoutFunction<Tuple2<Boolean, Event>,
      Tuple2<Boolean, Event>> {
    @Override
    public void timeout(Map<String, List<Tuple2<Boolean, Event>>> map, long l,
                        Collector<Tuple2<Boolean, Event>> collector) throws Exception {
      Tuple2<Boolean, Event> rideStarted = map.get("start").get(0);
      collector.collect(rideStarted);
    }
  }

  public static class FlatSelectNothing<T> implements PatternFlatSelectFunction<T, T> {
    @Override
    public void flatSelect(Map<String, List<T>> pattern, Collector<T> collector) {

    }
  }
}
