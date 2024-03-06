package org.apache.streampipes.sinks.notifications.jvm.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import org.junit.Test;

import java.util.ArrayList;

import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.DEFAULT_WAITING_TIME_MINUTES;
import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.KEY_SILENT_PERIOD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class INotificationDataSinkMigratorTest {

  @Test
  public void testNotificationDataSinkMigration() {

    INotificationDataSinkMigrator migrator = spy(INotificationDataSinkMigrator.class);
    IDataSinkParameterExtractor extractor = mock(IDataSinkParameterExtractor.class);

    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation();
    dataSinkInvocation.setStaticProperties(new ArrayList<>());

    MigrationResult<DataSinkInvocation> result = migrator.migrate(dataSinkInvocation, extractor);

    assertEquals(1, result.element().getStaticProperties().size());

    var property = (FreeTextStaticProperty) result.element().getStaticProperties().get(0);
    assertEquals(KEY_SILENT_PERIOD, property.getInternalName());
    assertEquals(DEFAULT_WAITING_TIME_MINUTES, Integer.valueOf(property.getValue()).intValue());
  }
}