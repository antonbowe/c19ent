package de.antonbowe.c19ent.project;

import de.antonbowe.c19ent.client.ClientModel;
import de.antonbowe.c19ent.shared.BusinessKeyModel;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class ProjectModel extends BusinessKeyModel<ProjectModel> {

    private String name;

    @DBRef
    private ClientModel client;

    @Override
    public void setBusinessKey(String businessKey) {

    }
}
