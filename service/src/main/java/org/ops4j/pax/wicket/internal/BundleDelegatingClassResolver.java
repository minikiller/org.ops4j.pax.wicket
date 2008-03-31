/*
 * Copyright 2006 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ops4j.pax.wicket.internal;

import java.util.HashSet;
import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class BundleDelegatingClassResolver extends ServiceTracker
    implements IClassResolver
{

    private static final String FILTER = "(|(objectClass=" + ContentSource.class.getName() + ")" +
                                         "(objectClass=" + ContentAggregator.class.getName() + ")" +
                                         "(objectClass=" + PageFactory.class.getName() + "))";

    private HashSet<Bundle> m_bundles;
    private String m_applicationName;

    public BundleDelegatingClassResolver( BundleContext context, String applicationName, Bundle paxWicketbundle )
    {
        super( context, FILTER, null );
        m_applicationName = applicationName;
        m_bundles = new HashSet<Bundle>();
        m_bundles.add( paxWicketbundle );
        open( true );
    }

    public Class resolveClass( String classname )
        throws ClassNotFoundException
    {
        for( Bundle bundle : m_bundles )
        {
            try
            {
                return bundle.loadClass( classname );
            } catch( ClassNotFoundException e )
            {
                // ignore, expected in many cases.
            } catch( IllegalStateException e)
            {
                // if the bundle has been uninstalled.
            }
        }
        return null;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        String appName = (String) serviceReference.getProperty( ContentSource.APPLICATION_NAME );
        if( !m_applicationName.equals( appName ) )
        {
            return null;
        }
        synchronized( this )
        {
            Bundle bundle = serviceReference.getBundle();
            HashSet<Bundle> clone = (HashSet<Bundle>) m_bundles.clone();
            clone.add( bundle );
            m_bundles = clone;
        }
        return super.addingService( serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object o )
    {
        String appName = (String) serviceReference.getProperty( ContentSource.APPLICATION_NAME );
        if( !m_applicationName.equals( appName ) )
        {
            return;
        }
        HashSet<Bundle> revisedSet = new HashSet<Bundle>();
        try
        {
            ServiceReference[] serviceReferences = context.getAllServiceReferences( null, FILTER );
            for( ServiceReference ref : serviceReferences )
            {
                revisedSet.add( ref.getBundle() );
            }
            m_bundles = revisedSet;
        } catch( InvalidSyntaxException e )
        {
            // Can not happen.
        }
        super.removedService( serviceReference, o );
    }
}
