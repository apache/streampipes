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
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileGenerationState;
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
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSetWatcher {

  private static final Logger LOG = LoggerFactory.getLogger(FileSetWatcher.class);

  private final Path directory;
  private final Pattern filePattern;
  private final ToLongFunction<String> fileSequence;
  private final boolean chronological;
  private final boolean considerLastModified;
  private final int interEventDelayMs;
  private final FileWatcherCheckpointStore checkpointStore;
  private final FileRecordReader reader;
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
    this(config.directory(), config.filePattern(), name -> sequence(config.filePattern(), name),
        (path, start, consumer) -> csvFileReader.readFrom(path, config.parserSettings(), start, consumer),
        checkpointStore, false, config.considerLastModified(), config.interEventDelayMs(), eventMapper, eventDelayExecutor);
  }

  public FileSetWatcher(Path directory, Pattern filePattern, ToLongFunction<String> fileSequence,
                        FileRecordReader reader, FileWatcherCheckpointStore checkpointStore) {
    this(directory, filePattern, fileSequence, reader, checkpointStore, true, false, 0, EventMapper.identity(), Thread::sleep);
  }

  private FileSetWatcher(Path directory, Pattern filePattern, ToLongFunction<String> fileSequence,
                         FileRecordReader reader, FileWatcherCheckpointStore checkpointStore,
                         boolean chronological, boolean considerLastModified, int interEventDelayMs, EventMapper eventMapper,
                         EventDelayExecutor eventDelayExecutor) {
    this.chronological = chronological;
    this.directory = directory;
    this.filePattern = filePattern;
    this.fileSequence = fileSequence;
    this.reader = reader;
    this.considerLastModified = considerLastModified;
    this.interEventDelayMs = interEventDelayMs;
    this.checkpointStore = checkpointStore;
    this.eventMapper = eventMapper;
    this.eventDelayExecutor = eventDelayExecutor;
  }

  public void poll(String adapterElementId, IEventCollector collector) throws IOException {
    LOG.debug("Polling archive files in directory '{}' for adapter '{}'.",
        directory, adapterElementId);
    var checkpoint = checkpointStore.load(adapterElementId);
    if (chronological) {
      pollDailyFiles(adapterElementId, collector, checkpoint);
      return;
    }
    var files = discoverFiles();
    if (files.isEmpty()) {
      return;
    }
    LOG.debug(
        "Loaded checkpoint for adapter '{}': currentFile='{}', sequence={}, knownGenerations={}.",
        adapterElementId,
        checkpoint.getCurrentFileName(),
        checkpoint.getCurrentSequence(),
        checkpoint.getGenerationStates().size()
    );
    processCurrentFile(adapterElementId, collector, checkpoint, files);
    processFollowingFiles(adapterElementId, collector, checkpoint, files);
  }

  private void pollDailyFiles(String adapterId, IEventCollector collector, FileWatcherCheckpoint checkpoint)
      throws IOException {
    // Older checkpoints may still contain entries for several daily files.
    if (checkpoint.getGenerationStates().keySet().removeIf(name ->
        !filePattern.matcher(name).matches() || precedesCheckpoint(name, checkpoint))) {
      checkpointStore.save(adapterId, checkpoint);
    }
    for (FileSlot slot : discoverFiles(checkpoint)) {
      long start = determineStartRecord(slot, checkpoint);
      String previous = checkpoint.getCurrentFileName();
      if (!slot.fileName().equals(previous)) {
        checkpoint.getGenerationStates().remove(previous);
      }
      if (!readFile(adapterId, collector, checkpoint, slot, start).reachedEndOfFile()) {
        break;
      }
    }
  }

  private boolean precedesCheckpoint(String name, FileWatcherCheckpoint checkpoint) {
    if (checkpoint.getCurrentFileName() == null) {
      return false;
    }
    long sequence = fileSequence.applyAsLong(name);
    return sequence < checkpoint.getCurrentSequence()
        || (sequence == checkpoint.getCurrentSequence() && name.compareTo(checkpoint.getCurrentFileName()) < 0);
  }

  private void processCurrentFile(String adapterElementId,
                                  IEventCollector collector,
                                  FileWatcherCheckpoint checkpoint,
                                  List<FileSlot> files) throws IOException {
    if (checkpoint.getCurrentFileName() == null || generationState(checkpoint, checkpoint.getCurrentFileName()) == null) {
      LOG.debug("No existing checkpoint state found. Starting from the first available matching file.");
      return;
    }

    FileSlot currentSlot = files.stream()
        .filter(file -> file.fileName().equals(checkpoint.getCurrentFileName()))
        .findFirst()
        .orElse(null);

    if (currentSlot == null) {
      LOG.debug("Checkpoint file '{}' is no longer present in '{}'. Continuing with following files.",
          checkpoint.getCurrentFileName(), directory);
      return;
    }

    long startRecord = determineStartRecord(currentSlot, checkpoint);
    LOG.debug("Resuming file '{}' at record {}.", currentSlot.fileName(), startRecord);

    readFile(adapterElementId, collector, checkpoint, currentSlot, startRecord);
  }

  private long determineStartRecord(FileSlot currentSlot, FileWatcherCheckpoint checkpoint) {
    var generationState = generationState(checkpoint, currentSlot.fileName());
    if (generationState == null) {
      return 0;
    }

    var previousFingerprint = generationState.getFingerprint();
    var previousLastProcessedRecord = generationState.getLastProcessedRecord();

    if (fingerprintsEqual(currentSlot.fingerprint(), previousFingerprint)) {
      LOG.debug("File '{}' fingerprint unchanged. Continuing after record {}.",
          currentSlot.fileName(), previousLastProcessedRecord);
      return previousLastProcessedRecord + 1;
    }

    if (isAppendedInPlace(currentSlot, previousFingerprint)) {
      LOG.debug("File '{}' grew in place. Continuing after record {}.",
          currentSlot.fileName(), previousLastProcessedRecord);
      return previousLastProcessedRecord + 1;
    }

    LOG.debug("File '{}' fingerprint changed. Restarting from record 0.", currentSlot.fileName());
    return 0;
  }

  private boolean isAppendedInPlace(FileSlot currentSlot, FileFingerprint previousFingerprint) {
    FileFingerprint currentFingerprint = currentSlot.fingerprint();
    if (previousFingerprint == null || currentFingerprint.getSize() <= previousFingerprint.getSize()) {
      return false;
    }
    if (considerLastModified
        && currentFingerprint.getLastModified() < previousFingerprint.getLastModified()) {
      return false;
    }

    try {
      return prefixSha256(currentSlot.path(), previousFingerprint.getSize()).equals(previousFingerprint.getContentHash());
    } catch (IOException e) {
      throw new RuntimeException("Could not verify appended content for " + currentSlot.fileName(), e);
    }
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

    return !considerLastModified || left.getLastModified() == right.getLastModified();
  }

  private void processFollowingFiles(String adapterElementId,
                                     IEventCollector collector,
                                     FileWatcherCheckpoint checkpoint,
                                     List<FileSlot> files) throws IOException {
    int startIndex = startIndex(files, checkpoint);
    LOG.debug("Processing following files starting at index {}.", startIndex);
    for (int offset = 0; offset < files.size(); offset++) {
      FileSlot candidate = files.get((startIndex + offset) % files.size());
      if (candidate.fileName().equals(checkpoint.getCurrentFileName())) {
        continue;
      }

      FileGenerationState generationState = generationState(checkpoint, candidate.fileName());
      FileFingerprint processedFingerprint = generationState == null ? null : generationState.getFingerprint();
      if (processedFingerprint != null && fingerprintsEqual(processedFingerprint, candidate.fingerprint())) {
        LOG.debug("Skipping file '{}' because this generation was already processed.", candidate.fileName());
        continue;
      }

      long startRecord = 0;
      if (generationState != null && isAppendedInPlace(candidate, generationState.getFingerprint())) {
        startRecord = generationState.getLastProcessedRecord() + 1;
        LOG.debug("Reading appended file generation '{}' from record {}.", candidate.fileName(), startRecord);
      } else {
        LOG.debug("Reading new file generation '{}' from record 0.", candidate.fileName());
      }

      readFile(adapterElementId, collector, checkpoint, candidate, startRecord);
    }
  }

  private FileWatchReadResult readFile(String adapterElementId,
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
        interEventDelayMs
    );
    final boolean[] firstEvent = {true};
    var result = reader.readFrom(fileSlot.path(), startRecord, (recordIndex, event) -> {
      delayBeforeNextEvent(firstEvent[0], fileSlot.fileName(), recordIndex);
      firstEvent[0] = false;
      if (LOG.isTraceEnabled()) {
        LOG.trace("Collected record {} from file '{}'.", recordIndex, fileSlot.fileName());
      }
      collector.collect(eventMapper.map(event));
      checkpoint.setCurrentFileName(fileSlot.fileName());
      checkpoint.setCurrentSequence(fileSlot.sequence());
      checkpoint.getGenerationStates().put(fileSlot.fileName(), new FileGenerationState(fileSlot.fingerprint(), recordIndex));

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
    checkpoint.getGenerationStates().put(
        fileSlot.fileName(),
        new FileGenerationState(fileSlot.fingerprint(), result.lastProcessedRecord())
    );
    checkpointStore.save(adapterElementId, checkpoint);
    LOG.debug(
        "Finished file '{}'. Emitted {} events, lastProcessedRecord={}.",
        fileSlot.fileName(),
        result.emittedEvents(),
        result.lastProcessedRecord()
    );
    return result;
  }

  private void delayBeforeNextEvent(boolean firstEvent,
                                    String fileName,
                                    long recordIndex) {
    if (firstEvent || interEventDelayMs <= 0) {
      return;
    }

    try {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Sleeping {} ms before record {} from file '{}'.",
            interEventDelayMs, recordIndex, fileName);
      }
      eventDelayExecutor.sleep(interEventDelayMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while delaying replay for file " + fileName, e);
    }
  }

  List<FileSlot> discoverFiles() throws IOException {
    return discoverFiles(null);
  }

  private List<FileSlot> discoverFiles(FileWatcherCheckpoint lowerBound) throws IOException {
    List<FileSlot> files = new ArrayList<>();
    try (var stream = Files.list(directory)) {
      stream.filter(Files::isRegularFile)
          .filter(path -> filePattern.matcher(path.getFileName().toString()).matches())
          .filter(path -> lowerBound == null || !precedesCheckpoint(path.getFileName().toString(), lowerBound))
          .forEach(path -> files.add(toFileSlot(path)));
    }

    files.sort(Comparator.comparingLong(FileSlot::sequence).thenComparing(FileSlot::fileName));
    if (LOG.isDebugEnabled()) {
      LOG.debug("Discovered {} matching archive files: {}",
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

  private FileGenerationState generationState(FileWatcherCheckpoint checkpoint, String fileName) {
    return checkpoint.getGenerationStates().get(fileName);
  }

  private FileSlot toFileSlot(Path path) {
    String fileName = path.getFileName().toString();
    return new FileSlot(fileName, path, fileSequence.applyAsLong(fileName), fingerprint(path));
  }

  private static long sequence(Pattern filePattern, String fileName) {
    Matcher matcher = filePattern.matcher(fileName);
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

  private String prefixSha256(Path path, long maxBytes) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      try (InputStream in = Files.newInputStream(path)) {
        byte[] buffer = new byte[8192];
        long remaining = maxBytes;
        while (remaining > 0) {
          int read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
          if (read == -1) {
            break;
          }
          digest.update(buffer, 0, read);
          remaining -= read;
        }
      }

      return HexFormat.of().formatHex(digest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }
}
