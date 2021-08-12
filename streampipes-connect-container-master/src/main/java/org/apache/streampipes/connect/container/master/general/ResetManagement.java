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

package org.apache.streampipes.connect.container.master.general;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.manager.pipeline.PipelineCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineCanvasMetadataCacheManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.client.file.FileMetadata;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResetManagement {
    // This class should be moved into another package. I moved it here because I got a cyclic maven
    // dependency between this package and streampipes-pipeline-management
    // See in issue [STREAMPIPES-405]

    private static final Logger logger = LoggerFactory.getLogger(ResetManagement.class);

    /**
     * Remove all configurations for this user. This includes:
     * [pipeline assembly cache, pipelines, adapters, files]
     * @param username
     */
    public static void reset(String username) {
        logger.info("Start resetting the system");

        // Clear pipeline assembly Cache
        PipelineCacheManager.removeCachedPipeline(username);
        PipelineCanvasMetadataCacheManager.removeCanvasMetadataFromCache(username);

        // Stop and delete all pipelines
        List<Pipeline> allPipelines = PipelineManager.getOwnPipelines(username);
        allPipelines.forEach(pipeline -> {
            PipelineManager.stopPipeline(pipeline.getPipelineId(), true);
            PipelineManager.deletePipeline(pipeline.getPipelineId());
        });

        // Stop and delete all adapters
        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        try {
            List<AdapterDescription> allAdapters = adapterMasterManagement.getAllAdapters();
            allAdapters.forEach(adapterDescription -> {
                try {
                    adapterMasterManagement.deleteAdapter(adapterDescription.getId());
                } catch (AdapterException e) {
                    logger.error("Failed to delete adapter with id: " + adapterDescription.getAdapterId(), e);
                }
            });
        } catch (AdapterException e) {
            logger.error("Failed to load all adapter descriptions", e);
        }

        // Stop and delete all files
        List<FileMetadata> allFiles = FileManager.getAllFiles();
        allFiles.forEach(fileMetadata -> {
            FileManager.deleteFile(fileMetadata.getFileId());
        });

        logger.info("Resetting the system was completed");
    }
}
