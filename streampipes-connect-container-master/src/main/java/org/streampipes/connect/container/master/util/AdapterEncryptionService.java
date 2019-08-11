/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.container.master.util;

import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.staticproperty.SecretStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.user.management.encryption.CredentialsManager;

import java.security.GeneralSecurityException;
import java.util.List;

public class AdapterEncryptionService {

  private AdapterDescription ad;

  public AdapterEncryptionService(AdapterDescription ad) {
    this.ad = ad;
  }

  public AdapterDescription encrypt() {
    if (ad.getConfig() != null) {
      encrypt(ad.getConfig());
    }

    if (ad instanceof GenericAdapterDescription) {
      encrypt(((GenericAdapterDescription) ad).getProtocolDescription().getConfig());
    }

    return ad;
  }

  public AdapterDescription decrypt() {
    if (ad.getConfig() != null) {
      decrypt(ad.getConfig());
    }
    if (ad instanceof GenericAdapterDescription) {
      decrypt(((GenericAdapterDescription) ad).getProtocolDescription().getConfig());
    }

    return ad;
  }

  private void encrypt(List<StaticProperty> staticProperties) {
    staticProperties
            .stream()
            .filter(SecretStaticProperty.class::isInstance)
            .forEach(secret -> {
              if (!((SecretStaticProperty) secret).getEncrypted()) {
                try {
                  String encrypted = CredentialsManager.encrypt(ad.getUserName(),
                          ((SecretStaticProperty) secret).getValue());
                  ((SecretStaticProperty) secret).setValue(encrypted);
                  ((SecretStaticProperty) secret).setEncrypted(true);
                } catch (GeneralSecurityException e) {
                  e.printStackTrace();
                }
              }
            });
  }

  private void decrypt(List<StaticProperty> staticProperties) {
    staticProperties.stream()
            .filter(SecretStaticProperty.class::isInstance)
            .forEach(sp -> {
              try {
                String decrypted = CredentialsManager.decrypt(ad.getUserName(),
                        ((SecretStaticProperty) sp).getValue());
                ((SecretStaticProperty) sp).setValue(decrypted);
                ((SecretStaticProperty) sp).setEncrypted(false);
              } catch (GeneralSecurityException e) {
                e.printStackTrace();
              }
            });
  }
}
