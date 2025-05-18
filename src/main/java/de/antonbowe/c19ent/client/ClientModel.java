package de.antonbowe.c19ent.client;

import de.antonbowe.c19ent.shared.BusinessKeyModel;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("clients")
public class ClientModel extends BusinessKeyModel<ClientModel> {

    private String name;

    @Override
    public void setBusinessKey(String businessKey) {

    }
}
