package org.apache.streampipes.service.core.migrations.v099;

import java.io.IOException;
import java.util.List;

import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class AddDatasetPrivilegeMigration implements Migration {

      public CRUDStorage<Privilege> privilegeStorage;

  private static final List<String> privilegesToAdd = List.of(
    "PRIVILEGE_READ_DATASET ",
    "PRIVILEGE_WRITE_DATASET "
  );

    public AddDatasetPrivilegeMigration() {
    this.privilegeStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPrivilegeStorage();
  }


    @Override
    public boolean shouldExecute() {
         return privilegeStorage.findAll().stream()
            .noneMatch(p -> privilegesToAdd.contains(p.getElementId()));
    }‚

    @Override
    public void executeMigration() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeMigration'");
    }

    @Override
    public String getDescription() {
        return "Add new Dataset Privileges.";
    }

}
