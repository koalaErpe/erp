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

package pl.korbeldaniel.erpe.client.local;

import static pl.korbeldaniel.erpe.client.local.Click.Type.DOUBLE;
import static pl.korbeldaniel.erpe.client.local.Click.Type.SINGLE;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import pl.korbeldaniel.erpe.client.shared.entity.Contact;

/**
 * <p>
 * An Errai UI component for displaying a single {@link Contact} as a row in an HTML table. Can be used to display
 * {@link Contact Contacts} in a {@link ListComponent}. This component can be bound to a {@link Contact} by calling
 * {@link #setValue(Contact)}.
 *
 * <p>
 * The HTML markup for this {@link Templated} component is the HTML element with the CSS class {@code contact} in the
 * file {@code contact-page.html} in this package. This component uses CSS from the file {@code contact-page.css} in
 * this package.
 *
 * <p>
 * The {@link DataField} annotation marks fields that replace HTML elements from the template file. As an example, the
 * field {@link ContactDisplay#contact} is the root {@code
 *
<tr>
 * } element of this component; it can be used to attach this component to the DOM.
 *
 * <p>
 * The {@link Bound} annotations mark UI fields as managed by Errai Data Binding, which keeps UI values synchronized
 * with properties in the bound {@link Contact} model instance. (See the base class, {@link BaseContactView}.)
 *
 * <p>
 * Errai UI automatically registers methods annotated with {@link EventHandler} to listen for DOM events.
 *
 * <p>
 * Instances of this type should be obtained via Errai IoC, either by using {@link Inject} in another container managed
 * bean, or by programmatic lookup through the bean manager.
 */
@Templated(value = "contact-page.html#contact", stylesheet = "contact-page.css")
public class ContactDisplay extends BaseContactView implements IsElement {

  /**
   * This element is the root element of this component (as declared in the {@code #contact} fragment of the
   * {@link Templated#value()} above).
   */
  @Inject
  @DataField
  private HTMLDivElement contact;

  @Inject
  @Bound @DataField
  private HTMLDivElement fullname;

  @Inject @Named("h4")
  @Bound @DataField
  private HTMLHeadingElement nickname;

  @Inject
  @Bound @DataField
  private BindableTelAnchor phonenumber;

  @Inject
  @Bound @DataField
  private BindableEmailAnchor email;

  /*
   * We specify a converter because Errai does not provide built-in conversion from String to Date.
   */
  @Inject
  @Bound(converter = DateConverter.class) @DataField
  private Div birthday;

  @Inject
  @Bound @DataField
  private Div notes;

  @Inject
  @Click(SINGLE)
  private Event<ContactDisplay> click;

  @Inject
  @Click(DOUBLE)
  private Event<ContactDisplay> dblClick;

  /**
   * Called for single-click events on the {@link DataField @DataField} {@link #contact}. The CDI event fired here is
   * observed by {@link ContactList#selectComponent(ContactDisplay)}.
   */
  @EventHandler("contact")
  public void onClick(final @ForEvent("click") MouseEvent event) {
    click.fire(this);
  }

  /**
   * Called for double-click events on the {@link DataField @DataField} {@link #contact}. The CDI event fired here is
   * observed by {@link ContactListPage#editComponent(ContactDisplay)}.
   */
  @EventHandler("contact")
  public void onDoubleClick(final @ForEvent("dblclick") MouseEvent event) {
    dblClick.fire(this);
  }

  /**
   * Marks this as selected (or not) so that it may be styled differently in the UI.
   *
   * @param selected
   *          If {@code true}, add the CSS class "selected" to the {@code <tr>} tag in this component. If {@code false},
   *          remove the CSS class "selected" from the {@code <tr>} tag in this component.
   */
  public void setSelected(final boolean selected) {
    if (selected) {
      contact.classList.remove("selected");
    } else {
      contact.classList.add("selected");
    }
  }

}
