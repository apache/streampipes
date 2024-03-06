package org.apache.streampipes.sinks.notifications.jvm.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;

import org.junit.Test;

import java.util.ArrayList;

import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.KEY_SILENT_PERIOD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class INotificationDataSinkMigratorTest {

  @Test
  public void migrate() {

    INotificationDataSinkMigrator migrator = spy(INotificationDataSinkMigrator.class);
    IDataSinkParameterExtractor extractor = mock(IDataSinkParameterExtractor.class);

    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation();
    dataSinkInvocation.setStaticProperties(new ArrayList<>());

    MigrationResult<DataSinkInvocation> result = migrator.migrate(dataSinkInvocation, extractor);

    assertEquals(result.element().getStaticProperties().size(), 1);
    assertEquals(result.element().getStaticProperties().get(0).getInternalName(), KEY_SILENT_PERIOD);
  }
}