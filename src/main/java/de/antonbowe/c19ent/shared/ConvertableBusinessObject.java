package de.antonbowe.c19ent.shared;

public interface ConvertableBusinessObject<M> {

  M convertToDatabaseType();
}
