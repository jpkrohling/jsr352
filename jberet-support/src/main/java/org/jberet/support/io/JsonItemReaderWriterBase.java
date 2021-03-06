/*
 * Copyright (c) 2014 Red Hat, Inc. and/or its affiliates.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Cheng Fang - Initial API and implementation
 */

package org.jberet.support.io;

import javax.batch.api.BatchProperty;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for {@link org.jberet.support.io.JsonItemReader} and {@link org.jberet.support.io.JsonItemWriter}.
 * It also holds Json-related common batch properties for customizing Json processing.
 */
public abstract class JsonItemReaderWriterBase extends ItemReaderWriterBase {

    @Inject
    @BatchProperty
    protected String jsonFactoryFeatures;

    @Inject
    @BatchProperty
    protected String mapperFeatures;

    @Inject
    @BatchProperty
    protected String jsonFactoryLookup;

    protected JsonFactory jsonFactory;
    protected ObjectMapper objectMapper;

    /**
     * Registers any {@code com.fasterxml.jackson.databind.module.SimpleModule} to the {@link #objectMapper}. Any number
     * of custom serializers or deserializers can be added to the module.
     */
    protected abstract void registerModule() throws Exception;

    /**
     * Initializes {@link #jsonFactory} and {@link #objectMapper} fields, which may be instantiated or obtained from
     * other part of the application. This method also configures the {@link #jsonFactory} and {@link #objectMapper}
     * properly based on the current batch artifact properties.
     */
    protected void initJsonFactoryAndObjectMapper() throws NamingException {
        if (jsonFactoryLookup != null) {
            jsonFactory = InitialContext.doLookup(jsonFactoryLookup);
        } else {
            jsonFactory = new MappingJsonFactory();
        }
        objectMapper = (ObjectMapper) jsonFactory.getCodec();
        if (jsonFactoryFeatures != null) {
            NoMappingJsonFactoryObjectFactory.configureJsonFactoryFeatures(jsonFactory, jsonFactoryFeatures);
        }
        if (mapperFeatures != null) {
            MappingJsonFactoryObjectFactory.configureMapperFeatures(objectMapper, mapperFeatures);
        }
    }
}
