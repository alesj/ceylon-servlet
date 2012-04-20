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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ceylon servlet delegate API.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface CeylonDelegate {
    void init(ServletConfig config) throws ServletException;

    boolean service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    void destroy();
}
