/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.korbeldaniel.erpe.server.dao;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import pl.korbeldaniel.erpe.client.shared.entity.Contact;

/**
 * A service that provides transaction boundaries around CRUD operations on {@link Contact Contacts}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ContactDao extends GenericDao<Contact>{

  public Optional<Contact> findById(Long id) {
	  Contact result = em.createNamedQuery("SELECT c FROM Contact c where c.id = :id", Contact.class)
			  .setParameter("id", id)
			  .getSingleResult();
	  return Optional.of(result);
  }
  public List<Contact> findAll() {
	  return em.createNamedQuery(Contact.ALL_CONTACTS_QUERY, Contact.class).getResultList();
  }
  public List<Contact> findByName() {

    return em.createNamedQuery(Contact.ALL_CONTACTS_QUERY, Contact.class).getResultList();
  }

  public void create(final Contact newContact) {
    em.persist(newContact);
  }

  public void update(final Contact contact) {
    em.merge(contact);
  }

  public void deleteById(final Long id) {
    final Contact contact = em.find(Contact.class, id);
    if (contact != null) {
      em.remove(contact);
    } else {
      throw new IllegalArgumentException(
              "The given id, " + id + ", was not a key for any " + Contact.class.getSimpleName());
    }
  }

}
