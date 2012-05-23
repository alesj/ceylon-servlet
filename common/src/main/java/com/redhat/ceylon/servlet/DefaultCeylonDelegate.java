/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted
 * by the author tags.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.ceylon.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ceylon.modules.Configuration;
import ceylon.modules.spi.Argument;
import ceylon.modules.spi.ArgumentType;

/**
 * Default Ceylon delegate.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultCeylonDelegate implements CeylonDelegate {
    static final String MAIN_MODULE = "ceylon-main-module";
    static final String MAIN_RUNNABLE = "ceylon-main-runnable";
    static final String CEYLON_REPO = "ceylon.repo";
    static final String CEYLON_RUNTIME_REPO = "ceylon-repository";

    private ServletRuntime runtime;

    public void init(ServletConfig config) throws ServletException {
        try {
            Configuration configuration = new Configuration();
            configuration.module = config.getInitParameter(MAIN_MODULE);
            configuration.run = config.getInitParameter(MAIN_RUNNABLE);
            String ceylonRuntimeRepo = config.getInitParameter(CEYLON_RUNTIME_REPO);
            ceylonRuntimeRepo = XmlVariableReplace.repalceVar(ceylonRuntimeRepo);
            if (ceylonRuntimeRepo == null) {
                ceylonRuntimeRepo = new File(System.getProperty("jboss.home.dir"), "ceylon-repo").toURI().toString();
            }
            configuration.setArgument(Argument.REPOSITORY.toString(), ArgumentType.CEYLON, new String[]{ceylonRuntimeRepo}, -1);

            String ceylonRepo = config.getInitParameter(CEYLON_REPO);
            ceylonRepo = XmlVariableReplace.repalceVar(ceylonRepo);

            if (ceylonRepo != null) {
                System.setProperty(CEYLON_REPO, ceylonRepo);
            }

            runtime = new ServletRuntime();
            runtime.execute(configuration);

            runtime.init(config);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public boolean service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        return runtime.service(req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doGet(req, resp);
    }

    public void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doHead(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doPost(req, resp);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doPut(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doDelete(req, resp);
    }

    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doOptions(req, resp);
    }

    public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        runtime.doTrace(req, resp);
    }

    public void destroy() {
        runtime.destroy();
    }
}
