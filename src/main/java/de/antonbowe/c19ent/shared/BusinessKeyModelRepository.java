package de.antonbowe.c19ent.shared;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessKeyModelRepository<E extends BusinessKeyModel<E>> extends MongoRepository<E, String> {

    boolean existsByBusinessKey(String businessKey);

}
