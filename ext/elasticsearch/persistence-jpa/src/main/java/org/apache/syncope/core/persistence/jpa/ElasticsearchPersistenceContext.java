/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.jpa;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.apache.syncope.core.persistence.api.attrvalue.validation.PlainAttrValidationManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.AnySearchDAO;
import org.apache.syncope.core.persistence.api.dao.AuditConfDAO;
import org.apache.syncope.core.persistence.api.dao.DynRealmDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.PlainSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.RealmDAO;
import org.apache.syncope.core.persistence.api.dao.RoleDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.AnyUtilsFactory;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.jpa.dao.ElasticsearchAnySearchDAO;
import org.apache.syncope.core.persistence.jpa.dao.ElasticsearchAuditConfDAO;
import org.apache.syncope.core.persistence.jpa.dao.ElasticsearchRealmDAO;
import org.apache.syncope.ext.elasticsearch.client.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(proxyBeanMethods = false)
public class ElasticsearchPersistenceContext {

    @ConditionalOnMissingBean(name = "elasticsearchAnySearchDAO")
    @Bean
    public AnySearchDAO anySearchDAO(
            final ElasticsearchProperties props,
            final RealmDAO realmDAO,
            final @Lazy DynRealmDAO dynRealmDAO,
            final @Lazy UserDAO userDAO,
            final @Lazy GroupDAO groupDAO,
            final @Lazy AnyObjectDAO anyObjectDAO,
            final PlainSchemaDAO schemaDAO,
            final EntityFactory entityFactory,
            final AnyUtilsFactory anyUtilsFactory,
            final PlainAttrValidationManager validator,
            final ElasticsearchClient client) {

        return new ElasticsearchAnySearchDAO(
                realmDAO,
                dynRealmDAO,
                userDAO,
                groupDAO,
                anyObjectDAO,
                schemaDAO,
                entityFactory,
                anyUtilsFactory,
                validator,
                client,
                props.getIndexMaxResultWindow());
    }

    @ConditionalOnMissingBean(name = "elasticsearchRealmDAO")
    @Bean
    public RealmDAO realmDAO(
            final @Lazy RoleDAO roleDAO,
            final ApplicationEventPublisher publisher,
            final ElasticsearchProperties props,
            final ElasticsearchClient client) {

        return new ElasticsearchRealmDAO(roleDAO, publisher, client, props.getIndexMaxResultWindow());
    }

    @ConditionalOnMissingBean(name = "elasticsearchAuditConfDAO")
    @Bean
    public AuditConfDAO auditConfDAO(
            final ElasticsearchProperties props,
            final ElasticsearchClient client) {

        return new ElasticsearchAuditConfDAO(client, props.getIndexMaxResultWindow());
    }
}
