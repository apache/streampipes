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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.audit.events.AuditChange;
import org.apache.streampipes.audit.events.extraction.StaticPropertyAuditExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class AdapterEditChangesTest {
  private final AdapterEditChanges detector = new AdapterEditChanges();

  private AdapterDescription adapter(String name, String value) {
    var adapter = new AdapterDescription();
    adapter.setName(name);
    adapter.setDescription("description");
    adapter.setConfig(List.of(FreeTextStaticProperty.of("interval", value)));
    return adapter;
  }

  @Test
  void capturesOnlyNameDescriptionAndConfig() {
    var before = adapter("old", "1000");
    var after = adapter("new", "5000");
    after.setDescription("new description");
    after.setRunning(true);
    after.setSelectedEndpointUrl("changed");
    assertEquals(List.of(new AuditChange("name", "old", "new"),
        new AuditChange("description", "description", "new description"),
        new AuditChange("interval", "1000", "5000")), detector.detect(before, after).changes());
  }

  @Test
  void lifecycleAndRevisionChangesAreNotEdits() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    after.setRunning(true);
    after.setRev("new-revision");
    after.setSelectedServiceId("worker");
    assertTrue(detector.detect(before, after).changes().isEmpty());
  }

  @Test
  void secretsAreComparedAsPlaintextButNeverRecorded() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var stored = new SecretStaticProperty("password", "Password", "");
    stored.setValue("ciphertext");
    stored.setEncrypted(true);
    var edited = new SecretStaticProperty("password", "Password", "");
    edited.setValue("old-secret");
    edited.setEncrypted(false);
    before.setConfig(List.of(stored));
    after.setConfig(List.of(edited));
    try (var encryption = mockStatic(SecretEncryptionManager.class)) {
      encryption.when(() -> SecretEncryptionManager.decrypt("ciphertext")).thenReturn("old-secret");
      assertTrue(detector.detect(before, after).changes().isEmpty());
      edited.setValue("new-secret");
      assertEquals(List.of(new AuditChange("password", "[REDACTED]", "[REDACTED]")),
          detector.detect(before, after).changes());
      assertEquals("ciphertext", stored.getValue());
      assertEquals("new-secret", edited.getValue());
    }
  }

  @Test
  void addedAndRemovedSecretsAreRedacted() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var secret = new SecretStaticProperty("password", "Password", "");
    secret.setValue("never-record");
    before.setConfig(List.of());
    after.setConfig(List.of(secret));
    assertEquals(List.of(new AuditChange("password", "[REDACTED]", "[REDACTED]")),
        detector.detect(before, after).changes());
    assertEquals(detector.detect(before, after), detector.detect(after, before));
  }
  @Test
  void nestedSecretInAddedGroupIsRedacted() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    before.setConfig(List.of());
    var secret = new SecretStaticProperty("password", "Password", "");
    secret.setValue("nested-secret");
    var group = new StaticPropertyGroup("auth", "Auth", "");
    group.setStaticProperties(List.of(secret));
    after.setConfig(List.of(group));
    var changes = detector.detect(before, after).changes();
    assertTrue(changes.contains(new AuditChange("auth.password", "[REDACTED]", "[REDACTED]")));
    assertTrue(changes.stream().noneMatch(change -> "nested-secret".equals(change.after())));
  }
  @Test
  void nestedConfigUsesChildInternalName() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldGroup = new StaticPropertyGroup("connection", "Connection", "");
    oldGroup.setStaticProperties(List.of(FreeTextStaticProperty.of("pollingIntervalMs", "1000")));
    var newGroup = new StaticPropertyGroup("connection", "Connection", "");
    newGroup.setStaticProperties(List.of(FreeTextStaticProperty.of("pollingIntervalMs", "5000")));
    before.setConfig(List.of(oldGroup));
    after.setConfig(List.of(newGroup));
    assertEquals(List.of(new AuditChange("connection.pollingIntervalMs", "1000", "5000")),
        detector.detect(before, after).changes());
  }
  @Test
  void oneOfReportsOneChangeWithSelectedOptions() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldSelection = new OneOfStaticProperty();
    oldSelection.setInternalName("protocol");
    oldSelection.setOptions(List.of(option("MQTT", "mqtt", true), option("OPC UA", "opcua", false)));
    var newSelection = new OneOfStaticProperty();
    newSelection.setInternalName("protocol");
    newSelection.setOptions(List.of(option("MQTT", "mqtt", false), option("OPC UA", "opcua", true)));
    before.setConfig(List.of(oldSelection));
    after.setConfig(List.of(newSelection));
    assertEquals(List.of(new AuditChange("protocol", "mqtt", "opcua")), detector.detect(before, after).changes());
  }

  @Test
  void reorderedOptionsAndPresentationChangesAreIgnored() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldSelection = new OneOfStaticProperty();
    oldSelection.setInternalName("protocol");
    oldSelection.setOptions(List.of(option("MQTT", "mqtt", true), option("OPC UA", "opcua", false)));
    var newSelection = new OneOfStaticProperty();
    newSelection.setInternalName("protocol");
    newSelection.setLabel("New display label");
    newSelection.setOptions(List.of(option("OPC", "opcua", false), option("Renamed MQTT", "mqtt", true)));
    before.setConfig(List.of(oldSelection));
    after.setConfig(List.of(newSelection));
    assertTrue(detector.detect(before, after).changes().isEmpty());
  }

  @Test
  void multipleSelectionUsesSelectedNamesWithLabelFallback() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldSelection = new AnyStaticProperty();
    oldSelection.setInternalName("fields");
    oldSelection.setOptions(List.of(option("Temperature", null, true), option("Pressure", null, false)));
    var newSelection = new AnyStaticProperty();
    newSelection.setInternalName("fields");
    newSelection.setOptions(List.of(option("Temperature", null, true), option("Pressure", null, true)));
    before.setConfig(List.of(oldSelection));
    after.setConfig(List.of(newSelection));
    assertEquals(List.of(new AuditChange("fields", List.of("Temperature"), List.of("Pressure", "Temperature"))),
        detector.detect(before, after).changes());
  }

  @Test
  void collectionMembersKeepTheirPosition() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldCollection = new CollectionStaticProperty();
    oldCollection.setInternalName("topics");
    oldCollection.setMembers(List.of(FreeTextStaticProperty.of("topic", "a"), FreeTextStaticProperty.of("topic", "b")));
    var newCollection = new CollectionStaticProperty();
    newCollection.setInternalName("topics");
    newCollection.setMembers(List.of(FreeTextStaticProperty.of("topic", "a"), FreeTextStaticProperty.of("topic", "c")));
    before.setConfig(List.of(oldCollection));
    after.setConfig(List.of(newCollection));
    assertEquals(List.of(new AuditChange("topics[1]", "b", "c")), detector.detect(before, after).changes());
  }

  @Test
  void alternativesExtractOnlyTheActiveBranchAndRedactItsSecrets() {
    var before = adapter("same", "1000");
    var after = adapter("same", "1000");
    var oldAuth = new StaticPropertyAlternatives();
    oldAuth.setInternalName("auth");
    var oldAnonymous = new StaticPropertyAlternative();
    oldAnonymous.setInternalName("anonymous");
    oldAnonymous.setSelected(true);
    oldAuth.setAlternatives(List.of(oldAnonymous));
    var newAuth = new StaticPropertyAlternatives();
    newAuth.setInternalName("auth");
    var passwordAuth = new StaticPropertyAlternative();
    passwordAuth.setInternalName("password");
    passwordAuth.setSelected(true);
    var secret = new SecretStaticProperty("credential", "Credential", "");
    secret.setValue("never-serialize");
    passwordAuth.setStaticProperty(secret);
    var inactive = new StaticPropertyAlternative();
    inactive.setInternalName("unused");
    inactive.setSelected(false);
    inactive.setStaticProperty(FreeTextStaticProperty.of("unused", "ignored"));
    newAuth.setAlternatives(List.of(passwordAuth, inactive));
    before.setConfig(List.of(oldAuth));
    after.setConfig(List.of(newAuth));
    assertEquals(List.of(new AuditChange("auth", List.of("anonymous"), List.of("password")),
        new AuditChange("auth.password.credential", "[REDACTED]", "[REDACTED]")),
        detector.detect(before, after).changes());
  }

  @Test
  void toggleMappingAndTreeExposeEffectiveValues() {
    var toggle = new SlideToggleStaticProperty();
    toggle.setInternalName("enabled");
    toggle.setSelected(true);
    var mapping = new MappingPropertyUnary();
    mapping.setInternalName("timestamp");
    mapping.setSelectedProperty("event.time");
    var tree = new RuntimeResolvableTreeInputStaticProperty();
    tree.setInternalName("nodes");
    tree.setSelectedNodesInternalNames(List.of("machine.temperature"));
    var extracted = new StaticPropertyAuditExtractor(SecretEncryptionManager::decrypt).extract(List.of(toggle, mapping, tree));
    assertEquals(true, extracted.get("enabled").value());
    assertEquals("event.time", extracted.get("timestamp").value());
    assertEquals(List.of("machine.temperature"), extracted.get("nodes").value());
  }

  private Option option(String label, String internalName, boolean selected) {
    var option = new Option(label, internalName);
    option.setSelected(selected);
    return option;
  }
}
