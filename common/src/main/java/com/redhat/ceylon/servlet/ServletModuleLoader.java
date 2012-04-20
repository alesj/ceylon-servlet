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

import ceylon.modules.jboss.runtime.CeylonModuleLoader;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;

/**
 * Servlet extension to Ceylon module loader.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class ServletModuleLoader extends CeylonModuleLoader {
    private static final ModuleIdentifier SERVLET = ModuleIdentifier.create("javax.servlet.api");

    ServletModuleLoader(RepositoryManager repository) {
        super(repository);
    }

    protected Module preloadModule(ModuleIdentifier mi) throws ModuleLoadException {
        if (SERVLET.equals(mi))
            return org.jboss.modules.Module.getBootModuleLoader().loadModule(mi);

        return super.preloadModule(mi);
    }
}
