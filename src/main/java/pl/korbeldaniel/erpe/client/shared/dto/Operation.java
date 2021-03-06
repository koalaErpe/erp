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

package pl.korbeldaniel.erpe.client.shared.dto;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import pl.korbeldaniel.erpe.client.shared.entity.Contact;

/**
 * Qualifier for {@link ContactOperation} to differentiate between creation, update, and deletion of {@link Contact
 * Contacts}.
 */
@Retention(RUNTIME)
@Qualifier
public @interface Operation {

  public enum OperationType {
    CREATE, UPDATE, DELETE
  }

  OperationType value();

}
