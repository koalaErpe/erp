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

import elemental2.dom.HTMLAnchorElement;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.ui.HasValue;
import org.jboss.errai.ui.shared.api.annotations.Bound;

/**
 * <p>
 * An {@link HTMLAnchorElement} class with special Errai Data Binding semantics. Binding to this element sets the element's
 * inner text and it's href. The bound value is presumed to be an email.
 *
 * <p>
 * Because this type implements {@link HasValue}, the {@link #getValue()} and {@link #setValue(String)} methods will be
 * used for accessing or mutating the element's value if this element is used with {@link Bound declarative data binding}.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLAnchorElement")
public class BindableEmailAnchor extends HTMLAnchorElement implements HasValue<String> {

  @JsOverlay
  @Override
  public final String getValue() {
    return textContent;
  }

  @JsOverlay
  @Override
  public final void setValue(final String value) {
    textContent = value;
    href = "mailto:" + value;
  }
}