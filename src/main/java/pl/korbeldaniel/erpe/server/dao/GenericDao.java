package pl.korbeldaniel.erpe.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GenericDao<E> {
	@PersistenceContext(unitName = "persistenceDefault")
	protected EntityManager em;

}
