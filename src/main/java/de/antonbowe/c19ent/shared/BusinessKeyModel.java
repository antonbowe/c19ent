package de.antonbowe.c19ent.shared;

import lombok.Getter;
import org.springframework.data.annotation.Id;

public abstract class BusinessKeyModel<E extends BusinessKeyModel<E>> {

  @Id @Getter protected String id;

  public String generateBusinessKey(BusinessKeyModelRepository<E> businessKeyModelRepository) {
    String businessKey = String.valueOf((int) (Math.random() * 10000000000L));

    if (businessKeyModelRepository != null
        && businessKeyModelRepository.existsByBusinessKey(businessKey)) {
      return this.generateBusinessKey(businessKeyModelRepository);
    }

    return businessKey;
  }

  public abstract void setBusinessKey(String businessKey);

}
