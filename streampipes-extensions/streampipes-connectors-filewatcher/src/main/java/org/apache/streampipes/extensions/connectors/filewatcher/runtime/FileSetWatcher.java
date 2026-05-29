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

package org.apache.streampipes.extensions.connectors.filewatcher.runtime;

import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileFingerprint;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherCheckpoint;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;

public class FileSetWatcher {

  private static final Logger LOG = LoggerFactory.getLogger(FileSetWatcher.class);

  private final FileWatcherConfig config;
  private final FileWatcherCheckpointStore checkpointStore;
  private final CsvFileReader csvFileReader;
  private final EventMapper eventMapper;
  private final EventDelayExecutor eventDelayExecutor;

  public FileSetWatcher(FileWatcherConfig config,
                        FileWatcherCheckpointStore checkpointStore,
                        CsvFileReader csvFileReader,
                        EventMapper eventMapper) {
    this(config, checkpointStore, csvFileReader, eventMapper, Thread::sleep);
  }

  public FileSetWatcher(FileWatcherConfig config,
                        FileWatcherCheckpointStore checkpointStore,
                        CsvFileReader csvFileReader,
                        EventMapper eventMapper,
                        EventDelayExecutor eventDelayExecutor) {
    this.config = config;
    this.checkpointStore = checkpointStore;
    this.csvFileReader = csvFileReader;
    this.eventMapper = eventMapper;
    this.eventDelayExecutor = eventDelayExecutor;
  }

  public void poll(String adapterElementId, IEventCollector collector) throws IOException {
    LOG.debug("Polling WinCC archive files in directory '{}' for adapter '{}'.",
        config.directory(), adapterElementId);
    var files = discoverFiles();
    if (files.isEmpty()) {
      LOG.debug("No matching WinCC archive files found in '{}'.", config.directory());
      return;
    }

    var checkpoint = checkpointStore.load(adapterElementId);
    LOG.debug(
        "Loaded checkpoint for adapter '{}': currentFile='{}', sequence={}, lastProcessedRecord={}, knownGenerations={}.",
        adapterElementId,
        checkpoint.getCurrentFileName(),
        checkpoint.getCurrentSequence(),
        checkpoint.getLastProcessedRecord(),
        checkpoint.getProcessedGenerations().size()
    );
    processCurrentFile(adapterElementId, collector, checkpoint, files);
    processFollowingFiles(adapterElementId, collector, checkpoint, files);
  }

  private void processCurrentFile(String adapterElementId,
                                  IEventCollector collector,
                                  FileWatcherCheckpoint checkpoint,
                                  List<FileSlot> files) throws IOException {
    if (checkpoint.getCurrentFileName() == null || checkpoint.getCurrentFingerprint() == null) {
      LOG.debug("No existing checkpoint state found. Starting from the first available matching file.");
      return;
    }

    FileSlot currentSlot = files.stream()
        .filter(file -> file.fileName().equals(checkpoint.getCurrentFileName()))
        .findFirst()
        .orElse(null);

    if (currentSlot == null) {
      LOG.debug("Checkpoint file '{}' is no longer present in '{}'. Continuing with following files.",
          checkpoint.getCurrentFileName(), config.directory());
      return;
    }

    long startRecord = determineStartRecord(currentSlot, checkpoint);
    LOG.debug("Resuming file '{}' at record {}.", currentSlot.fileName(), startRecord);

    readFile(adapterElementId, collector, checkpoint, currentSlot, startRecord);
  }

  private long determineStartRecord(FileSlot currentSlot, FileWatcherCheckpoint checkpoint) {
    if (fingerprintsEqual(currentSlot.fingerprint(), checkpoint.getCurrentFingerprint())) {
      LOG.debug("File '{}' fingerprint unchanged. Continuing after record {}.",
          currentSlot.fileName(), checkpoint.getLastProcessedRecord());
      return checkpoint.getLastProcessedRecord() + 1;
    }

    if (config.singleFileGrowthMode() && isAppendedInPlace(currentSlot.fingerprint(), checkpoint.getCurrentFingerprint())) {
      LOG.debug("File '{}' grew in place. Continuing after record {}.",
          currentSlot.fileName(), checkpoint.getLastProcessedRecord());
      return checkpoint.getLastProcessedRecord() + 1;
    }

    LOG.debug("File '{}' fingerprint changed. Restarting from record 0.", currentSlot.fileName());
    return 0;
  }

  private boolean isAppendedInPlace(FileFingerprint currentFingerprint, FileFingerprint previousFingerprint) {
    return currentFingerprint.getSize() > previousFingerprint.getSize()
        && (!config.considerLastModified()
        || currentFingerprint.getLastModified() >= previousFingerprint.getLastModified());
  }

  private boolean fingerprintsEqual(FileFingerprint left, FileFingerprint right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null) {
      return false;
    }

    boolean sameContent = left.getSize() == right.getSize()
        && left.getContentHash().equals(right.getContentHash());

    if (!sameContent) {
      return false;
    }

    return !config.considerLastModified() || left.getLastModified() == right.getLastModified();
  }

  private void processFollowingFiles(String adapterElementId,
                                     IEventCollector collector,
                                     FileWatcherCheckpoint checkpoint,
                                     List<FileSlot> files) throws IOException {
    int startIndex = startIndex(files, checkpoint);
    LOG.debug("Processing following files starting at index {}.", startIndex);
    for (int offset = 0; offset < files.size(); offset++) {
      FileSlot candidate = files.get((startIndex + offset) % files.size());
      FileFingerprint processedFingerprint = checkpoint.getProcessedGenerations().get(candidate.fileName());
      if (processedFingerprint != null && fingerprintsEqual(processedFingerprint, candidate.fingerprint())) {
        LOG.debug("Stopping at file '{}' because this generation was already processed.", candidate.fileName());
        break;
      }

      LOG.debug("Reading new file generation '{}' from record 0.", candidate.fileName());
      readFile(adapterElementId, collector, checkpoint, candidate, 0);
    }
  }

  private void readFile(String adapterElementId,
                        IEventCollector collector,
                        FileWatcherCheckpoint checkpoint,
                        FileSlot fileSlot,
                        long startRecord) throws IOException {
    LOG.debug(
        "Reading file '{}' (sequence={}, size={}, lastModified={}) from record {} with interEventDelayMs={}.",
        fileSlot.fileName(),
        fileSlot.sequence(),
        fileSlot.fingerprint().getSize(),
        fileSlot.fingerprint().getLastModified(),
        startRecord,
        config.interEventDelayMs()
    );
    final boolean[] firstEvent = {true};
    var result = csvFileReader.readFrom(fileSlot.path(), config.parserSettings(), startRecord, (recordIndex, event) -> {
      delayBeforeNextEvent(firstEvent[0], fileSlot.fileName(), recordIndex);
      firstEvent[0] = false;
      if (LOG.isTraceEnabled()) {
        LOG.trace("Collected record {} from file '{}'.", recordIndex, fileSlot.fileName());
      }
      collector.collect(eventMapper.map(event));
      checkpoint.setCurrentFileName(fileSlot.fileName());
      checkpoint.setCurrentSequence(fileSlot.sequence());
      checkpoint.setCurrentFingerprint(fileSlot.fingerprint());
      checkpoint.setLastProcessedRecord(recordIndex);
      checkpoint.getProcessedGenerations().put(fileSlot.fileName(), fileSlot.fingerprint());

      try {
        checkpointStore.save(adapterElementId, checkpoint);
        if (LOG.isTraceEnabled()) {
          LOG.trace("Checkpoint saved for file '{}' at record {}.", fileSlot.fileName(), recordIndex);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    checkpoint.setCurrentFileName(fileSlot.fileName());
    checkpoint.setCurrentSequence(fileSlot.sequence());
    checkpoint.setCurrentFingerprint(fileSlot.fingerprint());
    checkpoint.setLastProcessedRecord(result.lastProcessedRecord());
    checkpoint.getProcessedGenerations().put(fileSlot.fileName(), fileSlot.fingerprint());
    checkpointStore.save(adapterElementId, checkpoint);
    LOG.debug(
        "Finished file '{}'. Emitted {} events, lastProcessedRecord={}.",
        fileSlot.fileName(),
        result.emittedEvents(),
        result.lastProcessedRecord()
    );
  }

  private void delayBeforeNextEvent(boolean firstEvent,
                                    String fileName,
                                    long recordIndex) {
    if (firstEvent || config.interEventDelayMs() <= 0) {
      return;
    }

    try {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Sleeping {} ms before record {} from file '{}'.",
            config.interEventDelayMs(), recordIndex, fileName);
      }
      eventDelayExecutor.sleep(config.interEventDelayMs());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while delaying replay for file " + fileName, e);
    }
  }

  List<FileSlot> discoverFiles() throws IOException {
    List<FileSlot> files = new ArrayList<>();
    try (var stream = Files.list(config.directory())) {
      stream.filter(Files::isRegularFile)
          .filter(path -> config.filePattern().matcher(path.getFileName().toString()).matches())
          .forEach(path -> files.add(toFileSlot(path)));
    }

    files.sort(Comparator.comparingLong(FileSlot::sequence).thenComparing(FileSlot::fileName));
    if (LOG.isDebugEnabled()) {
      LOG.debug("Discovered {} matching WinCC archive files: {}",
          files.size(),
          files.stream().map(file -> file.fileName() + "@" + file.sequence()).toList());
    }
    return files;
  }

  private int startIndex(List<FileSlot> files, FileWatcherCheckpoint checkpoint) {
    if (checkpoint.getCurrentFileName() == null) {
      return 0;
    }

    for (int i = 0; i < files.size(); i++) {
      FileSlot slot = files.get(i);
      if (slot.sequence() > checkpoint.getCurrentSequence()) {
        return i;
      }
      if (slot.sequence() == checkpoint.getCurrentSequence()
          && slot.fileName().compareTo(checkpoint.getCurrentFileName()) > 0) {
        return i;
      }
    }

    return 0;
  }

  private FileSlot toFileSlot(Path path) {
    String fileName = path.getFileName().toString();
    return new FileSlot(fileName, path, sequence(fileName), fingerprint(path));
  }

  private long sequence(String fileName) {
    Matcher matcher = config.filePattern().matcher(fileName);
    if (matcher.matches() && matcher.groupCount() >= 1) {
      try {
        return Long.parseLong(matcher.group(1));
      } catch (NumberFormatException ignored) {
        return Long.MAX_VALUE;
      }
    }

    return Long.MAX_VALUE;
  }

  private FileFingerprint fingerprint(Path path) {
    try {
      long size = Files.size(path);
      long lastModified = Files.getLastModifiedTime(path).toMillis();
      String hash = sha256(path);
      return new FileFingerprint(size, lastModified, hash);
    } catch (IOException e) {
      throw new RuntimeException("Could not create fingerprint for " + path.getFileName(), e);
    }
  }

  private String sha256(Path path) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      try (InputStream in = Files.newInputStream(path)) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
          digest.update(buffer, 0, read);
        }
      }

      return HexFormat.of().formatHex(digest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }
}
