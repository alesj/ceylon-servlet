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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ceylon.modules.Configuration;
import ceylon.modules.jboss.runtime.JBossRuntime;
import ceylon.modules.spi.Constants;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import org.jboss.modules.ModuleLoader;

/**
 * Servlet runtime.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class ServletRuntime extends JBossRuntime implements CeylonDelegate {
    private Configuration configuration;
    private String moduleName;
    private ClassLoader classLoader;

    private Object runnable;
    private final Map<String, Object> cache = new HashMap<String, Object>();

    @Override
    protected ModuleLoader createModuleLoader(Configuration conf) {
        RepositoryManager repository = createRepository(conf);
        return new ServletModuleLoader(repository);
    }

    protected void execute(Configuration conf, String name, ClassLoader cl) throws Exception {
        this.configuration = conf;
        this.moduleName = name;
        this.classLoader = cl;
    }

    protected Object findCached(String methodName, Class... parameterTypes) throws ServletException {
        Object cached = cache.get(methodName);
        if (cached == null) {
            synchronized (cache) {
                cached = cache.get(methodName);
                if (cached == null) {
                    Class<?> current = runnable.getClass();
                    while (current != null) {
                        try {
                            cached = current.getDeclaredMethod(methodName, parameterTypes);
                            cache.put(methodName, cached);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            current = current.getSuperclass();
                        }
                    }
                    if (current == null) {
                        cache.put(methodName, this);
                    }
                }
            }
        }
        return cached;
    }

    protected Method findMethod(String methodName, Class... parameterTypes) throws ServletException {
        Object cached = findCached(methodName, parameterTypes);
        return (cached instanceof Method) ? Method.class.cast(cached) : null;
    }

    protected Method findServiceMethod(String methodName) throws ServletException {
        return findMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
    }

    protected boolean invoke(String methodName, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Method method = findServiceMethod(methodName);
        if (method != null) {
            try {
                method.invoke(runnable, req, resp);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        return (method != null);
    }

    public synchronized void init(ServletConfig config) throws ServletException {
        String runClassName = configuration.run;
        if (runClassName == null || runClassName.isEmpty()) {
            // "default" is not a package name
            if (moduleName.equals(Constants.DEFAULT.toString()))
                runClassName = RUN_INFO_CLASS;
            else
                runClassName = moduleName + "." + RUN_INFO_CLASS;
        }
        try {
            Class<?> runnableClass = classLoader.loadClass(runClassName);
            runnable = runnableClass.newInstance();
            Method init = findMethod("init", ServletConfig.class);
            if (init != null) {
                init.invoke(runnable, config);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public boolean service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        return invoke("service", req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doGet", req, resp);
    }

    public void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doHead", req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doPost", req, resp);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doPut", req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doDelete", req, resp);
    }

    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doOptions", req, resp);
    }

    public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("doTrace", req, resp);
    }

    public void destroy() {
        try {
            Method destroy = findMethod("destroy");
            if (destroy != null) {
                destroy.invoke(runnable);
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).warning("Exception while destroying runnable: " + e.getMessage());
        }
    }
}
