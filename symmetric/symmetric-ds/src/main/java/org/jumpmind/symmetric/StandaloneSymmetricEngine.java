/*
 * Licensed to JumpMind Inc under one or more contributor 
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding 
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU Lesser General Public License (the
 * "License"); you may not use this file except in compliance
 * with the License. 
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see           
 * <http://www.gnu.org/licenses/>.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.  */


package org.jumpmind.symmetric;

import java.util.Properties;

import org.springframework.context.ApplicationContext;

/**
 * This is the preferred way to create, configure, start and manage a
 * client-only instance of SymmetricDS. The engine will bootstrap the
 * symmetric.xml Spring context.
 * <p/>
 * The SymmetricDS instance is configured by properties configuration files. By
 * default the engine will look for and override existing properties with ones
 * found in the properties files. SymmetricDS looks for: symmetric.properties in
 * the classpath (it will use the first one it finds), and then for a
 * symmetric.properties found in the user.home system property location. Next,
 * if provided, in the constructor of the SymmetricEngine, it will locate and
 * use the properties file passed to the engine.
 * <p/>
 * When the engine is ready to be started, the {@link #start()} method should be
 * called. It should only be called once.
 *
 * @author Chris Henson <chenson42@users.sourceforge.net>
 */
public class StandaloneSymmetricEngine extends AbstractSymmetricEngine {

    public StandaloneSymmetricEngine() {
        this(null, false, null, null, null);
    }

    public StandaloneSymmetricEngine(Properties overrideProperties) {
        this(null, false, overrideProperties, null, null);
    }

    public StandaloneSymmetricEngine(String overridePropertiesResource) {
        this(null, false, null, overridePropertiesResource, null);
    }

    public StandaloneSymmetricEngine(String overridePropertiesResource1, String overridePropertiesResource2) {
        this(null, false, null, overridePropertiesResource1, overridePropertiesResource2);
    }

    public StandaloneSymmetricEngine(ApplicationContext parentContext, boolean isParentContext) {
        this(parentContext, isParentContext, null, null, null);
    }

    public StandaloneSymmetricEngine(ApplicationContext parentContext, boolean isParentContext,
            String overridePropertiesResource) {
        this(parentContext, isParentContext, null, overridePropertiesResource, null);
    }

    /**
     * Create a SymmetricDS instance using an existing
     * {@link ApplicationContext} as the parent. This gives the SymmetricDS
     * context access to beans in the parent context.
     */
    public StandaloneSymmetricEngine(ApplicationContext parentContext, boolean isParentContext,
            Properties overrideProperties, String overridePropertiesResource1, String overridePropertiesResource2) {
        init(parentContext, isParentContext, overrideProperties, overridePropertiesResource1,
                overridePropertiesResource2);
    }
}