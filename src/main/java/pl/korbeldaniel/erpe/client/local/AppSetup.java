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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialButton;
import pl.korbeldaniel.erpe.client.local.JQueryProducer.JQuery;
import pl.korbeldaniel.ui.client.ErpButton;
import pl.korbeldaniel.ui.client.IsButton;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.nav.client.local.NavigationPanel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static elemental2.dom.DomGlobal.document;

/**
 * <p>
 * This bean attaches the {@link NavBar} and {@link NavigationPanel} when the
 * application starts.
 *
 * <p>
 * The {@link EntryPoint} scope is like {@link ApplicationScoped} except that
 * entry points are eagerly initilialized when the IoC container starts.
 * Consequently, the {@link PostConstruct} of this bean will be invoked when the
 * container is initialized.
 */
@EntryPoint
public class AppSetup {

	@Inject
	private NavigationPanel navPanel;

	@Inject
	private NavBar navbar;

	@Inject
	private JQuery $;

	@PostConstruct
	public void init() {
		$.wrap($.wrap(document.body).children().first()).before(navbar.getElement());
		IsButton button2 = new ErpButton("Testt 2");
		RootPanel.get().add(button2);
	}

}