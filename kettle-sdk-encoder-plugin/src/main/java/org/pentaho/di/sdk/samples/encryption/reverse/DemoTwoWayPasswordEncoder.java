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

package org.pentaho.di.sdk.samples.encryption.reverse;

import org.pentaho.di.core.encryption.TwoWayPasswordEncoderInterface;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPlugin;
import org.pentaho.di.core.exception.KettleException;

@TwoWayPasswordEncoderPlugin( id = "REVERSE", name = "Reverse" )
public class DemoTwoWayPasswordEncoder implements TwoWayPasswordEncoderInterface {

  public static final String PASSWORD_ENCRYPTED_PREFIX = "Reversed ";

  @Override
  public void init() throws KettleException {
    // Nothing to do here
  }

  @Override
  public String encode( String password ) {
    return encode( password, true );
  }

  @Override
  public String encode( String password, boolean includePrefix ) {
    if ( password == null ) {
      password = "";
    }
    return PASSWORD_ENCRYPTED_PREFIX + new StringBuilder( password ).reverse().toString();
  }

  @Override
  public String decode( String encodedPassword, boolean optionallyEncrypted ) {
    if ( encodedPassword == null ) {
      return "";
    }
    if ( encodedPassword.startsWith( PASSWORD_ENCRYPTED_PREFIX ) ) {
      return decode( encodedPassword.substring( PASSWORD_ENCRYPTED_PREFIX.length() ) );
    } else {
      return decode( encodedPassword );
    }
  }

  @Override
  public String decode( String encodedPassword ) {
    if ( encodedPassword == null ) {
      return "";
    }
    return new StringBuilder( encodedPassword ).reverse().toString();
  }

  @Override
  public String[] getPrefixes() {
    return new String[]{ PASSWORD_ENCRYPTED_PREFIX };
  }
}
