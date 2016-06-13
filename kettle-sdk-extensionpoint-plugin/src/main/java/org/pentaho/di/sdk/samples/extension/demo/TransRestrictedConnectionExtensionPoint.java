/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

package org.pentaho.di.sdk.samples.extension.demo;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleTransException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMetaDataCombi;

@ExtensionPoint (
  id = "PDISDKTransRestrictedConnections",
  extensionPointId = "TransformationPrepareExecution",
  description = "Prevents a transformation from executing if it uses a restricted Database Connection" )
public class TransRestrictedConnectionExtensionPoint implements ExtensionPointInterface {

  private static final Class<?> PKG = TransRestrictedConnectionExtensionPoint.class;
  private static final List<String> RESTRICTED_DATABASES = Arrays.asList( "PDISDK_RestrictedConnection" );

  private static boolean containsIgnoreCase( List<String> list, String searchParameter ) {
    for ( String listEntry : list ) {
      if ( listEntry.equalsIgnoreCase( searchParameter ) ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {
    if ( object instanceof Trans ) {
      Trans trans = (Trans) object;

      // Review used database connections in each step
      for ( StepMetaDataCombi smdCombi : trans.getSteps() ) {
        String stepName = smdCombi.stepname;
        DatabaseMeta[] databases = smdCombi.meta.getUsedDatabaseConnections();
        if ( databases != null ) {
          for ( int i = 0; i < databases.length; i++ ) {

            // If Database Connection name matches
            if ( containsIgnoreCase( RESTRICTED_DATABASES, databases[i].getName() ) ) {
              throw new KettleTransException(
                BaseMessages.getString( PKG, "TransRestrictedConnections.Error.UsedConnection", stepName ) );
            }
          }
        }
      }
    }
  }

}
