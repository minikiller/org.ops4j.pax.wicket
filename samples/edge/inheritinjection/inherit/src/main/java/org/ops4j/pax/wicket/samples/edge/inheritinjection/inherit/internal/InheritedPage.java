/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.edge.inheritinjection.inherit.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.basic.Label;
import org.ops4j.pax.wicket.samples.edge.inheritinjection.parent.Parent;

/**
 * Very simple page providing entry points into various other examples.
 */
public class InheritedPage extends Parent {

    private static final long serialVersionUID = 1L;

    @Named("injectedBean")
    @Inject
    private InternalBean      injectedBean;

    public InheritedPage() {
        add(new Label("text", injectedBean.getSomeText()));
    }
}
