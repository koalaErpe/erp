/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.korbeldaniel.erpe.client.local;

import com.google.gwt.http.client.Response;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.MouseEvent;
import jsinterop.base.Js;
import pl.korbeldaniel.erpe.client.shared.api.rest.ContactStorageService;
import pl.korbeldaniel.erpe.client.shared.dto.ContactOperation;
import pl.korbeldaniel.erpe.client.shared.dto.Operation;
import pl.korbeldaniel.erpe.client.shared.entity.Contact;

import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.NavigationPanel;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageHiding;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static pl.korbeldaniel.erpe.client.local.Click.Type.DOUBLE;
import static pl.korbeldaniel.erpe.client.local.Click.Type.SINGLE;
import static pl.korbeldaniel.erpe.client.shared.dto.Operation.OperationType.CREATE;
import static pl.korbeldaniel.erpe.client.shared.dto.Operation.OperationType.DELETE;
import static pl.korbeldaniel.erpe.client.shared.dto.Operation.OperationType.UPDATE;

/**
 * <p>
 * An Errai UI component for creating, displaying, updating, and deleting {@link Contact Contacts}. This component is
 * also an Errai Navigation {@link Page}; it will be displayed on the GWT host page whenever the navigation URL fragment
 * is {@code #/contacts}.
 *
 * <p>
 * The HTML markup for this {@link Templated} component is the HTML element with the CSS class {@code contact-list} in
 * the file {@code contact-page.html} in this package. This component uses CSS from the file {@code contact-page.css} in
 * this package.
 *
 * <p>
 * The {@link DataField} annotation marks fields that replace HTML elements from the template file. As an example, the
 * field {@link ContactDisplay#editor} replaces the {@code <div>} element in the template with the CSS class
 * {@code modal-fields}. Because {@link ContactEditor} is an Errai UI component, the markup for {@link ContactEditor}
 * will replace the contents of the {@code modal-fields} div in this component.
 *
 * <p>
 * This component uses a {@link ListComponent} to display a list of {@link Contact Contacts}. The {@code List<Contact>}
 * returned by calling {@link DataBinder#getModel()} on {@link #binder} is a model bound to a table of
 * {@link ContactDisplay ContactDisplays} in an HTML table. Any changes to the model list (such as adding or removing
 * items) will be automatically reflected in the displayed table.
 *
 * <p>
 * Instances of this type should be obtained via Errai IoC, either by using {@link Inject} in another container managed
 * bean, or by programmatic lookup through the bean manager.
 */
@Page(role = DefaultPage.class, path = "/contacts")
@Templated(value = "contact-page.html#contact-list", stylesheet = "contact-page.css")
public class ContactListPage {

  @Inject
  @AutoBound
  private DataBinder<List<Contact>> binder;

  @Inject
  @Bound
  @DataField
  private ListComponent<Contact, ContactDisplay> list;

  @Inject
  @DataField
  private HTMLFormElement modal;

  @Inject
  @DataField("modal-fields")
  private ContactEditor editor;

  @Inject
  @DataField("modal-delete")
  private HTMLButtonElement delete;

  @Inject
  private NavBar navbar;

  @Inject
  private HTMLAnchorElement newContactAnchor;

  @Inject
  private HTMLAnchorElement sortContactsAnchor;

  /**
   * This is a simple interface for calling a remote HTTP service. Behind this interface, Errai has generated an HTTP
   * request to the service defined by {@link ContactStorageService} (a JaxRS service).
   */
  @Inject
  private Caller<ContactStorageService> contactService;

  @Inject
  private ClientMessageBus bus;

  @Inject
  private Logger logger;

  /**
   * Register handlers and populate the list of {@link Contact Contacts}.
   */
  @PostConstruct
  private void setup() {
    /*
     * Triggers an HTTP request to the ContactStorageService. The call back will be invoked asynchronously to display
     * all retrieved contacts.
     */
    contactService.call((final List<Contact> contacts) -> binder.getModel().addAll(contacts)).getAllContacts();

    // Remove placeholder table row from template.
    DOMUtil.removeAllElementChildren(list.getElement());

    /*
     * Configure actions for when a ContactDisplay in the list is selected or deselected.
     */
    list.setSelector(display -> display.setSelected(true));
    list.setDeselector(display -> display.setSelected(false));

    /*
     * Setup anchors that are added to the nav bar when the page is shown.
     */
    newContactAnchor.href = ("javascript:void(0);");
    newContactAnchor.textContent = ("Create Contact");
    newContactAnchor.onclick = e -> {
      displayFormWithNewContact();
      return null;
    };

    sortContactsAnchor.href = ("javascript:");
    sortContactsAnchor.textContent = ("Sort By Nickname");
    sortContactsAnchor.onclick = (e -> {
      sortContactsByName();
      return null;
    });
  }

  /**
   * This method is invoked when this {@link Page} is attached to the {@link NavigationPanel}.
   */
  @PageShown
  public void addNavBarButtons() {
    navbar.add(newContactAnchor);
    navbar.add(sortContactsAnchor);
  }

  /**
   * This method is invoked when this {@link Page} is being removed from the {@link NavigationPanel}.
   */
  @PageHiding
  public void removeNavBarButtons() {
    navbar.remove(newContactAnchor);
    navbar.remove(sortContactsAnchor);
  }

  /**
   * This is an Errai UI event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the id {@code new-content}.
   * <p>
   * The parameter is a JS interop wrapper for a native DOM {@link MouseEvent}. The {@code MouseEvent} interface like many
   * DOM event interfaces is used for multiple browser event types (i.e. "click", "dblclick", "mouseover", etc.). In order
   * to only listen for "click" events we use the {@link ForEvent} annotation.
   * <p>
   * This method displays the hidden modal form so that a user can create a new {@link Contact}.
   */
  @EventHandler("new-contact")
  public void onNewContactClick(final @ForEvent("click") MouseEvent event) {
    displayFormWithNewContact();
  }

  /**
   * This is an Errai UI event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-submit} CSS class.
   * <p>
   * The parameter is a JS interop wrapper for a native DOM {@link MouseEvent}. The {@code MouseEvent} interface like many
   * DOM event interfaces is used for multiple browser event types (i.e. "click", "dblclick", "mouseover", etc.). In order
   * to only listen for "click" events we use the {@link ForEvent} annotation.
   * <p>
   * This method displays and persists changes made to a {@link Contact} in the {@link ContactEditor}, whether it is a
   * newly created or an previously existing {@link Contact}.
   */
  @EventHandler("modal-submit")
  public void onModalSubmitClick(final @ForEvent("click") MouseEvent event) {
    if (modal.checkValidity()) {

      modal.classList.remove("displayed");

      if (binder.getModel().contains(editor.getValue())) {
        updateContactFromEditor();
      } else {
        createNewContactFromEditor();
      }
    }
  }

  /**
   * This is an Errai UI event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-cancel} CSS class.
   * <p>
   * The parameter is a JS interop wrapper for a native DOM {@link MouseEvent}. The {@code MouseEvent} interface like many
   * DOM event interfaces is used for multiple browser event types (i.e. "click", "dblclick", "mouseover", etc.). In order
   * to only listen for "click" events we use the {@link ForEvent} annotation.
   * <p>
   * This method hides the ContactEditor modal form and resets the bound model.
   */
  @EventHandler("modal-cancel")
  public void onModalCancelClick(final @ForEvent("click") MouseEvent event) {
    modal.classList.remove("displayed");
  }

  /**
   * This is an Errai UI native event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-delete} CSS class.
   * <p>
   * The parameter is a JS interop wrapper for a native DOM {@link MouseEvent}. The {@code MouseEvent} interface like many
   * DOM event interfaces is used for multiple browser event types (i.e. "click", "dblclick", "mouseover", etc.). In order
   * to only listen for "click" events we use the {@link ForEvent} annotation.
   * <p>
   * This method removes a {@link Contact} from the displayed table and makes an HTTP request to delete the contact from
   * persistent storage on the server.
   */
  @EventHandler("modal-delete")
  public void onModalDeleteClick(final @ForEvent("click") MouseEvent event) {
    if (binder.getModel().contains(editor.getValue())) {
      final Contact deleted = editor.getValue();
      contactService.call((final Response response) -> {
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
          binder.getModel().remove(deleted);
        }
      }).delete(editor.getValue().getId());
      editor.setValue(new Contact());

      modal.classList.remove("displayed");
    }
  }

  /**
   * Observes local CDI events fired from
   * {@link ContactDisplay#onDoubleClick(elemental2.dom.MouseEvent)}, in order to display the
   * modal form for editting a contact.
   */
  public void editComponent(final @Observes @Click(DOUBLE) ContactDisplay component) {
    selectComponent(component);
    editModel(component.getValue());
  }

  /**
   * This method observes CDI events fired locally by {@link ContactDisplay#onClick(elemental2.dom.MouseEvent)} in order to highlight a
   * {@link ContactDisplay} when it is clicked.
   */
  public void selectComponent(final @Observes @Click(SINGLE) ContactDisplay component) {
    if (list.getSelectedComponents().contains(component)) {
      list.deselectAll();
    } else {
      list.deselectAll();
      list.selectComponent(component);
    }
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when a
   * new {@link Contact} is created. In this way we can display newly created contacts from other browser sessions.
   */
  public void onRemoteCreated(final @Observes @Operation(CREATE) ContactOperation contactOperation) {
    if (sourceIsNotThisClient(contactOperation)) {
      binder.getModel().add(contactOperation.getContact());
    }
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when an
   * existing {@link Contact} is updated. In this way we can display new property values for contacts when they are
   * updated from other browser sessions.
   */
  public void onRemoteUpdated(final @Observes @Operation(UPDATE) ContactOperation contactOperation) {
    if (sourceIsNotThisClient(contactOperation)) {
      final int indexOf = binder.getModel().indexOf(contactOperation.getContact());
      if (indexOf == -1) {
        logger.warn("Received update before creation for " + contactOperation.getContact() + " from " + contactOperation
                .getSourceQueueSessionId());
        binder.getModel().add(contactOperation.getContact());
      } else {
        binder.getModel().set(indexOf, contactOperation.getContact());
      }
    }
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when an
   * existing {@link Contact} is deleted. In this way we can remove displayed contacts when they are deleted in other
   * browser sessions.
   */
  public void onRemoteDelete(final @Observes @Operation(DELETE) Long id) {
    final Iterator<Contact> contactIter = binder.getModel().iterator();
    while (contactIter.hasNext()) {
      if (id.equals(contactIter.next().getId())) {
        contactIter.remove();
        break;
      }
    }
  }

  private void createNewContactFromEditor() {
    final Contact editorModel = editor.getValue();
    // Adding this model to the list will create and display a new, bound ContactDisplay in the table.
    binder.getModel().add(editorModel);
    contactService.call((final Response response) -> {
      // Set the id if we successfully create this contact.
      if (response.getStatusCode() == Response.SC_CREATED) {
        final String createdUri = response.getHeader("Location");
        final String idString = createdUri.substring(createdUri.lastIndexOf('/') + 1);
        final long id = Long.parseLong(idString);
        editorModel.setId(id);
      }
    }).create(new ContactOperation(editorModel, bus.getSessionId()));
  }

  private void displayFormWithNewContact() {
    Contact contact = new Contact();
    contact.setBirthday(new Date());
    contact.setEmail("email@domein.pl");
    contact.setFullname("full");
    contact.setNickname("nick");
    contact.setNotes("Custom notes");
    contact.setPhonenumber("123123123");
	editor.setValue(contact);
    displayModal(false);
  }

  private void updateContactFromEditor() {
    /*
     * When editting a contact we paused binding so that changes from the UI do not propogate to the model until
     * "submit" is clicked. This call updates the model with all changes made in the UI while binding was paused.
     */
    editor.syncStateFromUI();
    contactService.call().update(new ContactOperation(editor.getValue(), bus.getSessionId()));
  }

  /**
   * For ignoring remote events that originate from this client.
   */
  private boolean sourceIsNotThisClient(final ContactOperation contactOperation) {
    return contactOperation.getSourceQueueSessionId() == null || !contactOperation.getSourceQueueSessionId()
            .equals(bus.getSessionId());
  }

  /**
   * If the parameter is true then this displays a form for editting (with a delete button). Otherwise show a form for
   * new contacts (no delete button).
   */
  private void displayModal(final boolean showDelete) {
    if (showDelete) {
      delete.style.removeProperty("display");
    } else {
      delete.style.setProperty("display", "none");
    }
    modal.classList.add("displayed");
  }

  private void editModel(final Contact model) {
    /*
     * This sets the editor model with data binding paused so that changes to the model are not propogated until the
     * user clicks "submit".
     */
    editor.setValuePaused(model);
    displayModal(true);
  }

  private void sortContactsByName() {
    binder.pause();
    binder.getModel().sort(Comparator.comparing(Contact::getNickname));
    binder.resume(StateSync.FROM_MODEL);
  }
}