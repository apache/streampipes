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
package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.storage.UserManagementService;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.storage.couchdb.impl.PipelineStorageImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        UserManagementService.class,
        PipelineManager.class,
        Operations.class})
public class TestPipelineManager {

    @Before
    public  void before() {
        PowerMockito.mockStatic(
                UserManagementService.class);
    }

    @Test
    public void testGetOwnPipelines() {
        // Prepare
        Pipeline expectedPipeline = this.getPipeline();
        List<Pipeline> expected = Arrays.asList(expectedPipeline);

        UserService userService = mock(UserService.class);
        when(userService.getOwnPipelines(any(String.class))).thenReturn(expected);
        when(UserManagementService.getUserService()).thenReturn(userService);

        // Test
        List<Pipeline> result = PipelineManager.getOwnPipelines("user@test.com");

        // Assertions
        assertEquals(1, result.size());
        assertEquals(this.getPipelineName(), result.get(0).getName());
    }

    @Test
    public void testGetPipeline() {
        // Prepare
        Pipeline expectedPipeline = this.getPipeline();

        PipelineStorageImpl pipelineStorageImpl = mock(PipelineStorageImpl.class);
        when(pipelineStorageImpl.getPipeline(any(String.class))).thenReturn(expectedPipeline);
        PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getPipelineStorage")).toReturn(pipelineStorageImpl);

        // Test
        Pipeline result = PipelineManager.getPipeline("pipelineid");

        // Assertions
        assertNotNull(result);
        assertEquals(this.getPipelineName(), result.getName());
    }


    @Test
    public void testStartPipeline() {
        // Prepare
        PipelineOperationStatus expectedPipelineOperationStatus = getPipelineOperationStatus();
        PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getPipeline", String.class)).toReturn(getPipeline());
        PowerMockito.stub(PowerMockito.method(Operations.class, "startPipeline", Pipeline.class)).toReturn(expectedPipelineOperationStatus);

        // Test
        PipelineOperationStatus result = PipelineManager.startPipeline("pipelineId");

        // Assertions
        assertNotNull(result);
        assertEquals(expectedPipelineOperationStatus.getPipelineName(), result.getPipelineName());
    }

    @Test
    public void testStopPipeline() {
        // Prepare
        PipelineOperationStatus expectedPipelineOperationStatus = getPipelineOperationStatus();
        PowerMockito.stub(PowerMockito.method(PipelineManager.class, "getPipeline", String.class)).toReturn(getPipeline());
        PowerMockito.stub(PowerMockito.method(Operations.class, "stopPipeline", Pipeline.class, boolean.class)).toReturn(expectedPipelineOperationStatus);

        // Test
        PipelineOperationStatus result = PipelineManager.stopPipeline("pipelineId", true);

        // Assertions
        assertNotNull(result);
        assertEquals(expectedPipelineOperationStatus.getPipelineName(), result.getPipelineName());
    }

    @Test
    public void testAddPipeline() {
        // Prepare
        PipelineOperationStatus expectedPipelineOperationStatus = getPipelineOperationStatus();
        PowerMockito.mockStatic(Operations.class);

        // Test
        String result = PipelineManager.addPipeline("test@user.com", getPipeline());

        // Assertions
        assertNotNull(result);
    }

    private String getPipelineName() {
        return "Test Pipeline";
    }

    private Pipeline getPipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineId("testId");
        pipeline.setName(this.getPipelineName());
        return pipeline;
    }

    private PipelineOperationStatus getPipelineOperationStatus() {
        return new PipelineOperationStatus("", getPipelineName(),"", new ArrayList<>());
    }
}